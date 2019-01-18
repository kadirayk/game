package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GaMiniOsClientConfigurationService extends Remote {
	
	public Double startGaClientAndEvaluate(String gaMiniOsServerIp, Integer gaMiniOsServerPort) throws RemoteException;

}
