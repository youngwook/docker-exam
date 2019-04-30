package com.youngwook.examples.docker_exam.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.ListContainersParam;
import com.spotify.docker.client.DockerClient.ListImagesParam;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerConfig.Builder;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.Image;
import com.spotify.docker.client.messages.ImageSearchResult;
import com.spotify.docker.client.messages.PortBinding;
import com.youngwook.examples.docker_exam.domain.RequestOfBuildImage;
import com.youngwook.examples.docker_exam.domain.RequestOfContainer;

import jersey.repackaged.com.google.common.collect.ImmutableList;
import jersey.repackaged.com.google.common.collect.ImmutableMap;

@Component
public class TestDockerClient {
	
	final static Logger log = LoggerFactory.getLogger(TestDockerClient.class);
	
	private DockerClient docker; 
	
//	@Value("${docker.engine}")
	private String host;
//	@Value("${docker.engine.port}")
	private String port;

	public TestDockerClient() {
		log.info("docker client initiation");
		
		host = "192.168.0.17";
		port = "4327";
		log.info(host+":"+port);
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
	public List<Container> getContainer(String name) {
		List<Container> container = null;
		try {
			 container = docker.listContainers(ListContainersParam.filter("name", name));
		} catch (DockerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return container;
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
	public String pullImage(String image) {
		String result ="failed";                                                  // pull image by name such as <registry>/<repository>:<tag>
		try {
			docker.pull(image);
			result = "success!";
		} catch (DockerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return result;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return result;
		}
		return result;
	}
	public ContainerCreation createContainer(RequestOfContainer rc) throws DockerException, InterruptedException {
		Builder config = ContainerConfig.builder();                                         //create container with builder class 
		config.image(rc.getImage());                                                        // set image name
		config.exposedPorts(rc.getCport());                                                 // set export port
		config.hostConfig(HostConfig                                                        // configure host with container pair information like port
				.builder()
				.portBindings(
						ImmutableMap.of(
								rc.getCport(),ImmutableList.of(
										PortBinding.of("0.0.0.0", rc.getHport())
										)
								)
						).build()
				);
		
		return docker.createContainer(config.build());                                      // generate container instance
	}
	public ContainerCreation startContainer(ContainerCreation container) {                     
		
		try {
			docker.startContainer(container.id());                                         // start container with specified container id which provide by the container instance
			return container;
		} catch (DockerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return null;
	}
	public boolean stopContainer(String id) {
		boolean result = false;
		try {
			docker.stopContainer(id, 10);                                                // stop container after 10 seconds
			docker.removeContainer(id);                                                  // remove container with specified id
			result = true;
		} catch (DockerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	private String sendRequest(String path, boolean isPost) throws IOException {
		URL dockerEngine = new URL(path);
		HttpURLConnection con = (HttpURLConnection) dockerEngine.openConnection();
		if(isPost) {
			con.setRequestMethod("POST");
		}
		int status = con.getResponseCode();
		BufferedReader res;
		if(status > 299) {
			res = new BufferedReader(new InputStreamReader(con.getErrorStream(),"UTF-8"));
		}else {
			res = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
		}
		StringBuilder response = new StringBuilder();
		for(String responseLine = res.readLine(); responseLine != null; responseLine = res.readLine()) {
			response.append(responseLine);
		}
		res.close();
		return response.toString();
		
	}
//	public String buildImage(RequestOfBuildImage image)  { // this is use for local file system. 
//	String imageID ="failed";	//return String
//	URI dockerfile;				//RUL of github repository
//	try {
//		dockerfile = new URI(image.getURL());	//generate URL object with String URL.
//		imageID = docker.build(Paths.get(dockerfile), image.getName()); //build image with URI from URL and name of Image which is the -t valuer in CLI
//	}  catch (InterruptedException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	} catch (URISyntaxException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	} catch (DockerException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	} 
//	return imageID;
//}
	public String buildImage(HashMap<String, String> image) {
		String result = "failed";
		String url = getDockerEngineURL("build")+convertParams2String(image);
		log.info("get url:"+url);
		try {
			result = sendRequest(url, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	private String getDockerEngineURL(String path) {
		StringBuilder builder = new StringBuilder("http://");
		builder.append(host).append(":").append(port).append("/").append(path);
		return builder.toString();
	}
	private String convertParams2String(Map<String, String> params) {
		URIBuilder builder = new URIBuilder();
		for(Map.Entry<String, String> entry: params.entrySet()) {
			builder.addParameter(entry.getKey(), entry.getValue());
		}
		String result = builder.toString();
		return result.length()>0?result:"";
	}
	@PreDestroy
	private void stop() {
		log.info("close client connection");                                            // close docker client
		docker.close();
	}
}
