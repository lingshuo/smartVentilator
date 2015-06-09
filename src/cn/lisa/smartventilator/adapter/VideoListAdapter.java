package cn.lisa.smartventilator.adapter;

import java.util.List;

import cn.lisa.smartventilator.R;
import cn.lisa.smartventilator.bean.*;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class VideoListAdapter extends BaseAdapter {
	
	private List<VideoInfo> mVideoList;
	
	LayoutInflater infater;
	
	public VideoListAdapter(Context context,List<VideoInfo> videoInfos) {
		super();
		infater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mVideoList=videoInfos;
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

	@Override
	public View getView(int position, View convertview, ViewGroup parent) {
		View view = infater.inflate(R.layout.media_video_item, null);
		ImageView video_thumb=(ImageView) view.findViewById(R.id.video_thumb);
		TextView video_name=(TextView)view.findViewById(R.id.video_name);

		video_name.setText(mVideoList.get(position).getName());
		video_thumb.setImageBitmap(mVideoList.get(position).getThumb());
		
		return view;
	}
	
}
