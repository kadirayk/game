package rmi.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import rmi.GaMiniOsClientConfigurationService;

public class GaMiniOsClientRmiClient {

	private String host;
	private Integer port;
	private Registry registry;

	public GaMiniOsClientRmiClient(String host, Integer port) {
		this.host = host == null ? "localhost" : host; // "131.234.250.145";
		this.port = port == null ? 1099 : port;
	}

	public Double startGaClientAndEvaluate(String gaMiniOsServerIp, Integer gaMiniOsServerPort) {
		GaMiniOsClientConfigurationService service = null;

		try {
			registry = LocateRegistry.getRegistry(host, port);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		try {
			service = (GaMiniOsClientConfigurationService) registry
					.lookup(GaMiniOsClientConfigurationService.class.getSimpleName());
		} catch (RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Double score = 0.0;
		try {
			score = service.startGaClientAndEvaluate(gaMiniOsServerIp, gaMiniOsServerPort);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return score;
	}

}
