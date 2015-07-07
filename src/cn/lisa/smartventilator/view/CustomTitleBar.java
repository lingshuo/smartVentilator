package cn.lisa.smartventilator.view;

import cn.lisa.smartventilator.R;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomTitleBar {

    private Activity mActivity;
    //��Ҫʹ�� static ��Ϊ������ҳ�淵��ʱ�ᱨ��

    /**
     * @param activity
     * @param title
     * @see [�Զ��������]
     */
    public void getTitleBar(Activity activity, String title) {
        mActivity = activity;
        activity.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        //ָ���Զ���������Ĳ����ļ�
        activity.setContentView(R.layout.mytitlebar);
        activity.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.mytitlebar);
        //��ȡ�Զ����������TextView�ؼ�����������Ϊ���ݹ������ַ���
        TextView textView = (TextView) activity.findViewById(R.id.mytitle);
        textView.setText(title);
        //���÷��ذ�ť�ĵ���¼�
        ImageView image = (ImageView) activity.findViewById(R.id.company_logo);
//        titleBackBtn.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//            //����ϵͳ�ķ��ذ����ĵ���¼�
//                mActivity.onBackPressed();
//            }
//        });
    }
}
