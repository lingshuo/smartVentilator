package cn.lisa.smartventilator.controller.entity;

import java.io.Serializable;

public class Radio implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -899790891476280211L;
	private int id;
	private String name;
	private String url;

	public Radio() {

	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

}
