package cn.lisa.smartventilator.view;

import cn.lisa.smartventilator.R;
import cn.lisa.smartventilator.manager.MyWindowManager;
import cn.lisa.smartventilator.receiver.LockReceiver;
import cn.lisa.smartventilator.service.FloatWindowService;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


public class FloatWindowBigView extends LinearLayout {

	/**
     * 设备策略服务
     */
    private DevicePolicyManager dpm; 
	   
    private ComponentName componentName;
    
    Context context;
	/**
	 * 记录大悬浮窗的宽度
	 */
	public static int viewWidth;

	/**
	 * 记录大悬浮窗的高度
	 */
	public static int viewHeight;

	public FloatWindowBigView(final Context context) {
		super(context);
		this.context=context;
		dpm = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		LayoutInflater.from(context).inflate(R.layout.float_window_big, this);
		View view = findViewById(R.id.big_window_layout);
		viewWidth = view.getLayoutParams().width;
		viewHeight = view.getLayoutParams().height;
		Button lock=(Button)findViewById(R.id.lock);
		Button close = (Button) findViewById(R.id.close);
		Button back = (Button) findViewById(R.id.back);
		lock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openAdmin(v);
				lockscreen(v);
			}
		});
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 点击关闭悬浮窗的时候，移除所有悬浮窗，并停止Service
				MyWindowManager.removeBigWindow(context);
				MyWindowManager.removeSmallWindow(context);
				Intent intent = new Intent(getContext(), FloatWindowService.class);
				context.stopService(intent);
			}
		});
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 点击返回的时候，移除大悬浮窗，创建小悬浮窗
				MyWindowManager.removeBigWindow(context);
				MyWindowManager.createSmallWindow(context);
			}
		});
	}
	/**
     * 用代码去开启管理员
     */
    public void openAdmin(View view) {
        // 创建一个Intent
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        // 我要激活谁
        ComponentName componentName= new ComponentName(context,LockReceiver.class);
 
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,componentName);
        // 劝说用户开启管理员权限
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"开启一键锁屏");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
        context.startActivity(intent);
    }
    /**
     * 一键锁屏
     */
    public void lockscreen(View view) {
        ComponentName who = new ComponentName(context, LockReceiver.class);
        if (dpm.isAdminActive(who)) {
            dpm.lockNow();// 锁屏
//            dpm.resetPassword("", 0);// 设置屏蔽密码
 
            // 清除Sdcard上的数据
            // dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
            // 恢复出厂设置
            // dpm.wipeData(0);
        } else {
            Toast.makeText(context, "还没有打开管理员权限", 1).show();
            return;
        }
 
    }
}
