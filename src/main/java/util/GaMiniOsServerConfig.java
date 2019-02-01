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

	public static final String RMI_SERVER_IP = "rmi.server_ip";
	public static final String RMI_SERVER_PORT = "rmi.server_port";
	public static final String RESOLUTION_SCALE_FACTOR = "resolution_scale_factor";

	@Key(RMI_SERVER_IP)
	@DefaultValue("localhost")
	public String getRmiServerIp();

	@Key(RMI_SERVER_PORT)
	@DefaultValue("1099")
	public Integer getRmiServerPort();
	
	@Key(RESOLUTION_SCALE_FACTOR)
	@DefaultValue("0.9")
	public Double getResolutionScaleFactor();
	

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
