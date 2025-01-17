package cn.lisa.smartventilator.controller.adapter;

import java.util.List;

import cn.lisa.smartventilator.R;
import cn.lisa.smartventilator.controller.entity.*;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class VideoListAdapter extends BaseAdapter {

	private List<Video> mVideoList;

	LayoutInflater infater;
	
	Handler mHandler;

	public VideoListAdapter(Context context, List<Video> videos,Handler handler) {
		super();
		infater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mVideoList = videos;
		mHandler=handler;
	}
	
	public void refresh(List<Video> videos){
		mVideoList=videos;
	}
	
	@Override
	public int getCount() {
		return mVideoList.size();
	}

	@Override
	public Object getItem(int position) {
		return mVideoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint({ "ViewHolder", "InflateParams" })
	@Override
	public View getView(int position, View convertview, ViewGroup parent) {
		View view = infater.inflate(R.layout.media_video_item, null);
		ImageView video_thumb = (ImageView) view.findViewById(R.id.video_thumb);
		TextView video_name = (TextView) view.findViewById(R.id.video_name);

		video_name.setText(mVideoList.get(position).getName());
		video_thumb.setImageBitmap(mVideoList.get(position).getThumb());

		return view;
	}

}
