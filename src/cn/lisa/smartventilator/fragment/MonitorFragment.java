package cn.lisa.smartventilator.fragment;

import android.app.Fragment;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import cn.lisa.smartventilator.R;

public class MonitorFragment extends Fragment {
	private ToggleButton tb_ventilator;
	private TextView tv_smoke;
	private TextView tv_phenolic;
	private TextView tv_pm2_5;
	private LinearLayout layout_gears_control;
	private LinearLayout layout_gears_blank;
	private Button mBtn1;
	private Button mBtn2;
	private Button mBtn3;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_monitor, null);
		tv_smoke = (TextView) view.findViewById(R.id.monitor_smoke_result);
		tv_phenolic = (TextView) view
				.findViewById(R.id.monitor_phenolic_result);
		tv_pm2_5 = (TextView) view.findViewById(R.id.monitor_pm2_5_result);
		tv_smoke.setText("良好");
		tv_phenolic.setText("良好");
		tv_pm2_5.setText("良好");
		tb_ventilator = (ToggleButton) view
				.findViewById(R.id.mTogBtn_ventilator); // 获取到控件
		layout_gears_control = (LinearLayout) view
				.findViewById(R.id.control_ventilator_gears);
		layout_gears_blank = (LinearLayout) view
				.findViewById(R.id.control_ventilator_blank);
		if (!tb_ventilator.isChecked()) {
			layout_gears_blank.setVisibility(View.VISIBLE);
			layout_gears_control.setVisibility(View.GONE);
		}else{
			layout_gears_blank.setVisibility(View.GONE);
			layout_gears_control.setVisibility(View.VISIBLE);
		}
		tb_ventilator.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					// 选中
					layout_gears_blank.setVisibility(View.GONE);
					layout_gears_control.setVisibility(View.VISIBLE);
				} else {
					// 未选中
					layout_gears_blank.setVisibility(View.VISIBLE);
					layout_gears_control.setVisibility(View.GONE);
				}
			}
		});// 添加监听事件

		mBtn1 = (Button) view.findViewById(R.id.control_ventilator_gears_btn_1);
		mBtn2 = (Button) view.findViewById(R.id.control_ventilator_gears_btn_2);
		mBtn3 = (Button) view.findViewById(R.id.control_ventilator_gears_btn_3);
		mBtn1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changeGear(2);
				mBtn1.setVisibility(View.GONE);
				mBtn2.setVisibility(View.VISIBLE);
			}
		});
		mBtn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changeGear(3);
				mBtn2.setVisibility(View.GONE);
				mBtn3.setVisibility(View.VISIBLE);
			}
		});
		mBtn3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changeGear(1);
				mBtn3.setVisibility(View.GONE);
				mBtn1.setVisibility(View.VISIBLE);
			}
		});
		return view;
	}

	/**/
	private void changeGear(int gear) {
		Log.i("sv", "change gears to " + gear);
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
}
