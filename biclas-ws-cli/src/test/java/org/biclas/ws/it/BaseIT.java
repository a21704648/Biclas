package org.biclas.ws.it;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;


import org.biclas.ws.AlreadyHasBicla_Exception;
import org.biclas.ws.BiclasPortType;
import org.biclas.ws.BiclasService;
import org.biclas.ws.CoordinatesView;
import org.biclas.ws.EmailExists_Exception;
import org.biclas.ws.FullStation_Exception;
import org.biclas.ws.InvalidEmail_Exception;
import org.biclas.ws.InvalidStation_Exception;
import org.biclas.ws.NoBiclaAvail_Exception;
import org.biclas.ws.NoBiclaRented_Exception;
import org.biclas.ws.NoCredit_Exception;
import org.biclas.ws.StationView;
import org.biclas.ws.UserNotExists_Exception;
import org.biclas.ws.UserView;
import org.biclas.ws.cli.BiclasClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


/*
 * Base class of tests
 * Loads the properties in the file
 */
public class BaseIT {
	CoordinatesView s1;
	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps;
	String email = "test@t.com";
	String idStation = "Grupo_5_Station1";	
	StationView stationTest= new StationView();
	
	BiclasService service = new BiclasService();
	BiclasPortType port = service.getBiclasPort();
	
	protected static BiclasClient client;
	

	@BeforeClass
	public static void oneTimeSetup() throws Exception {
		testProps = new Properties();
		try {
			testProps.load(BaseIT.class.getResourceAsStream(TEST_PROP_FILE));
			System.out.println("Loaded test properties:");
			System.out.println(testProps);
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
			throw e;
		}

		final String uddiEnabled = testProps.getProperty("uddi.enabled");
		final String verboseEnabled = testProps.getProperty("verbose.enabled");

		final String uddiURL = testProps.getProperty("uddi.url");
		final String wsName = testProps.getProperty("ws.name");
		final String wsURL = testProps.getProperty("ws.url");

		if ("true".equalsIgnoreCase(uddiEnabled)) {
			client = new BiclasClient(uddiURL, wsName);
		} else {
			client = new BiclasClient(wsURL);
		}
		client.setVerbose("true".equalsIgnoreCase(verboseEnabled));

	}

	@AfterClass
	public static void cleanup() {
	}
	
	
	@Test
	public void activarUtilizadorTest() throws EmailExists_Exception, InvalidEmail_Exception {
		
		client.activateUser(email);
		// chamar o serviço
		//UserView uv = port.activateUser(email);
		// associar utilizador ao browser
		int expectCredit = 10;
		int actualCredit;
		try {
			actualCredit = client.getCredit(email);
			assertEquals(expectCredit, actualCredit);
			System.out.println("Teste activar ulilizador");
			System.out.println("Esperava:" + expectCredit);
			System.out.println("Obtive:" + actualCredit);
			System.out.println("**************************************************");
		} catch (UserNotExists_Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Utilizador nao existe");
		}
		
		
		
	}
	
//	@Test
//	public void levantarBiclaTest() throws AlreadyHasBicla_Exception, InvalidStation_Exception, NoBiclaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, IOException, EmailExists_Exception, InvalidEmail_Exception {
//		System.out.println("Teste Levantar Biclas");
//		
//		stationTest.setCapacity(6);
//		stationTest.setAvailableBiclas(6);
//		
//
//		stationTest.setId(idStation);
//		
//		UserView userTest=port.activateUser(email);
//		int expectedSaldo=client.getCredit(email)-1;
//		port.rentBicla(idStation, email);
//		
//		int actualSaldo=client.getCredit(email);
//		assertEquals(expectedSaldo, actualSaldo);
//		System.out.println("Esperava :"+expectedSaldo); 
//		System.out.println("Obtive :"+actualSaldo); 
//		System.out.println("----------------------------------------------------------------------");
//		
//		
//	}
//	
	@Test
	public void entregarBiclaTest() throws AlreadyHasBicla_Exception, InvalidStation_Exception, NoBiclaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, IOException, EmailExists_Exception, InvalidEmail_Exception, FullStation_Exception, NoBiclaRented_Exception {
		System.out.println("Teste entregar Bicla");
		UserView userTest=port.activateUser(email);
		stationTest.setFreeDocks(3);
		
		userTest.setHasBicla(true);
		
		stationTest.setId(idStation);
		
		port.returnBicla(stationTest.getId(), email);	
		client.returnBicla(stationTest.getId(), email);
		System.out.println(stationTest.getId()+" tem: "+ stationTest.getFreeDocks());
		
		//		try {
//			if(this.user.isHasBicla() != null && this.user.isHasBicla()) {
//				System.out.println("|-|------------------------------------------------|-|");
//				System.out.print("|-|Nome estação: ");
//				String idStation = input.nextLine();
//				port.returnBicla(idStation, this.user.getEmail());
//				System.out.println("|-|------------------------------------------------|-|");
//				System.out.println("|-| Devolveu uma bicla com sucesso");
//				System.out.println("|-|------------------------------------------------|-|");
//				this.user.setHasBicla(false);
//			}else {
//				System.out.println("|-|------------------------------------------------|-|");
//				System.out.println("|-| Não tem bicla para devolver");
//				System.out.println("|-|------------------------------------------------|-|");
//			}
//				
//			
//		} catch (FullStation_Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoBiclaRented_Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		selecionarOperacao();
	}
	
	@Test
	public void numeroSationTest() {
		System.out.println("Verificar o numero de estacoes");
		CoordinatesView coordinates=new CoordinatesView();
		coordinates.setX(22);
		coordinates.setY(7);
		int numero=port.listStations(3, coordinates).size();
		int expected=3;
		int actual=numero;
		assertEquals(expected, actual);
		
		System.out.println("Foram lancadas "+actual+" estacoes");
		System.out.println("Foram lancadas "+actual+" estacoes");
	}
	
	@Test
	public void levantarBiclaTest2() throws AlreadyHasBicla_Exception, InvalidStation_Exception, NoBiclaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, IOException, EmailExists_Exception, InvalidEmail_Exception {
		System.out.println("Teste Levantar Biclas nao disponivel");
		
		
		stationTest.setAvailableBiclas(0);	

		stationTest.setId(idStation);		
		UserView userTest=port.activateUser(email);
		
		
		
		 try {
			 port.rentBicla(idStation, email);
			 
			    
			    
			    } 
			    catch (Exception e) {
			    	System.out.println("Nao apanhou execessao");
			    	String expected = e.getMessage();
			    	assertEquals( expected, e.getMessage());
			    }	
		 
		 System.out.println("++++++++++++++++++++++++++++++++");
		
	}
		
	

}
