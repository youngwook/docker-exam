package com.youngwook.examples.docker_exam.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.ListContainersParam;
import com.spotify.docker.client.DockerClient.ListImagesParam;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.Image;
import com.spotify.docker.client.messages.ImageSearchResult;
import com.youngwook.examples.docker_exam.domain.RequestOfBuildImage;

@Component
public class TestDockerClient {
	
	final static Logger log = LoggerFactory.getLogger(TestDockerClient.class);
	
	private DockerClient docker; 

	public TestDockerClient() {
		log.info("invoke init method");
		try {
			//create a client based on DOCKER_HOST and DOCKER_CERT_PATH env vars
			docker = DefaultDockerClient.fromEnv().build();
		} catch (DockerCertificateException e) {
			// TODO Auto-generated catch block
			log.info("error occured!");
			e.printStackTrace();
		} 
	}

	public List<Container> getContainers() throws DockerException, InterruptedException{
		//list all containers no matter running or not. 
		return docker.listContainers(ListContainersParam.allContainers());
	}
	
	public List<Image> getImages() throws DockerException, InterruptedException{
		//list images in docker host
		return docker.listImages();
//		return docker.listImages(ListImagesParam.allImages());
	}
	
	public List<ImageSearchResult> searchImage(String name) throws DockerException, InterruptedException{
		//list images with the name in docker hub and docker host(not sure)
		return docker.searchImages(name);
	}
	public String buildImage(RequestOfBuildImage image)  {
		String imageID ="failed";	//return String
		URI dockerfile;				//RUL of github repository
		try {
			dockerfile = new URI(image.getURL());	//generate URL object with String URL.
			imageID = docker.build(Paths.get(dockerfile), image.getName()); //build image with URI from URL and name of Image which is the -t valuer in CLI
		}  catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DockerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return imageID;
	}
	public String pushImage(String image) {
		try {
			docker.push(image);	//push image to registry with Image name
		} catch (DockerException e) {
			// TODO Auto-generated catch block		
			e.printStackTrace();
			return "failed";
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "failed";
		}
		return "ok";
	}
}
