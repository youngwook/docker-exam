package com.youngwook.examples.docker_exam.domain;

import java.util.List;

import com.spotify.docker.client.messages.Container;

public class MessageOfContainer {
	List<Container> containers;

	public List<Container> getContainers() {
		return containers;
	}

	public void setContainers(List<Container> containers) {
		this.containers = containers;
	}
	
}
