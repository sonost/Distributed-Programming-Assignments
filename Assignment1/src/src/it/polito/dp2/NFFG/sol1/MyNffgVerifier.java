package it.polito.dp2.NFFG.sol1;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.lang.model.element.Name;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol1.jaxb.LinkType;
import it.polito.dp2.NFFG.sol1.jaxb.LinksType;
import it.polito.dp2.NFFG.sol1.jaxb.NffgServiceType;
import it.polito.dp2.NFFG.sol1.jaxb.NffgType;
import it.polito.dp2.NFFG.sol1.jaxb.NodeType;
import it.polito.dp2.NFFG.sol1.jaxb.NodesType;
import it.polito.dp2.NFFG.sol1.jaxb.ReachPolType;
import it.polito.dp2.NFFG.sol1.jaxb.TravPolType;

public class MyNffgVerifier implements NffgVerifier {
	
	private Set<NffgReader> nffgs = new HashSet<NffgReader>();
	private HashSet<PolicyReader> policies = new HashSet<PolicyReader>();
	private static final String SCHEMA_FILE = "xsd" + File.separatorChar + "nffgInfo.xsd";
	//private Calendar updateTime;
	
	public MyNffgVerifier() throws NffgVerifierException {
		
		String xmlFileName = System.getProperty("it.polito.dp2.NFFG.sol1.NffgInfo.file");
		//Parser parser = new Parser(fileName, SCHEMA_FILE);
		//parser.parse();
		Object rootObj;
		try {
			rootObj = unmarshal(xmlFileName, SCHEMA_FILE, "it.polito.dp2.NFFG.sol1.jaxb");
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
		nffgs.add(nrs);
		
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
				policies.add(rPolicy);
		}
				
		java.util.List<TravPolType> travPolTypes = nffgNode.getPolicies().getTraversalPolicy();
		for(TravPolType travPolType: travPolTypes){
				String policyName = travPolType.getPolicyName();
				boolean isPositive = travPolType.isIsPositive();
				String sourceNode = travPolType.getSourceNode();
				String destNode = travPolType.getDestNode();
				TraversalPolicyReader tPolicy = new MyTraversalPolicyReader(policyName,nrs, travPolType,travPolType.getVerificationResult(), isPositive,nffgNode,nodes.get(sourceNode), nodes.get(destNode));
				policies.add(tPolicy);
		}
						
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
	
	
	@Override
	public Set<NffgReader> getNffgs() {
		if(nffgs != null)
			return nffgs;
		else
			return null;
	}
	
	@Override
	public NffgReader getNffg(String name ) {
		for(NffgReader nffg: nffgs){
			if(nffg.equals(name)){
				return nffg;
			}
		}
		return null;
	}
	
	
	@Override
	public Set<PolicyReader> getPolicies() {
		if(policies != null)
			return policies;
		else
			return null;
	}
	
	@Override
	public Set<PolicyReader> getPolicies(Calendar verificationtime) {
		HashSet<PolicyReader> matchedPolicies = new HashSet<PolicyReader>();
		for (PolicyReader policy : matchedPolicies){
			if (policy.getResult().getVerificationTime().after(verificationtime))
				matchedPolicies.add(policy);
		}
		if(matchedPolicies.isEmpty())
			return null;
		return matchedPolicies;
		}
	

	@Override
	public Set<PolicyReader> getPolicies(String namenffg) {
		
		HashSet<PolicyReader> matchedPolicies = new HashSet<PolicyReader>();
		for (PolicyReader policy :matchedPolicies) {
			if (policy.getNffg().getName().equals(namenffg))
				matchedPolicies.add(policy);
		}
		if(matchedPolicies.isEmpty())
			return null;
		return matchedPolicies;
	
	}
	
	public static boolean isNameValid(String namenffg) {
		String Regx = "[A-Za-z][A-Za-z0-9]*";
		return (namenffg==null || namenffg.matches(Regx));
	}
	
	
}
