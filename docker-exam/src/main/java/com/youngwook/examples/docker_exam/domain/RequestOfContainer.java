package com.youngwook.examples.docker_exam.domain;

public class RequestOfContainer {
	private String image;
	private String cport;
	private String hport;
	public String getImage() {
		return image;
	}
	public String getCport() {
		return cport;
	}
	public String getHport() {
		return hport;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public void setCport(String cport) {
		this.cport = cport;
	}
	public void setHport(String hport) {
		this.hport = hport;
	}
}
