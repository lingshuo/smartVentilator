package cn.lisa.smartventilator.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import cn.lisa.smartventilator.R;

public class MonitorFragment extends Fragment{
	private ToggleButton mTogBtn;
	private TextView tv_smoke;
	private TextView tv_phenolic;
	private TextView tv_pm2_5;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_monitor, null);
		tv_smoke=(TextView)view.findViewById(R.id.monitor_smoke_result);
		tv_phenolic=(TextView)view.findViewById(R.id.monitor_phenolic_result);
		tv_pm2_5=(TextView)view.findViewById(R.id.monitor_pm2_5_result);		
		tv_smoke.setText("����");
		tv_phenolic.setText("����");
		tv_pm2_5.setText("����");
		mTogBtn = (ToggleButton) view.findViewById(R.id.mTogBtn); // ��ȡ���ؼ�
		mTogBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					//ѡ��
				}else{
					//δѡ��
				}
			}
		});// ��Ӽ����¼�
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
