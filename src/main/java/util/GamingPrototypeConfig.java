package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.aeonbits.owner.Accessible;
import org.aeonbits.owner.ConfigFactory;
import org.aeonbits.owner.Mutable;

public interface GamingPrototypeConfig extends Mutable, Accessible {
	public static final String MAX_NUM_NO_CONSEQUENT_IMPROVEMENTS = "strategy.max_num_no_consequent_improvements";
	public static final String MAX_SIZE_OF_POPULATION = "strategy.max_size_of_population";
	public static final String MAX_EVALUATIONS = "strategy.max_evaluations";
	
	public static final String SERVER_VM_IP = "strategy.server_vm_ip";
	public static final String CLIENT_VM_IP = "strategy.client_vm_ip";
	public static final String RMI_SERVER_PORT = "strategy.rmi_server_port";
	public static final String RMI_VM_TYPE = "strategy.rmi_vm_type";

	public static final String GA_SERVER_IP = "ga.server_ip";
	public static final String GA_SERVER_PORT = "ga.server_port";

	public static final String DEPLOYMENT_PORT = "service.deployment_port";

	@Key(DEPLOYMENT_PORT)
	@DefaultValue("18080")
	public Integer getDeploymentPort();

	@Key(GA_SERVER_IP)
	@DefaultValue("192.168.56.101")
	public String getGaServerIp();

	@Key(GA_SERVER_PORT)
	@DefaultValue("8554")
	public String getGaServerPort();

	@Key(MAX_NUM_NO_CONSEQUENT_IMPROVEMENTS)
	@DefaultValue("3")
	public Integer getMaxNumNoConsequentImprovements();

	@Key(MAX_SIZE_OF_POPULATION)
	@DefaultValue("8")
	public Integer getMaxSizeOfPopulation();
	
	@Key(MAX_EVALUATIONS)
	@DefaultValue("4")
	public Integer getMaxEvaluations();

	@Key(SERVER_VM_IP)
	@DefaultValue("192.168.56.101") // default ip of VM
	public String getServerVmIp();
	
	@Key(CLIENT_VM_IP)
	@DefaultValue("192.168.56.101") // default ip of VM
	public String getClientVmIp();
	

	@Key(RMI_SERVER_PORT)
	@DefaultValue("1099")
	public Integer getRmiServerPort();

	/**
	 * possible values: - LOCAL_LINUX - LOCAL_WINDOWS - REMOTE
	 * 
	 * @return
	 */
	@Key(RMI_VM_TYPE)
	@DefaultValue("LOCAL_LINUX")
	public String getRmiVmType();

	public static GamingPrototypeConfig get(String file) {
		return get(new File(file));
	}

	public static GamingPrototypeConfig get(File file) {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			System.err.println("Could not find config file " + file + ". Assuming default configuration");
		} catch (IOException e) {
			System.err.println("Encountered problem with config file " + file
					+ ". Assuming default configuration. Problem:" + e.getMessage());
		}
		return ConfigFactory.create(GamingPrototypeConfig.class, props);
	}

}
