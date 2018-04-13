package it.polito.dp2.NFFG.sol1;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol1.jaxb.LinkType;

public class MyLinkReader implements LinkReader {
	
	private NodeReader sourceNode;
	private NodeReader destinationNode;
	private String linkName;
	
	public MyLinkReader(LinkType linkType, NodeReader sourceNode, NodeReader destinationNode){
	
		if(linkType != null){
			this.sourceNode = sourceNode;
			this.destinationNode = destinationNode;	
			this.linkName = linkType.getLinkName();
		}
	}
	
	@Override
	public String getName() {
		if(linkName != null)
			return this.linkName;
		else
			return null;
	}
	
	@Override
	public NodeReader getSourceNode() {
		if(sourceNode != null)
			return this.sourceNode;
		else
			return null;
	}

	@Override
	public NodeReader getDestinationNode() {
		if(destinationNode != null)
			return this.destinationNode;
		else
			return null;
	}
	
	public static boolean isNameValid(String namenffg) {
		String Regx = "[A-Za-z][A-Za-z0-9]*";
		return (namenffg.matches(Regx));
	}



}
