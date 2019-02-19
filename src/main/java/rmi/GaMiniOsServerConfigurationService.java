package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GaMiniOsServerConfigurationService extends Remote {

//	public Double configureAndEvaluate(ConfigurationData config) throws RemoteException;

	public Double configureAndStartUp(ConfigurationData config) throws RemoteException;
	
	public Boolean stopServer() throws RemoteException;
	
	public Boolean stopServerByWindowTitle(String gameWindowTitle) throws RemoteException;
	
	public Boolean startServer() throws RemoteException;

}
