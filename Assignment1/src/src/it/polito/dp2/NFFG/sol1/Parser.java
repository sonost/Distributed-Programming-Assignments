package it.polito.dp2.NFFG.sol1;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.awt.List;
import java.io.File;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import javax.lang.model.element.NestingKind;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

import org.w3c.dom.stylesheets.LinkStyle;
import org.xml.sax.SAXParseException;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol1.jaxb.*;

public class Parser {
	private String xmlFileName, schemaFileName;
	private Set<NffgReader> nffgSet = new HashSet<NffgReader>();
	private HashMap<String, MyNffgReader> nffgs = new HashMap<String, MyNffgReader>();
	private HashMap<String, MyPolicyReader> policies = new HashMap<String, MyPolicyReader>();
	private HashSet<PolicyReader> policySet = new HashSet<PolicyReader>();
	
	
	public Parser(String xmlFileName, String schemaFileName){
		this.xmlFileName = xmlFileName;
		this.schemaFileName = schemaFileName;
		this.nffgs = nffgs;
		this.policies = policies;
	}
	
	public void parse() throws NffgVerifierException {
        Object rootObj;
		try {
			rootObj = unmarshal(xmlFileName, schemaFileName, "it.polito.dp2.NFFG.sol1.jaxb");
		} catch (Exception e) {
			throw new NffgVerifierException(e);
		}
		
		if (!(rootObj instanceof JAXBElement<?>))
			throw new NffgVerifierException("The root element is not of type JAXBElement<>");
		
		Object rootObjValue = ((JAXBElement<?>)rootObj).getValue();
		
		if (rootObjValue == null)
			throw new NffgVerifierException("The value of the root element is null");
		
		if (!(rootObjValue instanceof NffgServiceType))
			throw new NffgVerifierException("The root element is not of type JAXBElement<nffgServiceType>");
		
		NffgServiceType nffgServiceNode = (NffgServiceType)rootObjValue;
		
		for (NffgType nffgNode : nffgServiceNode.getNffg())
			parseNffgNode(nffgNode);		
	}
	
	
	private void parseNffgNode(NffgType nffgNode) throws NffgVerifierException{
		
		String nffgName = nffgNode.getNffgName();
		if(!MyNffgReader.isNameValid(nffgName))
			throw new NffgVerifierException("Nffg name is not correct");
		HashMap<String, NodeReader> nodes = new HashMap<String, NodeReader>();
		//Calendar updateTime = nffgNode.getUpdateTime().toGregorianCalendar();
		NffgReader nrs = new MyNffgReader(nodes, nffgNode);
		nffgSet.add(nrs);
		
		HashMap<String, LinkReader> links = new HashMap<String, LinkReader>();
		
		NodesType nodesNode = nffgNode.getNodes();
		for(NodeType nodeNode : nodesNode.getNode()) {
			String nodeName = nodeNode.getNodeName();
			if(!MyNodeReader.isNameValid(nodeName))
				throw new NffgVerifierException("Node name is not correct");
			FunctionalType funcType = null;
			switch (nodeNode.getFType().getFuncType()) {
			case CACHE:
				funcType = FunctionalType.CACHE;
				break;
			case DPI:
				funcType = FunctionalType.DPI;
				break;
			case FW:
				funcType = FunctionalType.FW;
				break;
			case MAIL_CLIENT:
				funcType = FunctionalType.MAIL_CLIENT;
				break;
			case MAIL_SERVER:
				funcType = FunctionalType.MAIL_SERVER;
				break;
			case NAT:
				funcType = FunctionalType.NAT;
				break;
			case SPAM:
				funcType = FunctionalType.SPAM;
				break;
			case VPN:
				funcType = FunctionalType.VPN;
				break;
			case WEB_CLIENT:
				funcType = FunctionalType.WEB_CLIENT;
				break;
			case WEB_SERVER:
				funcType = FunctionalType.WEB_SERVER;
				break;
			default: 
				funcType = FunctionalType.WEB_CLIENT;
			}
			
			MyNodeReader node = new MyNodeReader(nodeNode, links, funcType);
			nodes.put(nodeName, node);
		}
			
			
		HashSet<MyLinkReader> lks = new HashSet<MyLinkReader>();
		
		LinksType linksNode = nffgNode.getLinks();
		for(LinkType linkNode : linksNode.getLink()) {
			String linkName = linkNode.getLinkName();
			if(!MyLinkReader.isNameValid(linkName)){
					throw new NffgVerifierException("Link name is not correct");
			}
			String sourceNode = linkNode.getSourceNode();
			if(!MyNodeReader.isNameValid(sourceNode)){
					throw new NffgVerifierException("node name is not correct");
			}
			String destNode = linkNode.getDestNode();
			if(!MyNodeReader.isNameValid(destNode)){
					throw new NffgVerifierException("node name is not correct");
			}
			MyLinkReader link = new MyLinkReader(linkNode,nodes.get(sourceNode),nodes.get(destNode));
			links.put(linkName, link);
		}
				
		java.util.List<ReachPolType> reachPolTypes = nffgNode.getPolicies().getReachabilityPolicy();
		for(ReachPolType reachPolType: reachPolTypes){
				String policyName = reachPolType.getPolicyName();
				boolean isPositive = reachPolType.isIsPositive();
				String sourceNode = reachPolType.getSourceNode();
				String destNode = reachPolType.getDestNode();
				ReachabilityPolicyReader rPolicy = new MyReachabilityPolicyReader(policyName, nrs, reachPolType.getVerificationResult(), isPositive,nffgNode,nodes.get(sourceNode), nodes.get(destNode));
				policySet.add(rPolicy);
		}
				
		java.util.List<TravPolType> travPolTypes = nffgNode.getPolicies().getTraversalPolicy();
		for(TravPolType travPolType: travPolTypes){
				String policyName = travPolType.getPolicyName();
				boolean isPositive = travPolType.isIsPositive();
				String sourceNode = travPolType.getSourceNode();
				String destNode = travPolType.getDestNode();
				TraversalPolicyReader tPolicy = new MyTraversalPolicyReader(policyName,nrs, travPolType,travPolType.getVerificationResult(), isPositive,nffgNode,nodes.get(sourceNode), nodes.get(destNode));
				policySet.add(tPolicy);
		}
						
	}
			
	
	private static Calendar parseDate(String string) throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Calendar cal = Calendar.getInstance();
		dateFormat.setTimeZone(TimeZone.getTimeZone("CEST"));
		cal.setTime(dateFormat.parse(string));
		return cal;
	}
	
	private static String formatDate(Calendar calendar) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		dateFormat.setTimeZone(calendar.getTimeZone());
		return dateFormat.format(calendar.getTime());
	}
	
	
	private static Object unmarshal(String xmlFileName, String xsdFileName, String contextPath) throws Exception {
		Unmarshaller u = null;
		try {
			JAXBContext jc = JAXBContext.newInstance(contextPath);
	        u = jc.createUnmarshaller();
		} catch (JAXBException e) {
			System.err.println("JAXBException");
			e.printStackTrace();
			throw e;
		}
        
        SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
        
        Schema schema = null;
        try {
            schema = sf.newSchema(new File(xsdFileName));
        } catch (org.xml.sax.SAXException se) {
            System.err.println("Unable to validate due to following error");
            se.printStackTrace();
            throw se;
        }
        
        u.setSchema(schema);
        
        Object rootObj = null;
		try {
			rootObj = u.unmarshal( new File( xmlFileName ) );
		} catch (JAXBException e) {
			System.err.println("JAXBException");
			e.printStackTrace();
			throw e;
			
		}
		
		if (rootObj == null)
			throw new Exception("The root object is null");
		
		return rootObj;
	}
	
}
		
		
		