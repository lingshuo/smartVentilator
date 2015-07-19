package cn.lisa.smartventilator.view.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import cn.lisa.smartventilator.R;
import cn.lisa.smartventilator.view.dialog.RadioDialog;
import cn.lisa.smartventilator.view.dialog.VideoDialog;

public class MediaFragment extends Fragment implements OnClickListener {
	private Context context;
	// 控件
	private RelativeLayout mYoukuPlayLayout;
	private RelativeLayout mVideoPlayLayout;
	private RelativeLayout mRadioPlayLayout;

	private VideoDialog mVideoDialog;
	private RadioDialog mRadioDialog;
	// 包管理器
	private PackageManager packageManager;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		this.context = getActivity();

		this.packageManager = context.getPackageManager();

		View view = inflater.inflate(R.layout.fragment_media, null);
		initview(view);
		initListener();
		return view;
	}

	/***
	 * 初始化界面
	 * 
	 * @param view
	 */
	private void initview(View view) {
		// 播放按钮
		mYoukuPlayLayout = (RelativeLayout) view.findViewById(R.id.media_play_layout);
		// 视频
		mVideoPlayLayout = (RelativeLayout) view.findViewById(R.id.video_play_layout);
		// 广播
		mRadioPlayLayout = (RelativeLayout) view.findViewById(R.id.radio_play_layout);

		mVideoDialog = new VideoDialog(context);
		mRadioDialog = new RadioDialog(context);
	}

	/***
	 * 初始化监听器
	 */
	private void initListener() {
		mYoukuPlayLayout.setOnClickListener(this);
		mVideoPlayLayout.setOnClickListener(this);
		mRadioPlayLayout.setOnClickListener(this);
	}

	/**
	 * 点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.media_play_layout:
			mRadioDialog.stopRadio();
			Intent intent = packageManager.getLaunchIntentForPackage("com.youku.phone");
			if (intent == null) {
				String str = "http://dl.m.cc.youku.com/android/phone/Youku_Phone_youkuweb.apk";
				Intent localIntent = new Intent("android.intent.action.VIEW");
				localIntent.setData(Uri.parse(str));
				startActivity(localIntent);
			} else {
				// stopRadio();
				startActivity(intent);
			}
			break;
		case R.id.video_play_layout:
			mRadioDialog.dismiss();
			mRadioDialog.stopRadio();
			mVideoDialog.show();
			break;
		case R.id.radio_play_layout:
			mVideoDialog.dismiss();
			mRadioDialog.show();
			break;
		default:
			break;
		}
	}

	@Override
	public void onDestroy() {
		mRadioDialog.release();
		super.onDestroy();

	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

}
