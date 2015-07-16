package cn.lisa.smartventilator.controller.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.lisa.smartventilator.controller.entity.Video;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

/***
 * ��Ƶ��صĹ�����
 * 
 * @author LingShuo
 *
 */
public class VideoManager {
	public final static int GET_VIDEO_FINISH = 0;
	// ��Ƶ·��
	private final static String cur_path = "/sdcard/smartVentilator/";

	/**
	 * ��ȡ��Ƶ�б�
	 * 
	 * @param handler
	 *            ������Ƶ�б��handler
	 * @return
	 */
	public static List<Video> getVideoList(Handler handler) {
		File file = new File(cur_path);
		if (!file.exists()) {
			try {
				file.mkdirs();
				file = new File(cur_path);
			} catch (Exception e) {
				return null;
			}
		}
		File[] files = null;
		files = file.listFiles();
		List<Video> videoList = new ArrayList<Video>();
		for (int i = 0; i < files.length; i++) {
			Video video = new Video();
			video.setName(VideoManager.getFileName(files[i].getPath()));
			video.setThumb(VideoManager.getVideoThumbnail(files[i].getPath(),
					100, 50, MediaStore.Images.Thumbnails.MICRO_KIND));
			video.setPath(files[i].getPath());
			videoList.add(video);
		}
		Message msg = new Message();
		msg.what = GET_VIDEO_FINISH;
		msg.obj = videoList;

		handler.sendMessage(msg);
		return videoList;
	}

	/**
	 * ��ȡ��Ƶ������ͼ
	 * 
	 * @param videoPath
	 * @param width
	 * @param height
	 * @param kind
	 * @return
	 */
	public static Bitmap getVideoThumbnail(String videoPath, int width,
			int height, int kind) {
		Bitmap bitmap = null;
		// ��ȡ��Ƶ������ͼ
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		// Log.i("sv","w"+bitmap.getWidth());
		// Log.i("sv","h"+bitmap.getHeight());
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	/**
	 * ����ϵͳ������ ������Ƶ
	 * 
	 * @param videoPath
	 * @return
	 */
	public static Intent getVideoIntent(String videoPath) {
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
		String strend = "";
		if (videoPath.toLowerCase().endsWith(".mp4")) {
			strend = "mp4";
		} else if (videoPath.toLowerCase().endsWith(".3gp")) {
			strend = "3gp";
		} else if (videoPath.toLowerCase().endsWith(".mov")) {
			strend = "mov";
		} else if (videoPath.toLowerCase().endsWith(".wmv")) {
			strend = "wmv";
		}

		ComponentName name = new ComponentName("com.android.gallery3d",
				"com.android.gallery3d.app.MovieActivity");
		intent.setDataAndType(Uri.parse(videoPath.trim()), "video/" + strend);
		intent.setComponent(name);
		return intent;
	}

	/**
	 * ��·����ȡ�ļ���
	 * 
	 * @param pathandname
	 * @return
	 */
	public static String getFileName(String pathandname) {
		int start = pathandname.lastIndexOf("/");
		int end = pathandname.lastIndexOf(".");
		if (start != -1 && end != -1) {
			return pathandname.substring(start + 1, end);
		} else {
			return null;
		}
	}
}
