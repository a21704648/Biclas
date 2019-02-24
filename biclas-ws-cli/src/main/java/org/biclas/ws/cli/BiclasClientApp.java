package org.biclas.ws.cli;

import java.util.Scanner;


public class BiclasClientApp {

    public static void main(String[] args) throws Exception {
    	Scanner input = new Scanner(System.in);
    	
        // Check arguments
        if (args.length == 0) {
            System.err.println("Argument(s) missing!");
            System.err.println("Usage: java " + BiclasClientApp.class.getName()
                    + " wsURL OR uddiURL wsName");
            return;
        }
        String uddiURL = null;
        String wsName = null;
        String wsURL = null;
        if (args.length == 1) {
            wsURL = args[0];
        } else if (args.length >= 2) {
            uddiURL = args[0];
            wsName = args[1];
        }

		System.out.println(BiclasClientApp.class.getSimpleName() + " running");

        // Create client
        BiclasClient client = null;

        
        if (wsURL != null) {
            System.out.printf("Creating client for server at %s%n", wsURL);
            client = new BiclasClient(wsURL);
        } else if (uddiURL != null) {
            System.out.printf("Creating client using UDDI at %s for server with name %s%n",
                uddiURL, wsName);
            client = new BiclasClient(uddiURL, wsName);
        }
 
        // the following remote invocations are just basic examples
        // the actual tests are made using JUnit

	//	System.out.println("Invoke ping()...");
	//	String result = client.testPing("client");
	//	System.out.println("Result: " + result);
        
		client.selecionarOperacao();
	 }
}

