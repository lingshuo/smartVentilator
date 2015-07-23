package cn.lisa.smartventilator.controller.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.lisa.smartventilator.controller.entity.Video;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

/***
 * 视频相关的功能类
 * 
 * @author LingShuo
 *
 */
public class VideoManager {
	public final static int GET_VIDEO_FINISH = 0;
	// 视频路径
	@SuppressLint("SdCardPath")
	private final static String cur_path = "/sdcard/smartVentilator/";
	private final static String ext_path="/mnt/extsd/";
	/**
	 * 获取视频列表
	 * 
	 * @param mHandler
	 *            处理视频列表的handler
	 * @return
	 */
	public static List<Video> getVideoList(Handler handler) {
		
		File ext_file=new File(ext_path);
	
		File cur_file = new File(cur_path);
		if (!cur_file.exists()) {
			try {
				cur_file.mkdirs();
				cur_file = new File(cur_path);
			} catch (Exception e) {
				Log.e("video", "cur_path direrror");
				return null;
			}
		}
		List<Video> videoList = new ArrayList<Video>();
		
		File[] ext_files = null;
		ext_files = ext_file.listFiles();
		if(ext_files!=null){
			for (int i = 0; i < ext_files.length; i++) {
				if(ext_files[i].isFile()){
					Video video = new Video();
					video.setName(VideoManager.getFileName(ext_files[i].getPath()));
					video.setThumb(VideoManager.getVideoThumbnail(ext_files[i].getPath(), 100, 50,
							MediaStore.Images.Thumbnails.MICRO_KIND));
					video.setPath(ext_files[i].getPath());
					videoList.add(video);
				}
			}
		}
		
		File[] cur_files = null;
		cur_files = cur_file.listFiles();

		for (int i = 0; i < cur_files.length; i++) {
			if(cur_files[i].isFile()){
				Video video = new Video();
				video.setName(VideoManager.getFileName(cur_files[i].getPath()));
				video.setThumb(VideoManager.getVideoThumbnail(cur_files[i].getPath(), 100, 50,
						MediaStore.Images.Thumbnails.MICRO_KIND));
				video.setPath(cur_files[i].getPath());
				videoList.add(video);
			}
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
	public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
		Bitmap bitmap = null;
		// 获取视频的缩略图
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
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
	@SuppressLint("DefaultLocale")
	public static Intent getVideoIntent(String videoPath) {
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
		String strend = "";
		if (videoPath.toLowerCase().endsWith(".mp4")){
			strend = "mp4";
		} else if (videoPath.toLowerCase().endsWith(".3gp")){
			strend = "3gp";
		} else if (videoPath.toLowerCase().endsWith(".mov")){
			strend = "mov";
		} else if (videoPath.toLowerCase().endsWith(".wmv")){
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
