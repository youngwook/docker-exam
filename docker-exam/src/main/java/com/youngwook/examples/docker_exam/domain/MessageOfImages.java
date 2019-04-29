package com.youngwook.examples.docker_exam.domain;

import java.util.List;

import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.Image;

public class MessageOfImages {
	List<Image> images;

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}
	
}
