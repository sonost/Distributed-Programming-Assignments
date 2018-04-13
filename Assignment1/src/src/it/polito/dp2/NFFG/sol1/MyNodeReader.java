package it.polito.dp2.NFFG.sol1;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol1.jaxb.LinksType;
import it.polito.dp2.NFFG.sol1.jaxb.NodeType;

public class MyNodeReader implements NodeReader {
	
	private HashMap<String,LinkReader> links ;
	private FunctionalType funcType;
	private String nodeName;
	
	public MyNodeReader(NodeType nodeType, HashMap<String,LinkReader> links, FunctionalType funcType) {
		if(nodeType != null){
			this.links = links;
			this.funcType = funcType;
			this.nodeName = nodeType.getNodeName();
		}
	}
	
	@Override
	public String getName() {
		if(nodeName != null)
			return this.nodeName;
		else
			return null;
	}
	
	@Override
	public Set<LinkReader> getLinks() {
		
		HashSet<LinkReader> lk = new HashSet<LinkReader>();
		for (LinkReader lr : links.values()) {
			if (lr.getSourceNode().getName().equals(this.nodeName)) {
				lk.add(lr);
			}
		}
		return lk;
	}

	@Override
	public FunctionalType getFuncType() {
		return funcType;
	}
	
	public static boolean isNameValid(String namenffg) {
		String Regx = "[A-Za-z][A-Za-z0-9]*";
		return (namenffg.matches(Regx));
	}

}
