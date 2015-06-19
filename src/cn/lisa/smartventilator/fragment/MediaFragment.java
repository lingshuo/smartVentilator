package cn.lisa.smartventilator.fragment;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import cn.lisa.smartventilator.R;
import cn.lisa.smartventilator.adapter.RadioListAdapter;
import cn.lisa.smartventilator.adapter.VideoListAdapter;
import cn.lisa.smartventilator.bean.Radio;
import cn.lisa.smartventilator.bean.Video;
import cn.lisa.smartventilator.util.RadioUtil;
import cn.lisa.smartventilator.util.VideoUtil;

public class MediaFragment extends Fragment implements OnClickListener,OnItemClickListener,
		OnBufferingUpdateListener, OnPreparedListener, OnInfoListener {
	
	//�ؼ�
	private ImageView mPlayBtn;
	private ListView mVideoList;
	private ListView mRadioList;
	
	//����
	private List<Video> videos;
	private List<Radio> radios;
	
	//������
	private VideoListAdapter mVideoListAdapter = null;
	private RadioListAdapter mradioListAdapter = null;

	// Ŀǰ����
	private int currentRadioPlayItem;
	// ����״̬
	private Map<Integer, Boolean> mPlayStatus;
	// ��̨������
	private MediaPlayer mMediaPlayer;
	//��������
	private PackageManager packageManager;
	
	// ��̨����handler
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Radio data = (Radio) msg.getData().getSerializable(
					RadioListAdapter.BUNDLE_KEY);

			switch (msg.what) {
			
			case VideoUtil.GET_VIDEO_FINISH:
				List<Video> videoInfoList = (List<Video>) msg.obj;
				mVideoListAdapter = new VideoListAdapter(getActivity(),
						videoInfoList);
				mVideoList.setAdapter(mVideoListAdapter);
				
				break;	
			case RadioUtil.CLICK_BUTTON_PLAY:
				Radio r = (Radio) data;
				Log.e("sv", "start " + r.getUrl());
				mPlayStatus = (Map<Integer, Boolean>) msg.obj;
				playRadio(r.getUrl());
				currentRadioPlayItem = msg.arg1;
				mradioListAdapter.refresh(mPlayStatus);
				
				break;
			case RadioUtil.CLICK_BUTTON_STOP:
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Context context = getActivity();
		mPlayStatus = new HashMap<Integer, Boolean>();
		this.packageManager = context.getPackageManager();

		View view = inflater.inflate(R.layout.fragment_media, null);
		initview(view);
		initListener();
		initData();
		return view;
	}
	
	/***
	 * ��ʼ������
	 * @param view
	 */
	private void initview(View view){
		//���Ű�ť
		mPlayBtn = (ImageView) view.findViewById(R.id.media_play_button);
		// Video
		mVideoList = (ListView) view.findViewById(R.id.lv_video);
		//��̨�ؼ�
		mRadioList = (ListView) view.findViewById(R.id.lv_radio);
	}
	
	/***
	 * ��ʼ��������
	 */
	private void initListener(){
		mPlayBtn.setOnClickListener(this);
		mVideoList.setOnItemClickListener(this);
	}
	
	/***
	 * ��ʼ������
	 */
	private void initData(){
		//��ȡ��Ƶ�б�
		videos=VideoUtil.getVideoList(handler);
		//��ȡ��̨�б�
		radios = RadioUtil.getRadioList(this);
		//��ʼ������״̬
		for (int i = 0; i < radios.size(); i++)
			mPlayStatus.put(i, false);
		//��adapter
		mradioListAdapter = new RadioListAdapter(getActivity(), handler,
				mPlayStatus, (ArrayList<Radio>) radios);
		mRadioList.setAdapter(mradioListAdapter);
	}
	
	/**
	 * ����¼�
	 */
	@Override
	public void onClick(View v) {
		switch(v.getId()){		
		case R.id.media_play_button:
			Intent intent = packageManager
					.getLaunchIntentForPackage("com.youku.phone");
			if (intent == null) {
				String str = "http://dl.m.cc.youku.com/android/phone/Youku_Phone_youkuweb.apk";
				Intent localIntent = new Intent(
						"android.intent.action.VIEW");
				localIntent.setData(Uri.parse(str));
				startActivity(localIntent);
			} else {
				stopRadio();
				startActivity(intent);
			}
			break;		
			
		default:
			break;
		}
	}
	
	/**
	 * �б����¼�
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch(parent.getId()){
		case R.id.lv_video:
			stopRadio();
			playVideo(position);
			break;
			
		default:
			break;
		}
	}
	
	/***
	 * ������Ƶ
	 * @param position
	 */
	private void playVideo(int position){
		Intent intent=VideoUtil.getVideoIntent(videos.get(position).getPath());
		startActivity(intent);
	}
	
	/**
	 * ���ŵ�̨��Ƶ
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
					Activity mActivity = getActivity();
					if (url == "") {
						// Tell the user to provide an audio file URL.
						Toast.makeText(mActivity, "��ȷ����Ҫ���ŵĵ�ַ��Ϊ�գ�",
								Toast.LENGTH_LONG).show();
						return;
					}
					// Create a new media player and set the listeners
					if (mMediaPlayer != null) {
						mMediaPlayer.release();
						mMediaPlayer = null;
					}
					mMediaPlayer = new MediaPlayer(mActivity);
					mMediaPlayer.setDataSource(url);
					mMediaPlayer.prepare();
					mMediaPlayer.setVideoQuality(MediaPlayer.VIDEOQUALITY_LOW);// ���ò��ŵ�����
					mMediaPlayer.setOnInfoListener(MediaFragment.this);// ע��һ���ص����������о���������Ϣʱ���á����磺��ʼ���塢��������������ٶȱ仯
					mMediaPlayer
							.setOnBufferingUpdateListener(MediaFragment.this);// ע��һ���ص�������������������仯ʱ����
					mMediaPlayer.setOnPreparedListener(MediaFragment.this);// ע��һ���ص���������Ԥ������ɺ����
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
	private void stopRadio(){
		if (mMediaPlayer != null && mradioListAdapter != null) {
			mMediaPlayer.stop();
			for (int i = 0; i < mPlayStatus.size(); i++)
				mPlayStatus.put(i, false);
			mradioListAdapter.refresh(mPlayStatus);
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
	public void onDestroy() {
		super.onDestroy();
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
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
