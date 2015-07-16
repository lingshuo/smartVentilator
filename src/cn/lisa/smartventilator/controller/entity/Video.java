package cn.lisa.smartventilator.controller.entity;

import android.graphics.Bitmap;

/**
 * Info of Video
 * 
 * @author Administrator
 *
 */
public class Video {

	private String name;

	private Bitmap thumb;

	private String path;

	public Video() {
		this.name = "myvideo";
		this.thumb = null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Bitmap getThumb() {
		return thumb;
	}

	public void setThumb(Bitmap thumb) {
		this.thumb = thumb;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String string) {
		this.path = string;
	}

}
