package it.polito.dp2.NFFG.sol1;

import java.util.*;
import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol1.jaxb.FuncType;
import it.polito.dp2.NFFG.sol1.jaxb.NffgType;
import it.polito.dp2.NFFG.sol1.jaxb.TravPolType;
import it.polito.dp2.NFFG.sol1.jaxb.VerifResType;

public class MyTraversalPolicyReader extends MyReachabilityPolicyReader implements TraversalPolicyReader{
	
	private HashSet<FunctionalType> functionalTypes = new HashSet<FunctionalType>();
	
	public MyTraversalPolicyReader(String policyName, NffgReader nffg, TravPolType travPolType, VerifResType result, Boolean isPositive,NffgType nffgType, NodeReader sourceNode, NodeReader destNode) {
		super(policyName, nffg, result, isPositive,nffgType, sourceNode, sourceNode);
		
		FunctionalType functionalType;
		for (FuncType traversedFunc : travPolType.getTraversedFunc()) {
			
			switch (traversedFunc) {
			case CACHE:
				functionalType = FunctionalType.CACHE;
				functionalTypes.add(functionalType);
				break;
			case DPI:
				//functionalType = FunctionalType.DPI;
				functionalTypes.add(FunctionalType.DPI);
				break;
			case FW:
				functionalType = FunctionalType.FW;
				functionalTypes.add(functionalType);
				break;
			case MAIL_CLIENT:
				functionalType = FunctionalType.MAIL_CLIENT;
				functionalTypes.add(functionalType);
				break;
			case MAIL_SERVER:
				functionalType = FunctionalType.MAIL_SERVER;
				functionalTypes.add(functionalType);
				break;
			case NAT:
				functionalType = FunctionalType.NAT;
				functionalTypes.add(functionalType);
				break;
			case SPAM:
				functionalType = FunctionalType.SPAM;
				functionalTypes.add(functionalType);
				break;
			case VPN:
				functionalType = FunctionalType.VPN;
				functionalTypes.add(functionalType);
				break;
			case WEB_CLIENT:
				//functionalType = FunctionalType.WEB_CLIENT;
				functionalTypes.add(FunctionalType.WEB_CLIENT);
				break;
			case WEB_SERVER:
				functionalType = FunctionalType.WEB_SERVER;
				functionalTypes.add(functionalType);
				break;
			default: 
				functionalType = FunctionalType.WEB_CLIENT;
				functionalTypes.add(functionalType);
			}
		}	
	}
	
	@Override
	public String getName() {
		if(policyName != null)
			return this.policyName;
		else
			return null;
	}
	
	@Override
	public Set<FunctionalType> getTraversedFuctionalTypes() {
		if(functionalTypes != null)
			return this.functionalTypes;
		else
			return null;
	}
	
	public static boolean isNameValid(String namenffg) {
		String Regx = "[A-Za-z][A-Za-z0-9]*";
		return (namenffg==null || namenffg.matches(Regx));
	}
}
