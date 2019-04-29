package com.youngwook.examples.docker_exam.domain;

import java.util.List;

import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ImageSearchResult;

public class MessageOfImage {
	List<ImageSearchResult> image;

	public List<ImageSearchResult> getImage() {
		return image;
	}

	public void setImage(List<ImageSearchResult> image) {
		this.image = image;
	}
}
