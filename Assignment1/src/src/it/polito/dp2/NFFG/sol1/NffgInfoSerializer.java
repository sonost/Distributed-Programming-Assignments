package it.polito.dp2.NFFG.sol1;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import it.polito.dp2.NFFG.sol1.jaxb.*;

import javax.swing.text.html.parser.Entity;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import it.polito.dp2.NFFG.*;


public class NffgInfoSerializer{
	
	private static final String SCHEMA_FILE = "xsd" + File.separatorChar + "nffgInfo.xsd";
	private static final String SCHEMA_LOCATION = "http://www.example.org/nffgInfo nffgInfo.xsd";
	private NffgVerifier monitor;

	ObjectFactory obj = new ObjectFactory();
	NffgServiceType rootEl = obj.createNffgServiceType();
	JAXBElement<NffgServiceType> rootElement = obj.createNffgService(rootEl);
//	DateFormat dateFormat;

	public NffgInfoSerializer() throws NffgVerifierException {
		it.polito.dp2.NFFG.NffgVerifierFactory factory = it.polito.dp2.NFFG.NffgVerifierFactory.newInstance();
		monitor = factory.newNffgVerifier();
		
	}
	
	public NffgInfoSerializer(NffgVerifier monitor) {
		super();
		this.monitor = monitor;
	}

	private void buildNffgs() {
		
		Set<NffgReader> nffgs = monitor.getNffgs();
		for (NffgReader nffg : nffgs){
			
			NffgType nffgObj = obj.createNffgType();
			nffgObj.setNffgName(nffg.getName());
			nffgObj.setUpdateTime(convertDate( nffg.getUpdateTime()));
			
			NodesType nodesObj = obj.createNodesType();
			LinksType linksObj = obj.createLinksType();
			
			Set<NodeReader> nodes = nffg.getNodes();
			for(NodeReader node:nodes){
				NodeType nodeObj = obj.createNodeType();
				nodeObj.setNodeName(node.getName());
				
				FType fType = obj.createFType();
				FuncType funcTypeString;
				switch (node.getFuncType()) {
				case CACHE:
					funcTypeString = FuncType.CACHE;
					break;
				case DPI:
					funcTypeString = FuncType.DPI;
					break;
				case FW:
					funcTypeString = FuncType.FW;
					break;
				case MAIL_CLIENT:
					funcTypeString = FuncType.MAIL_CLIENT;
					break;
				case MAIL_SERVER:
					funcTypeString = FuncType.MAIL_SERVER;
					break;
				case NAT:
					funcTypeString = FuncType.NAT;
					break;
				case SPAM:
					funcTypeString = FuncType.SPAM;
					break;
				case VPN:
					funcTypeString = FuncType.VPN;
					break;
				case WEB_CLIENT:
					funcTypeString = FuncType.WEB_CLIENT;
					break;
				case WEB_SERVER:
					funcTypeString = FuncType.WEB_SERVER;
					break;
				default: 
					funcTypeString = FuncType.WEB_CLIENT;	
				}

				fType.setFuncType((FuncType.fromValue(funcTypeString.value())));
				nodeObj.setFType(fType);
				nodesObj.getNode().add(nodeObj);
				
				Set<LinkReader> links = node.getLinks();
				for(LinkReader link:links){
						LinkType linkObj = obj.createLinkType();
						linkObj.setLinkName(link.getName());
						linkObj.setSourceNode(link.getSourceNode().getName());
						linkObj.setDestNode(link.getDestinationNode().getName());
						linksObj.getLink().add(linkObj);
				}	
			}	
			
			PoliciesType policiesObj = obj.createPoliciesType();
			Set<PolicyReader> policies = monitor.getPolicies(nffg.getName());
			for (PolicyReader policy : policies){
				
				if (policy instanceof TraversalPolicyReader){
					TravPolType policyObj = obj.createTravPolType();
					policyObj = (TravPolType)buildPolicies(policy);
					policiesObj.getTraversalPolicy().add(policyObj);
				}
				else{
					ReachPolType policyObj = obj.createReachPolType();
					policyObj = buildPolicies(policy);
					policiesObj.getReachabilityPolicy().add(policyObj);
				}
			}
			
			nffgObj.setNodes(nodesObj);
			nffgObj.setLinks(linksObj);
			nffgObj.setPolicies(policiesObj);
		
			rootEl.getNffg().add(nffgObj);
		}
	}
	
	private ReachPolType buildPolicies(PolicyReader pol) {
		
		ReachPolType policyObj;
		if(pol instanceof TraversalPolicyReader){
			policyObj = obj.createTravPolType();
		}
		else{
			policyObj = obj.createReachPolType();
		}
			
		policyObj.setPolicyName(pol.getName());
		policyObj.setRelatedNffgName(pol.getNffg().getName());
		policyObj.setIsPositive(pol.isPositive());
		policyObj.setSourceNode(((ReachabilityPolicyReader)pol).getSourceNode().getName());
		policyObj.setDestNode(((ReachabilityPolicyReader)pol).getDestinationNode().getName());
			
		VerifResType verifResObj = obj.createVerifResType();
		if(pol.getResult() != null){
				
			verifResObj.setResult(pol.getResult().getVerificationResult());
			verifResObj.setMessage(pol.getResult().getVerificationResultMsg());
			verifResObj.setVerificationTime(convertDate(pol.getResult().getVerificationTime()));
				
			policyObj.setVerificationResult(verifResObj);
		}
			
		if (pol instanceof TraversalPolicyReader){
			Set<FunctionalType> fff = ((TraversalPolicyReader)pol).getTraversedFuctionalTypes();
			for(FunctionalType tt : fff){
				((TravPolType) policyObj).getTraversedFunc().add(FuncType.fromValue(tt.name()));					
			}	
		}
			
		return policyObj;
	}
		

	private void marshaller(File outFile){
		try{
			JAXBContext jc = JAXBContext.newInstance("it.polito.dp2.NFFG.sol1.jaxb");
			Marshaller m = jc.createMarshaller();
        
			SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = sf.newSchema(new File(SCHEMA_FILE));
			m.setSchema(schema);
			m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, SCHEMA_LOCATION);
			
        
			m.marshal(rootElement, outFile);
       
		} catch (JAXBException e) {
			System.out.println("JAXBException: new instance of JAXBconent cannot be created");
			e.printStackTrace();
			System.exit(1);
		} catch (SAXException e) {
			System.out.println("SAXException: Xml schema cannot be created");
			e.printStackTrace();
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		try {
			NffgInfoSerializer f = new NffgInfoSerializer();
			f.buildNffgs();
			File outFile = new File(args[0]);
			f.marshaller(outFile);
		} catch (NffgVerifierException e) {
			System.err.println("Could not instantiate nffg verifier object");
			e.printStackTrace();
			System.exit(1);
		}
	}
	private static XMLGregorianCalendar convertDate(Calendar date) {
		try {
			GregorianCalendar c=new GregorianCalendar();
			c.setTime(date.getTime());
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		} catch (DatatypeConfigurationException e) {
		    throw new Error(e);
		}
	    }
}
