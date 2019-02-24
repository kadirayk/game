package rmi.server;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import rmi.GaMiniOsClientConfigurationService;
import util.GaMiniOsServerConfig;

public class GaMiniOsClientRmiServer {
	private static int PORT;
	private static String IP;
	private static Registry registry;
	public static String GADir;

	private static GaMiniOsServerConfig config;

	public static void startRegistry() throws RemoteException {
		// Create server registry
		registry = LocateRegistry.createRegistry(PORT);
	}

	public static void registerObject(String name, Remote remoteObj) throws RemoteException, AlreadyBoundException {
		System.setProperty("java.rmi.server.hostname", "localhost");
		// Bind the object in the registry.
		// It is bind with certain name.
		// Client will lookup on the registration of the name to get object.
		registry.bind(name, remoteObj);
		System.out.println("Registered: " + name + " -> " + remoteObj.getClass().getName() + "[" + remoteObj + "]");
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.out.println("give ga path as arg");
			return;
		}
		GADir = args[0];
		config = GaMiniOsServerConfig.get("./rmi-server.properties");
		IP = config.getClientRmiServerIp();
		PORT = config.getClientRmiServerPort();
		System.out.println("Server starting with ga dir:" + GADir);
		System.setProperty("java.rmi.server.hostname", IP);
		startRegistry();
		registerObject(GaMiniOsClientConfigurationService.class.getSimpleName(),
				new GaMiniOsClientConfigurationServiceImpl());

		// Server was the start, and was listening to the request from the client.
		System.out.println("Server started!");
	}
}
