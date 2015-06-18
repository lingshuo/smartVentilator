package cn.lisa.smartventilator.fragment;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lisa.smartventilator.R;
import cn.lisa.smartventilator.adapter.RadioListAdapter;
import cn.lisa.smartventilator.adapter.VideoListAdapter;
import cn.lisa.smartventilator.bean.Radio;
import cn.lisa.smartventilator.bean.VideoInfo;
import cn.lisa.smartventilator.util.RadioXmlParser;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MediaFragment extends Fragment implements
		OnBufferingUpdateListener, OnPreparedListener, OnInfoListener {
	private ImageView mPlayBtn;
	private VideoListAdapter mVideoListAdapter = null;
	private RadioListAdapter mradioListAdapter = null;
	private List<VideoInfo> videoInfoList;
	private List<Radio> radios;
	private ListView mVideoList;
	private ListView mRadioList;
	private boolean isAudioPlaying = true;
	private int currentRadioPlayItem;
	// 播放状态
	private Map<Integer, Boolean> mPlayStatus;
	// path of video
	private String cur_path = "/sdcard/smartVentilator/";

	private MediaPlayer mMediaPlayer;

	// 后台处理
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Radio data = (Radio) msg.getData().getSerializable(
					RadioListAdapter.BUNDLE_KEY);

			switch (msg.what) {
			case 0:
				List<VideoInfo> videoInfoList = (List<VideoInfo>) msg.obj;
				mVideoListAdapter = new VideoListAdapter(getActivity(),
						videoInfoList);
				mVideoList.setAdapter(mVideoListAdapter);
				break;
			case RadioListAdapter.CLICK_BUTTON_PLAY:
				Radio r = (Radio) data;
				Log.e("sv", "start " + r.getUrl());
				mPlayStatus = (Map<Integer, Boolean>) msg.obj;
				playRadio(r.getUrl());
				currentRadioPlayItem = msg.arg1;
				isAudioPlaying = true;
				mradioListAdapter.refresh(mPlayStatus);
				break;
			case RadioListAdapter.CLICK_BUTTON_STOP:
				Radio r1 = (Radio) data;
				Log.e("sv", "stop " + r1.getUrl());
				mPlayStatus = (Map<Integer, Boolean>) msg.obj;
				if (mMediaPlayer.isPlaying()
						&& currentRadioPlayItem == r1.getId() - 1) {
					mMediaPlayer.pause();
					isAudioPlaying = false;
				}
				for (int i = 0; i < mPlayStatus.size(); i++) {
					if (i == currentRadioPlayItem) {
						mPlayStatus.put(i, true);
					} else {
						mPlayStatus.put(i, false);
					}
				}
				break;
			}
		}

	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Context context = getActivity();
		mPlayStatus = new HashMap<Integer, Boolean>();
		final PackageManager packageManager = context.getPackageManager();

		View view = inflater.inflate(R.layout.fragment_media, null);
		// play button
		mPlayBtn = (ImageView) view.findViewById(R.id.media_play_button);
		mPlayBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = packageManager
						.getLaunchIntentForPackage("com.youku.phone");
				if (intent == null) {
					// System.out.println("APP not found!");
					String str = "market://details?id=com.youku.phone";
					Intent localIntent = new Intent(
							"android.intent.action.VIEW");
					localIntent.setData(Uri.parse(str));
					startActivity(localIntent);
				} else {
					startActivity(intent);
				}
			}
		});

		// Video
		mVideoList = (ListView) view.findViewById(R.id.lv_video);
		mVideoList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				playVideo(videoInfoList.get(position).getPath());
			}
		});
		// 获取视频
		loadVaule();

		// radio
		mRadioList = (ListView) view.findViewById(R.id.lv_radio);
		radios = getRadioList();
		// init play status
		for (int i = 0; i < radios.size(); i++)
			mPlayStatus.put(i, false);

		mradioListAdapter = new RadioListAdapter(getActivity(), handler,
				mPlayStatus, (ArrayList<Radio>) radios);
		mRadioList.setAdapter(mradioListAdapter);

		return view;
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

	// main function of get video from folder
	private void loadVaule() {
		File file = new File(cur_path);
		if (!file.exists()) {
			try {
				file.mkdirs();
				file = new File(cur_path);
			} catch (Exception e) {
				return;
			}
		}
		File[] files = null;
		files = file.listFiles();
		videoInfoList = new ArrayList<VideoInfo>();
		for (int i = 0; i < files.length; i++) {
			VideoInfo video = new VideoInfo();
			video.setName(getFileName(files[i].getPath()));
			video.setThumb(getVideoThumbnail(files[i].getPath(), 200, 200,
					MediaStore.Images.Thumbnails.MICRO_KIND));
			video.setPath(files[i].getPath());
			videoInfoList.add(video);

		}
		Message msg = new Message();
		msg.what = 0;
		msg.obj = videoInfoList;

		handler.sendMessage(msg);
	}

	// 获取视频的缩略图
	private Bitmap getVideoThumbnail(String videoPath, int width, int height,
			int kind) {
		Bitmap bitmap = null;
		// 获取视频的缩略图
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		// System.out.println("w"+bitmap.getWidth());
		// System.out.println("h"+bitmap.getHeight());
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	// 调用系统播放器 播放视频
	private void playVideo(String videoPath) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		String strend = "";
		if (videoPath.toLowerCase().endsWith(".mp4")) {
			strend = "mp4";
		} else if (videoPath.toLowerCase().endsWith(".3gp")) {
			strend = "3gp";
		} else if (videoPath.toLowerCase().endsWith(".mov")) {
			strend = "mov";
		} else if (videoPath.toLowerCase().endsWith(".wmv")) {
			strend = "wmv";
		}

		intent.setDataAndType(Uri.parse(videoPath), "video/" + strend);
		startActivity(intent);
	}

	// 从路径获取文件名
	public String getFileName(String pathandname) {
		int start = pathandname.lastIndexOf("/");
		int end = pathandname.lastIndexOf(".");
		if (start != -1 && end != -1) {
			return pathandname.substring(start + 1, end);
		} else {
			return null;
		}
	}

	// 获取Radio列表
	private List<Radio> getRadioList() {
		List<Radio> radiolist = null;
		try {
			InputStream is = getActivity().getAssets().open("radio.xml");
			RadioXmlParser parser = new RadioXmlParser();
			radiolist = parser.parse(is);
			// for (Radio book : radiolist) {
			// Log.i("sv", book.toString());
			// }
		} catch (Exception e) {
			Log.e("sv", e.getLocalizedMessage());
		}
		return radiolist;
	}

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
						Toast.makeText(mActivity, "请确定你要播放的地址不为空！",
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
					mMediaPlayer.setVideoQuality(MediaPlayer.VIDEOQUALITY_LOW);// 设置播放的质量
					mMediaPlayer.setOnInfoListener(MediaFragment.this);// 注册一个回调函数，在有警告或错误信息时调用。例如：开始缓冲、缓冲结束、下载速度变化
					mMediaPlayer
							.setOnBufferingUpdateListener(MediaFragment.this);// 注册一个回调函数，在网络流缓冲变化时调用
					mMediaPlayer.setOnPreparedListener(MediaFragment.this);// 注册一个回调函数，在预处理完成后调用
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

	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		// btn_play.setClickable(true);
		// txt_tip.setVisibility(View.GONE);
		mMediaPlayer.start();
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub
		// txt_tip.setVisibility(View.VISIBLE);
		// txt_tip.setText("正在缓冲"+percent+"%");
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

}
