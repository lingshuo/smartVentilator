package cn.lisa.smartventilator.view;

import cn.lisa.smartventilator.R;
import cn.lisa.smartventilator.manager.MyWindowManager;
import cn.lisa.smartventilator.receiver.LockReceiver;
import cn.lisa.smartventilator.service.FloatWindowService;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


public class FloatWindowBigView extends LinearLayout {

	/**
     * �豸���Է���
     */
    private DevicePolicyManager dpm; 
	   
    private ComponentName componentName;
    
    Context context;
	/**
	 * ��¼���������Ŀ��
	 */
	public static int viewWidth;

	/**
	 * ��¼���������ĸ߶�
	 */
	public static int viewHeight;

	public FloatWindowBigView(final Context context) {
		super(context);
		this.context=context;
		dpm = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		LayoutInflater.from(context).inflate(R.layout.float_window_big, this);
		View view = findViewById(R.id.big_window_layout);
		viewWidth = view.getLayoutParams().width;
		viewHeight = view.getLayoutParams().height;
		Button lock=(Button)findViewById(R.id.lock);
		Button close = (Button) findViewById(R.id.close);
		Button back = (Button) findViewById(R.id.back);
		lock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openAdmin(v);
				lockscreen(v);
			}
		});
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ����ر���������ʱ���Ƴ���������������ֹͣService
				MyWindowManager.removeBigWindow(context);
				MyWindowManager.removeSmallWindow(context);
				Intent intent = new Intent(getContext(), FloatWindowService.class);
				context.stopService(intent);
			}
		});
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ������ص�ʱ���Ƴ���������������С������
				MyWindowManager.removeBigWindow(context);
				MyWindowManager.createSmallWindow(context);
			}
		});
	}
	/**
     * �ô���ȥ��������Ա
     */
    public void openAdmin(View view) {
        // ����һ��Intent
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        // ��Ҫ����˭
        ComponentName componentName= new ComponentName(context,LockReceiver.class);
 
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,componentName);
        // Ȱ˵�û���������ԱȨ��
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"����һ������");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
        context.startActivity(intent);
    }
    /**
     * һ������
     */
    public void lockscreen(View view) {
        ComponentName who = new ComponentName(context, LockReceiver.class);
        if (dpm.isAdminActive(who)) {
            dpm.lockNow();// ����
//            dpm.resetPassword("", 0);// ������������
 
            // ���Sdcard�ϵ�����
            // dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
            // �ָ���������
            // dpm.wipeData(0);
        } else {
            Toast.makeText(context, "��û�д򿪹���ԱȨ��", 1).show();
            return;
        }
 
    }
}
