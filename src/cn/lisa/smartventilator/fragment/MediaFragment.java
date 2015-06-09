package cn.lisa.smartventilator.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.lisa.smartventilator.R;
import cn.lisa.smartventilator.adapter.VideoListAdapter;
import cn.lisa.smartventilator.bean.VideoInfo;
import android.app.Fragment;
import android.content.ComponentName;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

public class MediaFragment extends Fragment implements OnItemClickListener {
	private ImageView mPlayBtn;
	private VideoListAdapter mVideoListAdapter = null;
	private List<VideoInfo> videoInfoList;
	private ListView mVideoList;
	// path of video
	private String cur_path = "/sdcard/smartVentilator/";

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);

			if (msg.what == 0) {
				List<VideoInfo> videoInfoList = (List<VideoInfo>) msg.obj;
				mVideoListAdapter = new VideoListAdapter(getActivity(),
						videoInfoList);
				mVideoList.setAdapter(mVideoListAdapter);
			}
		}

	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Context context = getActivity();
		final PackageManager packageManager = context.getPackageManager();

		View view = inflater.inflate(R.layout.fragment_media, null);
//		videoInfoList = getVideos();

		mVideoList = (ListView) view.findViewById(R.id.lv_video);

		mVideoList.setOnItemClickListener(this);
		mPlayBtn = (ImageView) view.findViewById(R.id.media_play_button);
		mPlayBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = packageManager
						.getLaunchIntentForPackage("com.youku.phone");
				if (intent == null) {
					System.out.println("APP not found!");
				} else {
					startActivity(intent);
				}
			}
		});
		loadVaule();
		return view;
	}

	@Override
	public void onDestroy() {
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

	private void loadVaule() {
		File file = new File(cur_path);
		if(!file.exists()){
			try{
				file.mkdirs();
				file = new File(cur_path);
			}catch(Exception e){
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		playVideo(videoInfoList.get(position).getPath());
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
	
	public String getFileName(String pathandname) {
		int start = pathandname.lastIndexOf("/");
		int end = pathandname.lastIndexOf(".");
		if (start != -1 && end != -1) {
			return pathandname.substring(start + 1, end);
		} else {
			return null;
		}
	}
}
