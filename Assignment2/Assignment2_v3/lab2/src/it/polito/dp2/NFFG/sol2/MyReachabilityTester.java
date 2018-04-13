package it.polito.dp2.NFFG.sol2;

import java.net.URI;
import java.util.HashMap;
import java.util.Set;

import javax.naming.spi.ObjectFactory;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.lab2.NoGraphException;
import it.polito.dp2.NFFG.lab2.ReachabilityTester;
import it.polito.dp2.NFFG.lab2.ServiceException;
import it.polito.dp2.NFFG.lab2.UnknownNameException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;


public class MyReachabilityTester implements ReachabilityTester{
	
	private NffgVerifier monitor;
	private String actualUrl; 
	it.polito.dp2.NFFG.sol2.ObjectFactory obj = new it.polito.dp2.NFFG.sol2.ObjectFactory();
	HashMap<String, String> nodeMap = new HashMap<String, String>();
	WebTarget target;
	String nffgName;
	
	
	public MyReachabilityTester(NffgVerifier nffgVerifier, String url){
		
		this.monitor = nffgVerifier;
		this.actualUrl = url; 
		Client client = ClientBuilder.newClient();
		target = client.target(getBaseURI());
		
	}
	
	private URI getBaseURI() {
	    return UriBuilder.fromUri(actualUrl).build();
	}
	
	
	@Override
	public void loadNFFG(String name) throws UnknownNameException, ServiceException{
		
		NffgReader nffgReader = monitor.getNffg(name);
		if(nffgReader == null){
			throw new UnknownNameException("No nffg matched with the givem name");
		}
		
		Response res = target.path("resource/nodes/").request(MediaType.APPLICATION_XML).delete();
		if(res.getStatus() != 200){
			throw new ServiceException("Delete nodes request failed!");		
		}
		
		System.out.println("-----delete nodes operation succeeded!------");

	
		nffgName = name;
		Set<NodeReader> nodes = nffgReader.getNodes();
		for(NodeReader nodeReader: nodes ){
			
			Node node = obj.createNode();
			String nodeName = nodeReader.getName();
			Property property = obj.createProperty();
			property.setName("name");
			property.setValue(nodeReader.getName());
			node.getProperty().add(property);
			
			try{
				Node nodeRequest = target.path("resource/node/")
								 	.request(MediaType.APPLICATION_XML)
									.post(Entity.entity(node, MediaType.APPLICATION_XML), Node.class);
			
				System.out.println("----Create node operation succeeded!-----");
				String nodeId = nodeRequest.id;
				nodeMap.put(nodeName, nodeId);
				
			}catch (Exception e) {
				// TODO: handle exception
				throw new ServiceException("Post node failed!" + e.getMessage());
			}
		}
		
		for(NodeReader nodeReader: nodes ){
			Set<LinkReader> links = nodeReader.getLinks();
			for(LinkReader linkReader: links){
				
				String dNode = linkReader.getDestinationNode().getName();
				buildRelation(nodeReader.getName(), dNode);
			} 
		}
	}
	
	//method to create relationships
	private void buildRelation(String source, String dest) throws ServiceException{
		
		Relationship relationship = obj.createRelationship();
		relationship.setType("Link");
		String destinationNode = nodeMap.get(dest);
		relationship.setDstNode(destinationNode);
		
		try {
			Relationship relationRequest = target.path("resource/node/")
											.path(nodeMap.get(source))
											.path("relationship")
											.request(MediaType.APPLICATION_XML)
											.post(Entity.entity(relationship, MediaType.APPLICATION_XML),Relationship.class);
			System.out.println("----Create relationship operation succeeded!-----");
			
		} catch (Exception e) {
			// TODO: handle exception
			throw new ServiceException("Post relationship failed!" + e.getMessage());
		}
	}
	
	
	@Override
	public boolean testReachability(String srcName, String destName) throws UnknownNameException, ServiceException, NoGraphException{
		
		if(nffgName == null){
			
			throw new NoGraphException("No nffg matched with the givem name");
		}
		
	
		if(nodeMap.get(srcName) == null){
			throw new UnknownNameException("No node in the HashMap matched with the given source node");
		}
		String sourceId = nodeMap.get(srcName);

		if(nodeMap.get(destName) == null){
			throw new UnknownNameException("No node in the HashMap matched with the given destination node");
		}
		String destinationId = nodeMap.get(destName);
		
		try {
			Paths pathResponse = target.path("/resource/node/")
									  .path(sourceId)
									  .path("paths")
									  .queryParam("dst", destinationId)
									  .request().accept(MediaType.APPLICATION_XML)
									  .get(Paths.class);
			
			System.out.println("----Request for checking the reachability succeeded!-----");			
			
			if(pathResponse.getPath().size() == 0)
				return false;
			else{
				return true;
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			throw new ServiceException("Error in getting the paths between given nodes: " + e.getMessage());
		}
	}
	
	@Override
	public String getCurrentGraphName(){
		if(nffgName != null)
			return nffgName;
		else
			return null;
	}
	
}