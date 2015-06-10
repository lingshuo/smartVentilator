package cn.lisa.smartventilator.adapter;

import java.util.ArrayList;
import java.util.List;

import cn.lisa.smartventilator.R;
import cn.lisa.smartventilator.bean.*;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class RadioListAdapter extends BaseAdapter {
	
	private ArrayList<Radio> mRadioList;
	private Context context;
	LayoutInflater infater;
	public final static int CLICK_TEXT = 1;
	public final static int CLICK_BUTTON = 2;
	private Handler mHandler;
	public final static String BUNDLE_KEY="radio";
	public RadioListAdapter(Context context,Handler handler,ArrayList<Radio> Radios) {
		this.context=context;
		mHandler=handler;
		infater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mRadioList=new ArrayList<Radio>();
		for(Radio r:Radios){
			this.mRadioList.add(r);
		}
		
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

	@Override
	public View getView(int position, View convertview, ViewGroup parent) {
		View view = infater.inflate(R.layout.media_radio_item, null);
		TextView radio_name=(TextView)view.findViewById(R.id.radio_name);
		radio_name.setOnClickListener(new OnItemChildClickListener(CLICK_TEXT,position));
		radio_name.setText(mRadioList.get(position).getName());
		ImageButton btn_play=(ImageButton)view.findViewById(R.id.btn_play);
		btn_play.setOnClickListener(new OnItemChildClickListener(CLICK_BUTTON, position));
		return view;
	}
	private class OnItemChildClickListener implements View.OnClickListener {

		// 点击类型索引，对应前面的CLICK_INDEX_xxx
	    private int clickIndex;
	    // 点击列表位置
	    private int position;
	     
	    public OnItemChildClickListener(int clickIndex, int position) {
	        this.clickIndex = clickIndex;
	        this.position = position;
	    }
	 
	    @Override
	    public void onClick(View v) {
	        // 创建Message并填充数据，通过mHandle联系Activity接收处理
	        Message msg = new Message();
	        msg.what = clickIndex;
	        msg.arg1 = position;
	        Bundle b = new Bundle();
	        b.putSerializable(BUNDLE_KEY,mRadioList.get(position));
	        msg.setData(b);
	        mHandler.sendMessage(msg);
	    }
	     
		
	}
	
}
