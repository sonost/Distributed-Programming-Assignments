package it.polito.dp2.NFFG.sol2;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.lab2.ReachabilityTester;
import it.polito.dp2.NFFG.lab2.ReachabilityTesterException;

public class ReachabilityTesterFactory extends it.polito.dp2.NFFG.lab2.ReachabilityTesterFactory {
	
	private NffgVerifier nffgVerifier;
	
	@Override
	public ReachabilityTester newReachabilityTester() throws ReachabilityTesterException {
		
		// TODO Auto-generated method stub
		it.polito.dp2.NFFG.NffgVerifierFactory factory = it.polito.dp2.NFFG.NffgVerifierFactory.newInstance();
		String baseUrl = "it.polito.dp2.NFFG.lab2.URL";
		String actualUrl = System.getProperty(baseUrl);
		
		try {
			nffgVerifier = factory.newNffgVerifier();
			return new MyReachabilityTester(nffgVerifier,actualUrl);
		} catch (NffgVerifierException e) {
			// TODO: handle exception
			throw new ReachabilityTesterException("NffgVerifierException: " + e.getMessage());
		}
	}
}

