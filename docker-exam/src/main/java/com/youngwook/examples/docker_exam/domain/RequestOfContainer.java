package com.youngwook.examples.docker_exam.domain;

public class RequestOfContainer {
	private String image;
	private String port;
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
}
