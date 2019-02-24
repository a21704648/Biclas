package org.biclas.station.ws;


import javax.jws.WebService;

import org.biclas.station.domain.Coordinates;
import org.biclas.station.domain.Station;
import org.biclas.station.domain.exception.BadInitException;
import org.biclas.station.domain.exception.NoBiclaAvailException;
import org.biclas.station.domain.exception.NoSlotAvailException;


/**
 * This class implements the Web Service port type (interface). The annotations
 * below "map" the Java class to the WSDL definitions.
 */
// TODO
@WebService(endpointInterface = "org.biclas.station.ws.StationPortType",
wsdlLocation = "WSDL Station.wsdl",
name ="StationWebService",
portName = "StationPort",
targetNamespace="http://ws.station.biclas.org/",
serviceName = "StationService")

public class StationPortImpl  implements StationPortType {

	/**
	 * The Endpoint manager controls the Web Service instance during its whole
	 * lifecycle.
	 */
	private StationEndpointManager endpointManager;
			
	/** Constructor receives a reference to the endpoint manager. */
	public StationPortImpl(StationEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
	}

	// Main operations -------------------------------------------------------

	// /** Retrieve information about station. */
	@Override
	public StationView getInfo() {
		//********* Acrescentado pelo grupo
		//Criar uma view de Station
		StationView stationView = new StationView();
		
		CoordinatesView cv = new CoordinatesView();
		cv.setX(Station.getInstance().getCoordinates().getX());
		cv.setY(Station.getInstance().getCoordinates().getY());
		
		stationView.setId(Station.getInstance().getId()); 
		stationView.setCoordinate(cv);
		stationView.setCapacity( Station.getInstance().getMaxCapacity() );
		stationView.setTotalGets(Station.getInstance().getTotalGets());
		stationView.setTotalReturns(Station.getInstance().getTotalReturns() );
		stationView.setAvailableBiclas(Station.getInstance().getAvailablebiclas());
		stationView.setFreeDocks(Station.getInstance().getFreeDocks());
		return stationView;
		//********* Fim Acrescentado pelo grupo
	}
	//
	// /** Return a bike to the station. */
	@Override
	public int returnBicla() throws NoSlotAvail_Exception {
		//********* Acrescentado pelo grupo
		Integer returnBicha = 0;
		try {
			returnBicha =  Station.getInstance().returnBicla();
		} catch (NoSlotAvailException e) {
			// TODO Auto-generated catch block
			throwNoSlotAvail("Não tem doca disponivel na estação!");
			e.printStackTrace();
		}
		return returnBicha;
		//********* Fim Acrescentado pelo grupo
	}
	
	// /** Take a bike from the station. */
	@Override
	public void getBicla() throws NoBiclaAvail_Exception {
		//********* Acrescentado pelo grupo
		try {
			Station.getInstance().getBicla();
		} catch (NoBiclaAvailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//********* Fim Acrescentado pelo grupo
	}

	// Test Control operations -----------------------------------------------

	// /** Diagnostic operation to check if service is running. */
	@Override
	public String testPing(String inputMessage) {
		System.out.println("Ping testado.");
		// // If no input is received, return a default name.
		if (inputMessage == null || inputMessage.trim().length() == 0)
			inputMessage = "friend";
		
		//If the station does not have a name, return a default.
		String wsName = endpointManager.getWsName();
		if (wsName == null || wsName.trim().length() == 0)
			wsName = "Station";
	
		// // Build a string with a message to return.
		StringBuilder builder = new StringBuilder();
		builder.append("Hello ").append(inputMessage);
		builder.append(" from ").append(wsName);
		return builder.toString();
	}
	
	// /** Return all station variables to default values. */
	@Override
	public void testClear() {
		Station.getInstance().reset();
	}
	//
	// /** Set station variables with specific values. */
	@Override
	public void testInit(int x, int y, int capacity, int returnPrize) throws
		BadInit_Exception {
		try {
				Station.getInstance().init(x, y, capacity, returnPrize);
			} catch (BadInitException e) {
					e = new BadInitException();
					e.throwBadInit("Invalid initialization values!");
				}
	}

	// View helpers ----------------------------------------------------------

	//Descomentado
	// /** Helper to convert a domain station to a view. */
	private StationView buildStationView(Station station) {
		StationView view = new StationView();
		view.setId(station.getId());
		view.setCoordinate(buildCoordinatesView(station.getCoordinates()));
		view.setCapacity(station.getMaxCapacity());
		view.setTotalGets(station.getTotalGets());
		view.setTotalReturns(station.getTotalReturns());
		view.setFreeDocks(station.getFreeDocks());
		view.setAvailableBiclas(station.getAvailablebiclas());
		return view;
	}
	
	//Descomentado
	// /** Helper to convert a domain coordinates to a view. */
	private CoordinatesView buildCoordinatesView(Coordinates coordinates) {
		CoordinatesView view = new CoordinatesView();
		view.setX(coordinates.getX());
		view.setY(coordinates.getY());
		return view;
	}

	// Exception helpers -----------------------------------------------------

	//Descomentado
	/** Helper to throw a new NoBiclaAvail exception. */
	private void throwNoBiclaAvail(final String message) throws
		NoBiclaAvail_Exception {
		NoBiclaAvail faultInfo = new NoBiclaAvail();
		faultInfo.message = message;
		throw new NoBiclaAvail_Exception(message, faultInfo);
	}
	
	//Descomentado
	/** Helper to throw a new NoSlotAvail exception. */
	private void throwNoSlotAvail(final String message) throws
		NoSlotAvail_Exception {
		NoSlotAvail faultInfo = new NoSlotAvail();
		faultInfo.message = message;
		throw new NoSlotAvail_Exception(message, faultInfo);
	}
	
	//Descomentado
	/** Helper to throw a new BadInit exception. */
	private void throwBadInit(final String message) throws BadInit_Exception {
		BadInit faultInfo = new BadInit();
		faultInfo.message = message;
		throw new BadInit_Exception(message, faultInfo);
	}

}
