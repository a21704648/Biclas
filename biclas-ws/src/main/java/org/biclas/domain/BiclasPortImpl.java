package org.biclas.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import org.biclas.station.ws.NoSlotAvail_Exception;
import org.biclas.station.ws.cli.StationClient;
import org.biclas.station.ws.cli.StationClientException;
import org.biclas.ws.AlreadyHasBicla_Exception;
import org.biclas.ws.BadInit_Exception;
import org.biclas.ws.BiclasPortType;
import org.biclas.ws.CoordinatesView;
import org.biclas.ws.EmailExists_Exception;
import org.biclas.ws.FullStation_Exception;
import org.biclas.ws.GetInfoStation;
import org.biclas.ws.InvalidEmail_Exception;
import org.biclas.ws.InvalidStation_Exception;
import org.biclas.ws.NoBiclaAvail_Exception;
import org.biclas.ws.NoBiclaRented_Exception;
import org.biclas.ws.NoCredit_Exception;
import org.biclas.ws.StationView;
import org.biclas.ws.UserNotExists_Exception;
import org.biclas.ws.UserView;

import pt.ulusofona.ws.uddi.UDDINamingException;

@WebService(endpointInterface = "org.biclas.ws.BiclasPortType",
wsdlLocation = "WSDL Biclas.wsdl",
name ="BiclasWebService",
portName = "BiclasPort",
targetNamespace="http://ws.biclas.org/",
serviceName = "BiclasService")

public class BiclasPortImpl implements BiclasPortType{

	BiclasManager biclasManager;
	GetInfoStation infoStation;
	
	public BiclasPortImpl(BiclasManager biclasManager)  {
		this.biclasManager = biclasManager;
		this.infoStation = new GetInfoStation();
	}

	@Override
	public List<StationView> listStations(Integer numberOfStations, CoordinatesView coordinates) {
		
		List<StationView> stations = new ArrayList<>();
		
		List<String> lista = null;
		try {
			lista = biclasManager.getStations();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(String linha : lista) {
			String[] dados = linha.split("--");
			System.out.println("-> " + dados[0]);
			
			StationView sv = new StationView();
			sv.setId(dados[0]);
			stations.add(sv);
		}
		return stations;
	}

	@Override
	public StationView getInfoStation(String stationId) throws InvalidStation_Exception {
		org.biclas.station.ws.StationView stationView;
		StationView s2 = new StationView();
		try {
			System.out.println("antes "+ stationId);
			stationView = biclasManager.getStation(stationId).getInfo();
			System.out.println("depois "+ stationView.getId() );
			
			CoordinatesView  cv = new CoordinatesView();
			cv.setX(stationView.getCoordinate().getX());
			cv.setY(stationView.getCoordinate().getY());
			s2.setCoordinate(cv);
			
			s2.setTotalGets(stationView.getTotalGets());
			s2.setTotalReturns(stationView.getTotalReturns());
			s2.setAvailableBiclas(stationView.getAvailableBiclas());
			s2.setFreeDocks(stationView.getFreeDocks());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StationClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UDDINamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s2;	
	}
	
	@Override
	public int getCredit(String email) throws UserNotExists_Exception {
		return  biclasManager.getUser(email).getCredit();
	}

	@Override
	public UserView activateUser(String email) throws EmailExists_Exception, InvalidEmail_Exception {
		//********* Acrescentado pelo grupo
		UserView userView = new UserView();
		userView.setEmail(email);
		userView.setHasBicla(false);
		//set saldo inical
		biclasManager.registarUser(userView);
		userView.setCredit(10);
		return userView;
		//********* Fim Acrescentado pelo grupo
	}

	@Override
	public void rentBicla(String stationId, String email) throws AlreadyHasBicla_Exception, InvalidStation_Exception,
	NoBiclaAvail_Exception, NoCredit_Exception, UserNotExists_Exception {
	// TODO Auto-generated method stub
	//********* Acrescentado pelo grupo
	org.biclas.station.ws.StationView stationView;
	StationView s2 = new StationView();
	System.out.println("Rent");
	try {
		stationView = biclasManager.getStation(stationId).getInfo();
		StationClient sc = new StationClient(biclasManager.getUddiURL(), stationView.getId());
		try {
			
			
			//retirar bicla de uma doca
			sc.getBicla();
			//atribuir uma bicla ao user
			if(biclasManager.getUser(email).getCredit() > 0) {
				System.out.println(email);
				System.out.println(biclasManager.getUser(email));	
				System.out.println(biclasManager.getUser(email).isHasBicla());
				
				if(!biclasManager.getUser(email).isHasBicla()) {
					
					if(biclasManager.getStation(stationId).getInfo().getAvailableBiclas() > 0) {
						biclasManager.getUser(email).setHasBicla(true);
						biclasManager.getUser(email).setCredit( biclasManager.getUser(email).getCredit() - 1 );
						System.out.println("|-| " + email + " reservou um bicla.");
					}
				}
			}
			
		} catch (org.biclas.station.ws.NoBiclaAvail_Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		} catch (StationClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UDDINamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	//********* Fim Acrescentado pelo grupo
	}
	

	@Override
	public void returnBicla(String stationId, String email)
			throws FullStation_Exception, InvalidStation_Exception, NoBiclaRented_Exception, UserNotExists_Exception {
		// TODO Auto-generated method stub
		//********* Acrescentado pelo grupo
		int bonus = 0;
		org.biclas.station.ws.StationView stationView;
		StationView s2 = new StationView();
		try {
			stationView = biclasManager.getStation(stationId).getInfo();
			StationClient sc = new StationClient(biclasManager.getUddiURL(), stationView.getId());
			if(biclasManager.getUser(email).isHasBicla()) {
				//por bicla na doca
				if(biclasManager.getStation(stationId).getInfo().getFreeDocks() > 0) {
					try {
						bonus =  sc.returnBicla();
					} catch (NoSlotAvail_Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					biclasManager.getUser(email).setHasBicla(false);
					biclasManager.getUser(email).setCredit( biclasManager.getUser(email).getCredit() + bonus );
					
				}
			}
			
			} catch (StationClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UDDINamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//********* Fim Acrescentado pelo grupo
	}

	
	@Override
	public String testPing(String inputMessage) {
		//********* Acrescentado pelo grupo
		
		// If no input is received, return a default name.
		if (inputMessage == null || inputMessage.trim().length() == 0)
			inputMessage = "friend";
		
		//If the station does not have a name, return a default.
		String wsName = biclasManager.getWsName();
		if (wsName == null || wsName.trim().length() == 0)
			wsName = "Biclas";
	
		// // Build a string with a message to return.
		StringBuilder builder = new StringBuilder();
		builder.append("Hello ").append(inputMessage);
		builder.append(" from ").append(wsName).append("\n");
		//*************************************************************
		//pingar a Stations
		
		List<String> lista = null;
		try {
			lista = biclasManager.obter_publicacao();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for(String linha : lista) {
			//separar o nomews da urlws
			String[] dados = linha.split("--");
			
			//caso nomes de wsBiclas não estiver na lista
			if(!dados[0].equals(biclasManager.getWsName())) {
				
				StationClient client;
				try {
					//passar wsURL como paramentro
					client = new StationClient(dados[1]);
					String supplierPingResult = client.testPing("client");
					builder.append("\n").append(supplierPingResult);
					builder.append("\n");
				} catch (StationClientException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
		}
		return builder.toString();
		//********* Fim Acrescentado pelo grupo
	}

	@Override
	public void testClear() {
		//********* Acrescentado pelo grupo
		//biclasManager.getInstance().reset();
		//********* Fim Acrescentado pelo grupo
	}

	@Override
	public void testInitStation(String stationId, int x, int y, int capacity, int returnPrize)
			throws BadInit_Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void testInit(int userInitialPoints) throws BadInit_Exception {
		// TODO Auto-generated method stub
		
	}
	
	
}
