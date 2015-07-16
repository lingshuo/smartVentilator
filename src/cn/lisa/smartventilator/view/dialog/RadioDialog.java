package cn.lisa.smartventilator.view.dialog;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lisa.smartventilator.R;
import cn.lisa.smartventilator.controller.adapter.RadioListAdapter;
import cn.lisa.smartventilator.controller.entity.Radio;
import cn.lisa.smartventilator.controller.manager.RadioManager;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;

public class RadioDialog extends Dialog implements OnBufferingUpdateListener,
		OnPreparedListener, OnInfoListener {
	private Context mContext;

	private ListView mRadioList;
	// 数据
	private List<Radio> radios;
	// 适配器
	private RadioListAdapter mradioListAdapter = null;
	// 目前播放
	private int currentRadioPlayItem;
	// 播放状态
	private Map<Integer, Boolean> mPlayStatus;
	// 电台播放器
	private MediaPlayer mMediaPlayer;
	private static int mTheme = R.style.CustomDialog;
	// 后台处理handler
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Radio data = (Radio) msg.getData().getSerializable(
					RadioListAdapter.BUNDLE_KEY);

			switch (msg.what) {

			case RadioManager.CLICK_BUTTON_PLAY:
				Radio r = (Radio) data;
				Log.e("sv", "start " + r.getUrl());
				mPlayStatus = (Map<Integer, Boolean>) msg.obj;
				playRadio(r.getUrl());
				currentRadioPlayItem = msg.arg1;
				mradioListAdapter.refresh(mPlayStatus);

				break;
			case RadioManager.CLICK_BUTTON_STOP:
				Radio r1 = (Radio) data;
				Log.e("sv", "stop " + r1.getUrl());
				mPlayStatus = (Map<Integer, Boolean>) msg.obj;
				if (mMediaPlayer.isPlaying()
						&& currentRadioPlayItem == r1.getId() - 1) {
					mMediaPlayer.pause();
				}
				for (int i = 0; i < mPlayStatus.size(); i++) {
					if (i == currentRadioPlayItem) {
						mPlayStatus.put(i, true);
					} else {
						mPlayStatus.put(i, false);
					}
				}
				break;
			default:
				break;
			}
		}

	};

	public RadioDialog(Context context) {
		super(context, mTheme);
		this.mContext = context;

	}

	@SuppressLint("UseSparseArrays")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_radio);
		mPlayStatus = new HashMap<Integer, Boolean>();
		// 电台控件
		mRadioList = (ListView) findViewById(R.id.lv_radio);
		initData();
	}

	/***
	 * 初始化数据
	 */
	private void initData() {
		// 获取电台列表
		radios = RadioManager.getRadioList(mContext);
		// 初始化播放状态
		for (int i = 0; i < radios.size(); i++)
			mPlayStatus.put(i, false);
		// 绑定adapter
		mradioListAdapter = new RadioListAdapter(mContext, handler,
				mPlayStatus, (ArrayList<Radio>) radios);
		mRadioList.setAdapter(mradioListAdapter);
	}

	/**
	 * 播放电台音频
	 * 
	 * @param url
	 */
	private void playRadio(final String url) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					/**
					 * Sets the data source (file-path or http/rtsp/mms URL) to
					 * use.
					 * 
					 * @param path
					 *            the path of the file, or the http/rtsp/mms URL
					 *            of the stream you want to play
					 */
					if (url == "") {
						// // Tell the user to provide an audio file URL.
						// Toast.makeText(mActivity, "请确定你要播放的地址不为空！",
						// Toast.LENGTH_LONG).show();
						return;
					}
					// Create a new media player and set the listeners
					if (mMediaPlayer != null) {
						mMediaPlayer.release();
						mMediaPlayer = null;
					}
					mMediaPlayer = new MediaPlayer(mContext);
					mMediaPlayer.setDataSource(url);
					mMediaPlayer.prepare();
					mMediaPlayer.setVideoQuality(MediaPlayer.VIDEOQUALITY_LOW);// 设置播放的质量
					mMediaPlayer.setOnInfoListener(RadioDialog.this);// 注册一个回调函数，在有警告或错误信息时调用。例如：开始缓冲、缓冲结束、下载速度变化
					mMediaPlayer.setOnBufferingUpdateListener(RadioDialog.this);// 注册一个回调函数，在网络流缓冲变化时调用
					mMediaPlayer.setOnPreparedListener(RadioDialog.this);// 注册一个回调函数，在预处理完成后调用
					// Metadata mMetadata = mMediaPlayer.getMetadata();//
					// 获取元数据编码
					// // mMediaPlayer.start();
					// System.out.println(mMetadata.getString(Metadata.ARTIST)
					// + mMetadata.getString(Metadata.BIT_RATE));
				} catch (Exception e) {
					Log.e("sv", "error: " + e.getMessage(), e);
				}

			}
		}).start();

	}

	/**
	 * 停止播放电台
	 */
	public void stopRadio() {
		if (mMediaPlayer != null && mradioListAdapter != null) {
			mMediaPlayer.stop();
			for (int i = 0; i < mPlayStatus.size(); i++)
				mPlayStatus.put(i, false);
			mradioListAdapter.refresh(mPlayStatus);
		}
	}

	/**
	 * 停止播放并释放资源
	 */
	public void release() {
		stopRadio();
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mMediaPlayer.start();
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		return false;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		release();
	}

}
