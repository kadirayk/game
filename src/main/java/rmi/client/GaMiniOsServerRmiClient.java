package rmi.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import rmi.ConfigurationData;
import rmi.GaMiniOsServerConfigurationService;

public class GaMiniOsServerRmiClient {

	private String host;
	private Integer port;
	private Registry registry;

	public GaMiniOsServerRmiClient(String host, Integer port) {
		this.host = host == null ? "localhost" : host; // "131.234.250.145";
		this.port = port == null ? 1099 : port;
	}

	// public Double configureAndEvaluate(ConfigurationData config) {
	// GaMiniOsServerConfigurationService service = null;
	//
	// try {
	// registry = LocateRegistry.getRegistry(host, port);
	// } catch (RemoteException e) {
	// e.printStackTrace();
	// }
	//
	// try {
	// service = (GaMiniOsServerConfigurationService)
	// registry.lookup(GaMiniOsServerConfigurationService.class.getSimpleName());
	// } catch (RemoteException | NotBoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// Double score = 0.0;
	// try {
	// score = service.configureAndEvaluate(config);
	// } catch (RemoteException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return score;
	// }

	public void configureAndStartup(ConfigurationData config) {
		GaMiniOsServerConfigurationService service = null;

		try {
			registry = LocateRegistry.getRegistry(host, port);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		try {
			service = (GaMiniOsServerConfigurationService) registry
					.lookup(GaMiniOsServerConfigurationService.class.getSimpleName());
		} catch (RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			service.configureAndStartUp(config);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Double stopServer() {
		GaMiniOsServerConfigurationService service = null;

		try {
			registry = LocateRegistry.getRegistry(host, port);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		try {
			service = (GaMiniOsServerConfigurationService) registry
					.lookup(GaMiniOsServerConfigurationService.class.getSimpleName());
		} catch (RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Double encodingError = null;
		try {
			encodingError = service.stopServer();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encodingError;
	}

	public Boolean stopServerByWindowTitle(String gameWindowTitle) {
		GaMiniOsServerConfigurationService service = null;

		try {
			registry = LocateRegistry.getRegistry(host, port);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		try {
			service = (GaMiniOsServerConfigurationService) registry
					.lookup(GaMiniOsServerConfigurationService.class.getSimpleName());
		} catch (RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Boolean response = null;
		try {
			response = service.stopServerByWindowTitle(gameWindowTitle);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	public Boolean startServer() {
		GaMiniOsServerConfigurationService service = null;

		try {
			registry = LocateRegistry.getRegistry(host, port);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		try {
			service = (GaMiniOsServerConfigurationService) registry
					.lookup(GaMiniOsServerConfigurationService.class.getSimpleName());
		} catch (RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Boolean response = null;
		try {
			response = service.startServer();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

}
