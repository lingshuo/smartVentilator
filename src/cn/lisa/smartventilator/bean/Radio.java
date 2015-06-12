package cn.lisa.smartventilator.bean;

import java.io.Serializable;

public class Radio implements Serializable {
	private int id;
	private String name;
	private String url;
	
	public Radio(){

	}
	public void setId(int id){
		this.id=id;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public int getId(){
		return id;
	}
	
	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}
	
}
