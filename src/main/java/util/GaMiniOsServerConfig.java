package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.aeonbits.owner.Accessible;
import org.aeonbits.owner.ConfigFactory;
import org.aeonbits.owner.Mutable;

public interface GaMiniOsServerConfig extends Mutable, Accessible {

	public static final String SERVER_RMI_SERVER_IP = "server.rmi_server_ip";
	public static final String SERVER_RMI_SERVER_PORT = "server.rmi_server_port";
	public static final String CLIENT_RMI_SERVER_IP = "client.rmi_server_ip";
	public static final String CLIENT_RMI_SERVER_PORT = "client.rmi_server_port";
	public static final String SCREEN_SIZE = "screen_size";

	@Key(SERVER_RMI_SERVER_IP)
	@DefaultValue("localhost")
	public String getServerRmiServerIp();

	@Key(SERVER_RMI_SERVER_PORT)
	@DefaultValue("1099")
	public Integer getServerRmiServerPort();

	@Key(CLIENT_RMI_SERVER_IP)
	@DefaultValue("localhost")
	public String getClientRmiServerIp();

	@Key(CLIENT_RMI_SERVER_PORT)
	@DefaultValue("1099")
	public Integer getClientRmiServerPort();

	@Key(SCREEN_SIZE)
	@DefaultValue("100")
	public Double getScreenSize();

	public static GaMiniOsServerConfig get(String file) {
		return get(new File(file));
	}

	public static GaMiniOsServerConfig get(File file) {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			System.err.println("Could not find config file " + file + ". Assuming default configuration");
		} catch (IOException e) {
			System.err.println("Encountered problem with config file " + file
					+ ". Assuming default configuration. Problem:" + e.getMessage());
		}
		return ConfigFactory.create(GaMiniOsServerConfig.class, props);
	}

}
