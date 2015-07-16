package cn.lisa.smartventilator.view.fragment;

import cn.lisa.smartventilator.R;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SettingFragment extends Fragment implements OnClickListener {

	@SuppressWarnings("unused")
	private Context context;
	public DevicePolicyManager policyManager;
	public ComponentName componentName;
	@SuppressWarnings("unused")
	private TextView tv_deviceId;
	@SuppressWarnings("unused")
	private TextView tv_username;
	@SuppressWarnings("unused")
	private TextView tv_password;
	private EditText et_username;
	private EditText et_password;

	private Button btn_cancel;
	private Button btn_confirm;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();

		View view = inflater.inflate(R.layout.fragment_setting, null);
		tv_deviceId = (TextView) view.findViewById(R.id.tv_device_id);
		tv_username = (TextView) view.findViewById(R.id.tv_device_username);
		tv_password = (TextView) view.findViewById(R.id.tv_device_password);

		et_username = (EditText) view.findViewById(R.id.editText_username);
		et_password = (EditText) view.findViewById(R.id.editText_password);

		btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
		btn_confirm = (Button) view.findViewById(R.id.btn_confirm);

		btn_cancel.setOnClickListener(this);
		btn_confirm.setOnClickListener(this);

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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cancel:
			// 取消修改
			et_username.setText("");
			et_password.setText("");
			break;
		case R.id.btn_confirm:
			// 提交修改
			break;
		default:
			break;
		}
	}
}
