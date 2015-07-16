package cn.lisa.smartventilator.view.dialog;

import java.util.List;

import cn.lisa.smartventilator.R;
import cn.lisa.smartventilator.controller.adapter.VideoListAdapter;
import cn.lisa.smartventilator.controller.entity.Video;
import cn.lisa.smartventilator.controller.manager.VideoManager;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class VideoDialog extends Dialog implements OnItemClickListener {

	private ListView mVideoList;
	private RelativeLayout mBlankLayout;
	private Context mContext;
	private VideoListAdapter mVideoListAdapter = null;
	private List<Video> videos;
	private static int mTheme = R.style.CustomDialog;
	// 后台处理handler
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {

			case VideoManager.GET_VIDEO_FINISH:
				@SuppressWarnings("unchecked")
				List<Video> videoInfoList = (List<Video>) msg.obj;
				if (videoInfoList.size() > 0) {
					mVideoListAdapter = new VideoListAdapter(mContext,
							videoInfoList);
					mVideoList.setAdapter(mVideoListAdapter);
					mBlankLayout.setVisibility(View.GONE);
				} else {
					mVideoList.setVisibility(View.GONE);
					mBlankLayout.setVisibility(View.VISIBLE);
				}

				break;
			default:
				break;
			}
		}

	};

	public VideoDialog(Context context) {
		super(context, mTheme);
		this.mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_video);
		mVideoList = (ListView) findViewById(R.id.lv_video);
		mBlankLayout = (RelativeLayout) findViewById(R.id.video_blank_layout);
		mVideoList.setOnItemClickListener(this);
		// 获取视频列表
		videos = VideoManager.getVideoList(handler);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (parent.getId()) {
		case R.id.lv_video:
			// stopRadio();
			playVideo(position);
			break;

		default:
			break;
		}
	}

	/***
	 * 播放视频
	 * 
	 * @param position
	 */
	private void playVideo(int position) {
		try {
			Intent intent = VideoManager.getVideoIntent(videos.get(position)
					.getPath());
			mContext.startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(mContext, R.string.str_media_no_player,
					Toast.LENGTH_SHORT).show();
		}

	}

}
