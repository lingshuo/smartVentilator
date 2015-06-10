package cn.lisa.smartventilator.bean;

import java.io.Serializable;

public class Radio implements Serializable {
	private String name;
	private String url;
	
	public Radio(){

	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}
	
}
