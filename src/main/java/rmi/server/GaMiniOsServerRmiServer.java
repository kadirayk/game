package rmi.server;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import rmi.GaMiniOsServerConfigurationService;
import util.GaMiniOsServerConfig;

public class GaMiniOsServerRmiServer {
	private static int PORT;
	private static String IP;
	private static Registry registry;

	public static String gaFolderPath; // C:/ga

	private static GaMiniOsServerConfig config;

	public static void startRegistry() throws RemoteException {
		// Create server registry
		registry = LocateRegistry.createRegistry(PORT);
	}

	public static void registerObject(String name, Remote remoteObj) throws RemoteException, AlreadyBoundException {
		// Bind the object in the registry.
		// It is bind with certain name.
		// Client will lookup on the registration of the name to get object.
		registry.bind(name, remoteObj);
		System.out.println("Registered: " + name + " -> " + remoteObj.getClass().getName() + "[" + remoteObj + "]");
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Server starting...");
		gaFolderPath = "./ga";
		config = GaMiniOsServerConfig.get("./rmi-server.properties");
		IP = config.getServerRmiServerIp();
		PORT = config.getServerRmiServerPort();
		System.setProperty("java.rmi.server.hostname", IP);
		startRegistry();
		registerObject(GaMiniOsServerConfigurationService.class.getSimpleName(),
				new GaMiniOsServerConfigurationServiceImpl());

		// Server was the start, and was listening to the request from the client.
		System.out.println("Server started!");
	}
}