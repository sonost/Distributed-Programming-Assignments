package it.polito.dp2.NFFG.sol1;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
//import java.lang.*;

import javax.tools.JavaCompiler;
import it.polito.dp2.NFFG.NamedEntityReader;
import it.polito.dp2.NFFG.*;

public class MyNamedEntityReader implements NamedEntityReader {
	
	private String name;
	
	public MyNamedEntityReader(String name) {
		this.name = name;
	} 
	
	@Override
	public String getName() {
		if(name != null){
			return this.name;
		}
		else{
			System.out.println("not does not exist");
			return null;
		}
	}
	
	/*public java.lang.String getName() {
		if(!isNameValid(name)){
			//return name;
			try {
				printError();
			} catch (NffgVerifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return name;
	} 
	
	public static boolean isNameValid(String namenffg) {
		String Regx = "[A-Za-z][A-Za-z0-9]*";
		return (namenffg!= null && namenffg.matches(Regx));
	}
	
	public void printError() throws NffgVerifierException{
		throw new NffgVerifierException("not valid name");
	}*/
}