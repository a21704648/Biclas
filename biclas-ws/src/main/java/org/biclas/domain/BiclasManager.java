package org.biclas.domain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import javax.xml.ws.Endpoint;

import org.biclas.station.ws.cli.StationClient;
import org.biclas.station.ws.cli.StationClientException;
import org.biclas.ws.BiclasApp;
import org.biclas.ws.BiclasPortType;
import org.biclas.ws.UserView;
import org.omg.CORBA.portable.InputStream;

import pt.ulusofona.ws.uddi.UDDINaming;
import pt.ulusofona.ws.uddi.UDDINamingException;


public class BiclasManager {
	
	private String id;
	
	//********* Acrescentado pelo grupo
	
	/** UDDI naming server location */
	private String uddiURL = null;
	/** Web Service name */
	private String wsName = null;

	InputStream inputStream = (InputStream) BiclasApp.class.getResourceAsStream("/biclas.properties");
	Properties properties = new Properties();
	
	/** Get Web Service UDDI publication name */
	public String getWsName() {
		return wsName;
	}

	/** Web Service location to publish */
	private String wsURL = null;
	
	
	//********* Acrescentado pelo grup
	
	Map<String, UserView> users;
	//********* Fim Acrescentado pelo grupo
	
//	// Singleton -------------------------------------------------------------
//	/**
//	 * SingletonHolder is loaded on the first execution of Singleton.getInstance()
//	 * or the first access to SingletonHolder.INSTANCE, not before.
//	 */
	private static class SingletonHolder {
		private static final BiclasManager INSTANCE = new BiclasManager();
	}

	public static synchronized BiclasManager getInstance() {
		return SingletonHolder.INSTANCE;
	}
	public BiclasManager() {
		// TODO Auto-generated constructor stub
	}
	
	/** Port implementation */
	public BiclasPortImpl portImpl = new BiclasPortImpl(this);

	// /** Obtain Port implementation */
	public BiclasPortType getPort() {
		return portImpl;
	}
	
	/** Web Service end point */
	private Endpoint endpoint = null;
	
	// /** UDDI Naming instance for contacting UDDI server */
	private UDDINaming uddiNaming = null;
		//
		// /** Get UDDI Naming instance for contacting UDDI server */
	public UDDINaming getUddiNaming() {
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
	
	public void setParametros(String wsName, String wsURL, String uddiURL) throws StationClientException {
 		this.wsName = wsName;
 		this.wsURL = wsURL;
 		this.uddiURL = uddiURL;
 		users = new HashMap<String, UserView>();
 	}
	
	public void registarUser(UserView user) {
		this.users.put(user.getEmail(), user);
	}
	
	public UserView getUser(String email) {
		return users.get(email);
	}
	public String getId() {
    	return wsName;
    }
	
	/* end point management */

	public String getUddiURL() {
		return uddiURL;
	}

	public void setUddiURL(String uddiURL) {
		this.uddiURL = uddiURL;
	}

	public String getWsURL() {
		return wsURL;
	}

	public void setWsURL(String wsURL) {
		this.wsURL = wsURL;
	}

	public void setWsName(String wsName) {
		this.wsName = wsName;
	}

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
		//********* Acrescentado pelo grupo
		System.out.printf("Publishing '%s' to UDDI at %s%n", this.wsName, uddiURL);
		uddiNaming = new UDDINaming(this.uddiURL);
		uddiNaming.rebind(this.wsName, this.wsURL);
		guardar_publicacao(wsName, wsURL);
		//********* Fim Acrescentado pelo grupo
	}

	void unpublishFromUDDI() throws UDDINamingException, IOException {
		// TODO
		//********* Acrescentado pelo grupo
		uddiNaming.unbind(this.wsName);
		System.out.printf("Deleted '%s' from UDDI%n", this.wsName);
		eliminarDoFicheiro(wsName);
		//********* Fim Acrescentado pelo grupo
	}
	
	public StationClient getStation(String stationId) throws IOException, StationClientException, UDDINamingException {
		List<String> lista = getStations();
		
		for(String linha : lista) {
			String[] dados = linha.split("--");
			//caso seja correto que corresponde ao id passado no argumento
			//StationClient stationClient = new StationClient(biclasManager.getUddiURL(), dados[0]);
			//org.biclas.station.ws.StationView stationView = stationClient.getInfo();
			//String idStationReal = stationView.getId();
			
			//caso seja correto que corresponde ao id passado no argumento
			if(dados[0].equals(stationId)) {
				return new StationClient(getUddiURL(), dados[0]);
			}
		}
		return null;
	}

	public List<String> getStations() throws IOException{
		//tem que existir sempre pelos menos uma estação
		List<String> lista = obter_publicacao();
		List<String> stationsUteis = new ArrayList<String>();
		
		for(String linha : lista) {
			String[] dados = linha.split("--");
			//caso os servidor não seja biclas
			if(!dados[0].equals(getWsName())) {
				stationsUteis.add(linha);
			}
		}
		return stationsUteis;
	}
	
	
	static String nomeFicheiro = "../publicacoes.txt";	

	public void guardar_publicacao(String wsName, String wsURL) {
		String line;
		boolean existe = false;
		
		try{
	            FileWriter fw = new FileWriter(nomeFicheiro, true);
	            BufferedWriter bw = new BufferedWriter(fw); 
	            
	            FileReader fr = new FileReader(nomeFicheiro);
	    		BufferedReader br = new BufferedReader(fr);
	    		
	            while ((line = br.readLine()) != null) {
	                if(line.equals(wsName+"--"+wsURL)) {
	                	existe = true;
	                }
	            }
	            
	            if(!existe) {
	            	bw.write(wsName + "--" + wsURL);
		            bw.newLine();
	            }
	            
	            bw.close();
	            System.out.println("-> Registo salvo com sucesso.");
	        }catch (IOException ex){
	            System.out.println("Erro.: Não foi possivel gravar os dados");
	        }
	}
	
	public List<String> obter_publicacao() throws IOException {
		
		String linha;
		List<String> lista = new ArrayList<>();
		
		FileReader fr = new FileReader(nomeFicheiro);
		BufferedReader br = new BufferedReader(fr);
		//enquanto houver mais linhas
		while (br.ready()) {
			//lê a proxima linha
			linha = br.readLine();
			lista.add(linha);
		}
		
		br.close();
		System.out.println("-> Registo carregados com sucesso.");
		
		return lista;
	}
	
	public void eliminarFicheiro() {
		File arquivo = new File( nomeFicheiro);
		arquivo.delete();
		System.out.println("Arquivo apagado com sucesso.");
	}
	
	public void eliminarDoFicheiro(String wsName) throws IOException {
		List<String> lista = new ArrayList<>();
		lista = obter_publicacao();
		for (String string : lista) {
			if(string.contains(wsName)) {
				lista.remove(string);
				eliminarFicheiro();
				try{
		            FileWriter fw = new FileWriter(nomeFicheiro);
		            BufferedWriter bw = new BufferedWriter(fw); 
		           
		            for(String linha : lista) {
		            	System.out.println(linha);
		            }
		            
		            for(String linha : lista) {
		            	bw.write(linha);
			            bw.newLine();
		            }
	            	
		            bw.close();
		            System.out.println("-> Registo salvo com sucesso.");
		        }catch (IOException ex){
		            System.out.println("Erro.: Não foi possivel gravar os dados");
		        }
			}
		}
	}
}
