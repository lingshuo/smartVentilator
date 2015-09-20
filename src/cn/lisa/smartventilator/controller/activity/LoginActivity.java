package cn.lisa.smartventilator.controller.activity;

import cn.lisa.smartventilator.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	private Button btn_login;
	private Button btn_cancel;
	
	private EditText et_username;
	private EditText et_password;
	
	private CheckBox cb_rememeber;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_login);
		btn_login=(Button)findViewById(R.id.login_btn_confirm);
		btn_cancel=(Button)findViewById(R.id.login_btn_cancel);
		
		et_username=(EditText)findViewById(R.id.login_editText_username);
		et_password=(EditText)findViewById(R.id.login_editText_password);
		
		cb_rememeber=(CheckBox)findViewById(R.id.login_checkBox);
		cb_rememeber.setChecked(false);
		
		btn_login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				SharedPreferences sp = getSharedPreferences("smartventilator.preferences", 0);
				Editor editor = sp.edit();
				String username = et_username.getText().toString();
				String password = et_password.getText().toString();
				String uname=sp.getString("username", "");
				String pword=sp.getString("password", "");
				if(uname.isEmpty()&&pword.isEmpty()){//新用户注册
					editor.putString("username", username);
					editor.putString("password", password);
					editor.putBoolean("login", true);
					editor.commit();
					Intent intent=new Intent();
					intent.setClass(getApplicationContext(), MainActivity.class);
					intent.putExtra("login", true);
					startActivity(intent);
				}else if(username.equals(uname)&&password.equals(pword)){//登陆
					Intent intent=new Intent();
					intent.setClass(getApplicationContext(), MainActivity.class);
					intent.putExtra("login", true);
					editor.putBoolean("login", true);
					editor.commit();
					startActivity(intent);
				}else{
					Toast.makeText(getApplicationContext(), "用户名或密码错误", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		
		btn_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				et_username.setText("");
				et_password.setText("");
			}
		});
		
		cb_rememeber.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferences sp = getSharedPreferences("smartventilator.preferences", 0);
				Editor editor = sp.edit();
				editor.putBoolean("rememberUser", cb_rememeber.isChecked());
				// 提交设置
				editor.commit();
			}
		});
		super.onCreate(savedInstanceState);		
	}
	
	// 隐藏输入法
    // 获取点击事件
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (isHideInput(view, ev)) {
                HideSoftInput(view.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }
    // 判定是否需要隐藏
    private boolean isHideInput(View v, MotionEvent ev) {
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (ev.getX() > left && ev.getX() < right && ev.getY() > top
                    && ev.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
    // 隐藏软键盘
    private void HideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
