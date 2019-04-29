package com.youngwook.examples.docker_exam.controller;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.spotify.docker.client.DockerClient.ListContainersParam;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.ImageSearchResult;
import com.youngwook.examples.docker_exam.client.TestDockerClient;
import com.youngwook.examples.docker_exam.domain.MessageOfContainer;
import com.youngwook.examples.docker_exam.domain.MessageOfImage;
import com.youngwook.examples.docker_exam.domain.MessageOfImages;
import com.youngwook.examples.docker_exam.domain.RequestOfBuildImage;
import com.youngwook.examples.docker_exam.domain.RequestOfContainer;

@RestController
public class Controller {
	final static Logger log = LoggerFactory.getLogger(Controller.class);

	@Autowired
	TestDockerClient docker;
	
	@RequestMapping("/")
	public String index() {
		return "hello";
	}
	
	@RequestMapping("/containers")
	public MessageOfContainer getContainers() {
		//generate instance of MessageOfContainer of domain class which will automatically convert to JSON file by spring boot. 
		//for list of containers in docker host
		MessageOfContainer mc = new MessageOfContainer();
		try {
			mc.setContainers(docker.getContainers());
		} catch (DockerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mc;
	}
	
	@RequestMapping("/images")
	public MessageOfImages getImages() {
		//generate instance of MessageOfImages of domain class which will automatically convert to JSON file by spring boot. 
		//for list of images in docker host
		MessageOfImages image = new MessageOfImages();
		try {
			image.setImages(docker.getImages());
		} catch (DockerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return image;
	}
	@RequestMapping(value="/buildImg", method=RequestMethod.POST)
	public String buildImage(@RequestBody RequestOfBuildImage image) {
		//bound RequestBody with RequestOfBuildImage class to automatically convert JSON to Object
		//invoke build image method and receive the return value with ImageID
		log.info("image name: "+image.getName());
		log.info("image url: " + image.getURL());
		String imageID = docker.buildImage(image);
		String results = docker.pushImage(imageID);
		return results;
	}
	
	@RequestMapping(value = "/image/{name}", method= {RequestMethod.POST})
	public MessageOfImage getImages(@PathVariable String name) {
		//generate instance of MessageOfImage of domain class which will automatically convert to JSON file by spring boot. 
		//for list of image with name in docker hub host(not sure)
		MessageOfImage image = new MessageOfImage();
		try {
			image.setImage(docker.searchImage(name));
		} catch (DockerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return image;
	}
	@PostMapping("/pushImage")
	public String pushImage(@RequestBody HashMap<String, String> image) {
		//get request body through HashMap that is automatically converted by Spring boot from JSON to HashMap. 
		// invoke pushImage with parameter of image name.
		String result = docker.pushImage(image.get("ImageName"));
		return result;
	}
	@PostMapping("/runImage")
	public ContainerCreation runImage(@RequestBody RequestOfContainer container) {

		ContainerCreation ct = null; 
		docker.pullImage(container.getImage());
		try {
			ct = docker.startContainer(docker.createContainer(container));

		} catch (DockerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ct;
	}
	@PostMapping("/stopImage/{id}")
	public String stopImage(@PathVariable String id) {
		if(!docker.stopContainer(id)) {
			return "failed";
		}
		return "successfully stoped and removed";
		
	}
	@RequestMapping("/ping")
	public String ping() {
		return "I am alive";
	}

}
