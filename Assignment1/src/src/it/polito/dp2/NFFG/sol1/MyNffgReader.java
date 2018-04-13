package it.polito.dp2.NFFG.sol1;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.datatype.XMLGregorianCalendar;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol1.jaxb.NffgType;

public class MyNffgReader implements NffgReader {
	
	private HashMap<String, NodeReader> nodes;
	private Calendar updateTime;
	private Set<LinkReader> links = new HashSet<LinkReader>();
	private String nffgName;
	
	public MyNffgReader(HashMap<String, NodeReader> nodes, NffgType nffg){
	
		if(nffg != null){
			this.nodes = nodes;
			this.updateTime = nffg.getUpdateTime().toGregorianCalendar();
			this.nffgName = nffg.getNffgName();
			for (NodeReader node : nodes.values()) {
				for (LinkReader link : node.getLinks()) {
					this.links.add(link);
				}
			}
		}	
	}
	
	@Override
	public String getName() {
		if(nffgName != null)
			return this.nffgName;
		else
			return null;
	}
	
	@Override
	public Set<NodeReader> getNodes() {
		if(nodes != null)
			return new HashSet<NodeReader>(nodes.values());
		else
			return null;
	}
	
	@Override
	public NodeReader getNode(String name) {
		for (NodeReader nr : nodes.values()){
			if (nr.getName().equals(name)) {
				return nr;
			}
		}
		return null;
	}

	@Override
	public Calendar getUpdateTime() {
		if(updateTime != null)
			return this.updateTime;
		else
			return null;
	}

	public static boolean isNameValid(String namenffg) {
		String Regx = "[A-Za-z][A-Za-z0-9]*";
		return (namenffg==null || namenffg.matches(Regx));
	}

	
	
}
