package cn.lisa.smartventilator.controller.adapter;

import java.util.ArrayList;
import java.util.Map;

import cn.lisa.smartventilator.R;
import cn.lisa.smartventilator.controller.entity.*;
import cn.lisa.smartventilator.controller.manager.RadioManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class RadioListAdapter extends BaseAdapter {

	private ArrayList<Radio> mRadioList;
	// 记录当前播放状态
	private Map<Integer, Boolean> playStaus;
	LayoutInflater infater;

	private Handler mHandler;
	public final static String BUNDLE_KEY = "radio";

	public RadioListAdapter(Context context, Handler handler, Map<Integer, Boolean> playStaus, ArrayList<Radio> Radios) {
		mHandler = handler;
		infater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.playStaus = playStaus;
		this.mRadioList = new ArrayList<Radio>();
		for (Radio r : Radios) {
			this.mRadioList.add(r);
		}

	}

	public void refresh(Map<Integer, Boolean> playStaus) {
		this.playStaus = playStaus;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mRadioList.size();
	}

	@Override
	public Object getItem(int position) {
		return mRadioList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint({ "ViewHolder", "InflateParams" })
	@Override
	public View getView(int position, View convertview, ViewGroup parent) {
		View view = infater.inflate(R.layout.media_radio_item, null);

		TextView radio_name = (TextView) view.findViewById(R.id.radio_name);
		// radio_name.setOnClickListener(new
		// OnItemChildClickListener(CLICK_BUTTON_PLAY,position));
		radio_name.setText(mRadioList.get(position).getName());
		// radio_name.setTag(CLICK_BUTTON_PLAY);

		ImageButton btn_play = (ImageButton) view.findViewById(R.id.btn_play);
		btn_play.setOnClickListener(new OnItemChildClickListener(RadioManager.CLICK_BUTTON_PLAY,
				position));
		btn_play.setTag(RadioManager.CLICK_BUTTON_PLAY);

		ImageButton btn_stop = (ImageButton) view.findViewById(R.id.btn_stop);
		btn_stop.setOnClickListener(new OnItemChildClickListener(RadioManager.CLICK_BUTTON_STOP,
				position));
		btn_stop.setTag(RadioManager.CLICK_BUTTON_STOP);

		if (playStaus.get(position)) {
			btn_play.setVisibility(View.INVISIBLE);
			btn_stop.setVisibility(View.VISIBLE);
		} else {
			btn_play.setVisibility(View.VISIBLE);
			btn_stop.setVisibility(View.INVISIBLE);
		}
		return view;
	}

	private class OnItemChildClickListener implements View.OnClickListener {

		// 点击类型索引，对应前面的CLICK_BUTTON_xxx
		private int clickIndex;
		// 点击列表位置
		private int position;

		public OnItemChildClickListener(int clickIndex, int position) {
			this.clickIndex = clickIndex;
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			View vp = (View) v.getParent().getParent();
			TextView tv = (TextView) vp.findViewById(R.id.radio_name);
			tv.getText().toString();
			// 创建Message并填充数据，通过mHandle联系Activity接收处理
			if (v.getTag().equals(RadioManager.CLICK_BUTTON_PLAY)) {
				v.setVisibility(View.INVISIBLE);
				ImageButton ib = (ImageButton) vp.findViewById(R.id.btn_stop);
				ib.setVisibility(View.VISIBLE);
				for (int i = 0; i < playStaus.size(); i++) {
					if (i == position)
						playStaus.put(i, true);
					else
						playStaus.put(i, false);
				}

			} else if (v.getTag().equals(RadioManager.CLICK_BUTTON_STOP)) {
				v.setVisibility(View.INVISIBLE);
				ImageButton ib = (ImageButton) vp.findViewById(R.id.btn_play);
				ib.setVisibility(View.VISIBLE);
				for (int i = 0; i < playStaus.size(); i++) {
					if (i == position)
						playStaus.put(i, false);
					else
						playStaus.put(i, true);
				}
			}

			Message msg = new Message();
			msg.what = clickIndex;
			msg.arg1 = position;
			msg.obj = playStaus;
			Bundle b = new Bundle();
			b.putSerializable(BUNDLE_KEY, mRadioList.get(position));
			msg.setData(b);
			mHandler.sendMessage(msg);
		}

	}

}
