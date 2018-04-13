package it.polito.dp2.NFFG.sol1;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.datatype.XMLGregorianCalendar;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol1.jaxb.NffgType;
import it.polito.dp2.NFFG.sol1.jaxb.VerifResType;

public class MyVerificationResultReader implements VerificationResultReader {
	
	private PolicyReader policy;
	private String verificationResMsg;
	private Boolean isTrue;
	private XMLGregorianCalendar verificationTime;
	
	public MyVerificationResultReader(MyPolicyReader policy, VerifResType verifResType, NffgType nffgType) {
		this.policy = policy;
		this.verificationResMsg = verifResType.getMessage();
		this.isTrue = verifResType.isResult();
		this.verificationTime= verifResType.getVerificationTime();	
	}
	
	@Override
	public PolicyReader getPolicy() {
		if(policy != null)
			return policy;
		else {
			return null;
		}
	}
	
	@Override
	public String getVerificationResultMsg() {
		if(verificationResMsg != null)
			return verificationResMsg;
		else
			return null;
	}

	@Override
	public Boolean getVerificationResult() {
		if(isTrue != null)
			return isTrue;
		else
			return null;
	}
	
	@Override
	public Calendar getVerificationTime() {
		if(verificationTime != null)
			return verificationTime.toGregorianCalendar();
		else
			return null;
	}
	
	public static boolean isMessageValid(String verificationResMsg) {
		String Regx = "[A-Za-z0-9]*";
		return (verificationResMsg==null||verificationResMsg.matches(Regx));
	}
	
}
