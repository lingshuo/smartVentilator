package cn.lisa.smartventilator.view.view;

import cn.lisa.smartventilator.R;
import cn.lisa.smartventilator.controller.manager.MyWindowManager;
import cn.lisa.smartventilator.controller.service.FloatWindowService;
import cn.lisa.smartventilator.view.activity.LockActivity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class FloatWindowBigView extends LinearLayout implements OnClickListener {

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
		this.context = context;

		LayoutInflater.from(context).inflate(R.layout.float_window_big, this);
		View view = findViewById(R.id.big_window_layout);
		viewWidth = view.getLayoutParams().width;
		viewHeight = view.getLayoutParams().height;
		Button lock = (Button) findViewById(R.id.lock);
		Button back = (Button) findViewById(R.id.back);

		lock.setOnClickListener(this);
		back.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lock:
			// ����
			MyWindowManager.removeBigWindow(context);
			Intent intent = new Intent(getContext(), FloatWindowService.class);
			context.stopService(intent);

			Intent intent1 = new Intent();
			intent1.setClass(getContext(), LockActivity.class);
			intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent1);

			break;
		// case R.id.close:
		// // ����ر���������ʱ���Ƴ���������������ֹͣService
		// MyWindowManager.removeBigWindow(context);
		// MyWindowManager.removeSmallWindow(context);
		// Intent intent = new Intent(getContext(),
		// FloatWindowService.class);
		// context.stopService(intent);
		// break;
		case R.id.back:
			// ������ص�ʱ���Ƴ���������������С������
			MyWindowManager.removeBigWindow(context);
			MyWindowManager.createSmallWindow(context);
			break;
		}
	}
}
