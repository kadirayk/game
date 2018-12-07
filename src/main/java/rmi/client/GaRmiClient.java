package rmi.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import rmi.ConfigurationData;
import rmi.ConfigurationService;

public class GaRmiClient {

	private String host;
	private Integer port;
	private Registry registry;

	public GaRmiClient(String host, Integer port) {
		this.host = host == null ? "localhost" : host; // "131.234.250.145";
		this.port = port == null ? 1099 : port;
	}

	public Double configureAndEvaluate(ConfigurationData config) {
		ConfigurationService service = null;

		try {
			registry = LocateRegistry.getRegistry(host, port);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		try {
			service = (ConfigurationService) registry.lookup(ConfigurationService.class.getSimpleName());
		} catch (RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Double score = 0.0;
		try {
			score = service.configureAndEvaluate(config);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return score;
	}

	public String configureAndStartup(ConfigurationData config) {
		ConfigurationService service = null;

		try {
			registry = LocateRegistry.getRegistry(host, port);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		try {
			service = (ConfigurationService) registry.lookup(ConfigurationService.class.getSimpleName());
		} catch (RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String response = null;
		try {
			response = service.configureAndStartUp(config);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

}
