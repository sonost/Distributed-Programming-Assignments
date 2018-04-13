package it.polito.dp2.NFFG.sol1;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.print.attribute.standard.RequestingUserName;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol1.jaxb.NffgType;
import it.polito.dp2.NFFG.sol1.jaxb.VerifResType;

public class MyPolicyReader implements PolicyReader {
	
	protected NffgReader relatedNffg;
	protected VerificationResultReader verificationResult; 
	protected Boolean isPositive;
	protected String policyName;
	
	public MyPolicyReader(String policyName, NffgReader nffgReader, VerifResType verificationResult, Boolean isPositive, NffgType nffgType) {
	
		this.policyName = policyName;
		this.relatedNffg = nffgReader;
		this.isPositive= isPositive;	
		if(verificationResult != null)
			this.verificationResult = new MyVerificationResultReader(this, verificationResult, nffgType);
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
		if(relatedNffg != null )
			return this.relatedNffg;
		else
			return null;
	}

	@Override
	public Boolean isPositive() {
		if(isPositive != null)
			return isPositive;
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
 
	public static boolean isNameValid(String namenffg) {
		String Regx = "[A-Za-z][A-Za-z0-9]*";
		return (namenffg==null || namenffg.matches(Regx));
	}
	
}
