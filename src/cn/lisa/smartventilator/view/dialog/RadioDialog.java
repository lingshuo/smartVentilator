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
	// ����
	private List<Radio> radios;
	// ������
	private RadioListAdapter mradioListAdapter = null;
	// Ŀǰ����
	private int currentRadioPlayItem;
	// ����״̬
	private Map<Integer, Boolean> mPlayStatus;
	// ��̨������
	private MediaPlayer mMediaPlayer;
	private static int mTheme = R.style.CustomDialog;
	// ��̨����handler
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
		// ��̨�ؼ�
		mRadioList = (ListView) findViewById(R.id.lv_radio);
		initData();
	}

	/***
	 * ��ʼ������
	 */
	private void initData() {
		// ��ȡ��̨�б�
		radios = RadioManager.getRadioList(mContext);
		// ��ʼ������״̬
		for (int i = 0; i < radios.size(); i++)
			mPlayStatus.put(i, false);
		// ��adapter
		mradioListAdapter = new RadioListAdapter(mContext, handler,
				mPlayStatus, (ArrayList<Radio>) radios);
		mRadioList.setAdapter(mradioListAdapter);
	}

	/**
	 * ���ŵ�̨��Ƶ
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
						// Toast.makeText(mActivity, "��ȷ����Ҫ���ŵĵ�ַ��Ϊ�գ�",
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
					mMediaPlayer.setVideoQuality(MediaPlayer.VIDEOQUALITY_LOW);// ���ò��ŵ�����
					mMediaPlayer.setOnInfoListener(RadioDialog.this);// ע��һ���ص����������о���������Ϣʱ���á����磺��ʼ���塢��������������ٶȱ仯
					mMediaPlayer.setOnBufferingUpdateListener(RadioDialog.this);// ע��һ���ص�������������������仯ʱ����
					mMediaPlayer.setOnPreparedListener(RadioDialog.this);// ע��һ���ص���������Ԥ������ɺ����
					// Metadata mMetadata = mMediaPlayer.getMetadata();//
					// ��ȡԪ���ݱ���
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
	 * ֹͣ���ŵ�̨
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
	 * ֹͣ���Ų��ͷ���Դ
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
