package cn.lisa.smartventilator.utility.system;

import android.app.Activity;
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.WindowManager;

public class BrightnessUtil {
	/** * 判断是否开启了自动亮度调节 */

	public static boolean isAutoBrightness(ContentResolver aContentResolver) {

		boolean automicBrightness = false;

		try {

			automicBrightness = Settings.System.getInt(aContentResolver,

			Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;

		}

		catch (SettingNotFoundException e)

		{

			e.printStackTrace();

		}

		return automicBrightness;
	}

	/** * 获取屏幕的亮度 */

	public static int getScreenBrightness(Activity activity) {

		int nowBrightnessValue = 0;

		ContentResolver resolver = activity.getContentResolver();

		try {

			nowBrightnessValue = android.provider.Settings.System.getInt(resolver,
					Settings.System.SCREEN_BRIGHTNESS);

		}

		catch (Exception e) {

			e.printStackTrace();

		}

		return nowBrightnessValue;
	}

	/** * 设置亮度 */

	public static void setBrightness(Activity activity, int brightness) {

		// Settings.System.putInt(activity.getContentResolver(),

		// Settings.System.SCREEN_BRIGHTNESS_MODE,

		// Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);

		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();

		lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
		Log.d("lxy", "set  lp.screenBrightness == " + lp.screenBrightness);

		activity.getWindow().setAttributes(lp);
	}

	/** * 停止自动亮度调节 */

	public static void stopAutoBrightness(Activity activity) {

		Settings.System.putInt(activity.getContentResolver(),

		Settings.System.SCREEN_BRIGHTNESS_MODE,

		Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
	}

	/**
	 * * 开启亮度自动调节 *
	 * 
	 * @param activity
	 */

	public static void startAutoBrightness(Activity activity) {

		Settings.System.putInt(activity.getContentResolver(),

		Settings.System.SCREEN_BRIGHTNESS_MODE,

		Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);

	}

	/** * 保存亮度设置状态 */

	public static void saveBrightness(ContentResolver resolver, int brightness) {

		Uri uri = android.provider.Settings.System.getUriFor("screen_brightness");

		android.provider.Settings.System.putInt(resolver, "screen_brightness", brightness);

		// resolver.registerContentObserver(uri, true, myContentObserver);

		resolver.notifyChange(uri, null);
	}
}
