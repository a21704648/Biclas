
package org.biclas.station.ws;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.ws.Endpoint;

import pt.ulusofona.ws.uddi.UDDINaming;
import pt.ulusofona.ws.uddi.UDDINamingException;

import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

/** The endpoint manager starts and registers the service. */
public class StationEndpointManager {

	/** UDDI naming server location */
	private String uddiURL = null;
	/** Web Service name */
	private String wsName = null;

	/** Get Web Service UDDI publication name */
	public String getWsName() {
		return wsName;
	}

	/** Web Service location to publish */
	private String wsURL = null;

	/** Port implementation */
	private StationPortImpl portImpl = new StationPortImpl(this);

	// /** Obtain Port implementation */
	public StationPortType getPort() {
		return portImpl;
	}

	/** Web Service end point */
	private Endpoint endpoint = null;

	// /** UDDI Naming instance for contacting UDDI server */
	private UDDINaming uddiNaming = null;
		//
		// /** Get UDDI Naming instance for contacting UDDI server */
		UDDINaming getUddiNaming() {
		return uddiNaming;
	}

	/** output option */
	private boolean verbose = true;

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/** constructor with provided UDDI location, WS name, and WS URL */
	public StationEndpointManager(String uddiURL, String wsName, String wsURL) {
		this.uddiURL = uddiURL;
		this.wsName = wsName;
		this.wsURL = wsURL;
	}

	/** constructor with provided web service URL */
	public StationEndpointManager(String wsName, String wsURL) {
		this.wsName = wsName;
		this.wsURL = wsURL;
	}

	/* end point management */

	public void start() throws Exception {
		try {
			// publish end point (publicar servicos)
			endpoint = Endpoint.create(this.portImpl);
			if (verbose) {
				System.out.printf("Starting %s%n", wsURL);
			}
			endpoint.publish(wsURL);
		} catch (Exception e) {
			endpoint = null;
			if (verbose) {
				System.out.printf("Caught exception when starting: %s%n", e);
				e.printStackTrace();
			}
			throw e;
		}
		publishToUDDI();
		
	}

	public void awaitConnections() {
		if (verbose) {
			System.out.println("Awaiting connections");
			System.out.println("Press enter to shutdown");
		}
		try {
			System.in.read();
		} catch (IOException e) {
			if (verbose) {
				System.out.printf("Caught i/o exception when awaiting requests: %s%n", e);
			}
		}
	}

	public void stop() throws Exception {
		try {
			if (endpoint != null) {
				// stop end point
				endpoint.stop();
				if (verbose) {
					System.out.printf("Stopped %s%n", wsURL);
				}
			}
		} catch (Exception e) {
			if (verbose) {
				System.out.printf("Caught exception when stopping: %s%n", e);
			}
		}
		this.portImpl = null;
		unpublishFromUDDI();
	}

	/* UDDI */

	void publishToUDDI() throws Exception {
		// TODO
		
		System.out.printf("Publishing '%s' to UDDI at %s%n", this.wsName, uddiURL);
		uddiNaming = new UDDINaming(this.uddiURL);
		uddiNaming.rebind(this.wsName, this.wsURL);
		
	}

	void unpublishFromUDDI() throws UDDINamingException, IOException {
		// TODO

		uddiNaming.unbind(this.wsName);
		System.out.printf("Deleted '%s' from UDDI%n", this.wsName);

	}
	
	
}