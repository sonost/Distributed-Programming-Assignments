package it.polito.dp2.NFFG.sol1;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol1.jaxb.NffgType;
import it.polito.dp2.NFFG.sol1.jaxb.VerifResType;

public class MyReachabilityPolicyReader extends MyPolicyReader implements ReachabilityPolicyReader{

	//private NffgReader relatedNffg;
	private NodeReader sourceNode;
	private NodeReader destNode;
	
	public MyReachabilityPolicyReader(String policyName, NffgReader nffg, VerifResType result, Boolean isPositive, NffgType nffgType, NodeReader sourceNode, NodeReader destNode) {
		super(policyName, nffg, result, isPositive, nffgType);
		this.sourceNode = sourceNode;
		this.destNode = destNode;
	}

	@Override
	public String getName() {
		if(policyName != null)
			return this.policyName;
		else
			return null;
	}
	
	@Override
	public NffgReader getNffg() {
		if( relatedNffg!= null )
			return this.relatedNffg;
		else
			return null;
	}
	
	@Override
	public VerificationResultReader getResult() {
		if(verificationResult != null)
			return verificationResult;
		else
			return null;
	}
	
	@Override
	public NodeReader getSourceNode() {
		return this.sourceNode;
	}
	
	@Override
	public NodeReader getDestinationNode() {
		return this.destNode;
	}
	
	public static boolean isNameValid(String namenffg) {
		String Regx = "[A-Za-z][A-Za-z0-9]*";
		return (namenffg==null || namenffg.matches(Regx));
	}
	
}
