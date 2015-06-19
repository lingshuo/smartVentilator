package cn.lisa.smartventilator.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.lisa.smartventilator.bean.Video;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
/***
 * 视频相关的功能类
 * @author LingShuo
 *
 */
public class VideoUtil {
	public final static int GET_VIDEO_FINISH = 0;
	// 视频路径
	private final static String cur_path = "/sdcard/smartVentilator/";
	
	/**
	 * 获取视频列表
	 * @param handler 处理视频列表的handler
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
			video.setName(VideoUtil.getFileName(files[i].getPath()));
			video.setThumb(VideoUtil.getVideoThumbnail(files[i].getPath(), 100,
					50, MediaStore.Images.Thumbnails.MICRO_KIND));
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
	 * 获取视频的缩略图
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
		// 获取视频的缩略图
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		// Log.i("sv","w"+bitmap.getWidth());
		// Log.i("sv","h"+bitmap.getHeight());
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	/**
	 * 调用系统播放器 播放视频
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
	 * 从路径获取文件名
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
