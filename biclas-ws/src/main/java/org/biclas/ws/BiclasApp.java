package org.biclas.ws;

import java.io.InputStream;
import java.util.Properties;

import org.biclas.domain.BiclasManager;

public class BiclasApp {
	
	public static void main(String[] args) throws Exception {
		// Check arguments
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + BiclasApp.class.getName() + "wsName wsURL OR wsName wsURL uddiURL");
			return;
		}
		
	
		String uddiURL = args[0];
		String wsName = args[1];
		String wsURL = args[2];
		
		System.out.println(BiclasApp.class.getSimpleName() + " running");	
		
		//********* Acrescentado pelo grupo
		
		BiclasManager endPoint = BiclasManager.getInstance();
		
		endPoint.setParametros(wsName, wsURL, uddiURL);
		
//		System.out.println(endPoint.getWsName());
//		System.out.println(endPoint.getWsURL());
//		System.out.println(endPoint.getUddiURL());

		// TODO start Web Service
		try {
			endPoint.start();
			endPoint.awaitConnections();
		 } finally {
			 endPoint.stop();
		 }
		
		//********* Fim Acrescentado pelo grupo
	}

}
