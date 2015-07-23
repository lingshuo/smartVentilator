package cn.lisa.smartventilator.view.fragment;

import cn.lisa.smartventilator.R;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SettingFragment extends Fragment implements OnClickListener {

	private Context context;
	public DevicePolicyManager policyManager;
	public ComponentName componentName;
	private TextView tv_deviceId;
	@SuppressWarnings("unused")
	private TextView tv_username;
	@SuppressWarnings("unused")
	private TextView tv_password;
	private EditText et_username;
	private EditText et_password;
	private TextView tv_version;
	
	private Button btn_cancel;
	private Button btn_confirm;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = getActivity();
		SharedPreferences sp = context.getSharedPreferences("smartventilator.preferences", 0);
		String mid = sp.getString("mID", "");
		String version=sp.getString("version", "");
		View view = inflater.inflate(R.layout.fragment_setting, null);
		tv_deviceId = (TextView) view.findViewById(R.id.tv_device_id);
		tv_deviceId.setText(mid);
		tv_username = (TextView) view.findViewById(R.id.tv_device_username);
		tv_password = (TextView) view.findViewById(R.id.tv_device_password);

		et_username = (EditText) view.findViewById(R.id.editText_username);
		et_password = (EditText) view.findViewById(R.id.editText_password);

		btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
		btn_confirm = (Button) view.findViewById(R.id.btn_confirm);

		tv_version=(TextView)view.findViewById(R.id.version);
	
		tv_version.setText(version);
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
