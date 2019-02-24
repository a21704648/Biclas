//package org.biclas.ws.cli.tests;
//
//import static org.junit.Assert.*;
//
//import java.io.IOException;
//
//import org.biclas.ws.AlreadyHasBicla_Exception;
//import org.biclas.ws.BiclasPortType;
//import org.biclas.ws.BiclasService;
//import org.biclas.ws.EmailExists_Exception;
//import org.biclas.ws.InvalidEmail_Exception;
//import org.biclas.ws.InvalidStation_Exception;
//import org.biclas.ws.NoBiclaAvail_Exception;
//import org.biclas.ws.NoCredit_Exception;
//import org.biclas.ws.UserNotExists_Exception;
//import org.biclas.ws.UserView;
//import org.biclas.ws.cli.BiclasClient;
//import org.biclas.ws.cli.BiclasClientException;
//import org.junit.Test;
//
//public class BiclasClientIT {
//	
//	UserView user = null;
//	BiclasService service = new BiclasService();
//	BiclasPortType port = service.getBiclasPort();
//	
//	
//
//	@Test
//	public void activarUtilizadorTest() throws EmailExists_Exception, InvalidEmail_Exception {
//		String email = "test@t.com";
//		
//		// chamar o serviço
//		UserView uv = port.activateUser(email);
//		// associar utilizador ao browser
//		int expectCredit = 10;
//		int actualCredit = uv.getCredit();
//		assertEquals(expectCredit, actualCredit);
//		user = uv;
//		System.out.println("Teste activar ulilizador");
//		System.out.println("Esperava:" + expectCredit);
//		System.out.println("Obtive:" + actualCredit);
//		System.out.println("**************************************************");
//	}
//
//	@Test
//	public void levantarBiclaTest() throws AlreadyHasBicla_Exception, InvalidStation_Exception, NoBiclaAvail_Exception,
//			NoCredit_Exception, UserNotExists_Exception, IOException, EmailExists_Exception, InvalidEmail_Exception, BiclasClientException {
//		BiclasClient client=new BiclasClient("wwwwww");
//		String email = "testLevantar@t.com";
//		client.
//		
//		client.levantarBicla();
//		client.getCredit(email);
//		
//	
//		UserView uv = port.activateUser(email);
//		
//		String idStation = "Grupo_5_Station1";
//		
//		
//		port.rentBicla(idStation, uv.getEmail());
//		
//		
//		
//		
//
//	}
//
//}
