package cn.lisa.smartventilator.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.lisa.smartventilator.R;

public class MonitorFragment extends Fragment{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_monitor, null);
		TextView tv1=(TextView)view.findViewById(R.id.monitor_smoke_result);
		TextView tv2=(TextView)view.findViewById(R.id.monitor_phenolic_result);
		TextView tv3=(TextView)view.findViewById(R.id.monitor_pm2_5_result);		
		tv1.setText("Á¼ºÃ");
		tv2.setText("Á¼ºÃ");
		tv3.setText("Á¼ºÃ");
		
		return view;
	}

	private ImageView imageView;
	
	
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
}
