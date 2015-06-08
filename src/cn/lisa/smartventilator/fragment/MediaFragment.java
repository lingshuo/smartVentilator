package cn.lisa.smartventilator.fragment;

import cn.lisa.smartventilator.R;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MediaFragment extends Fragment {
	private ImageView mPlayBtn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_media, null);
		mPlayBtn = (ImageView) view.findViewById(R.id.media_play_button);
		Context context = getActivity();
		final PackageManager packageManager = context.getPackageManager();
		mPlayBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = packageManager
						.getLaunchIntentForPackage("com.youku.phone");
				if (intent == null) {
					System.out.println("APP not found!");
				} else {
					startActivity(intent);
				}
			}
		});
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
