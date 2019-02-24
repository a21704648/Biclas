package org.biclas.station.ws;


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.biclas.station.domain.Station;

/**
 * The application is where the service starts running. The program arguments
 * are processed here. Other configurations can also be done here.
 */
public class StationApp {

	private static Scanner input;

	public static void main(String[] args) throws Exception {
		input = new Scanner (System.in);
		
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + StationApp.class.getName() + "wsName wsURL OR wsName wsURL uddiURL");
			return;
		}
		
	
		String wsName = args[0];
		String wsURL = args[1];
		String uddiURL = args[2];
		
		// TODO handle UDDI arguments

		
		StationEndpointManager endpoint = new StationEndpointManager(uddiURL, wsName, wsURL);
		Station.getInstance().setId(wsName);

		
		System.out.print("cordenada X: ");
		String coordX = input.nextLine();
		System.out.print("cordenada Y: ");
		String coordY = input.nextLine();
		System.out.print(" Capacidade: ");
		String capacity = input.nextLine();
		System.out.print(" Bonus: ");
		String returnPrize = input.nextLine();
		Station.getInstance().init(Integer.parseInt(coordX), Integer.parseInt(coordY), 
				Integer.parseInt(capacity), Integer.parseInt(returnPrize));

		System.out.println(StationApp.class.getSimpleName() + " running");
		
		// TODO start Web Service
		try {
			endpoint.start();
			endpoint.awaitConnections();
		 } finally {
			 endpoint.stop();
		 }

	}

}