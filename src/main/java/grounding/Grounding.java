package grounding;

import java.io.File;

import rmi.client.GaRmiClient;
import strategy.strategy1.Individual;
import util.GamingPrototypeConfig;
import util.SerializationUtil;

public class Grounding {

	private static final GamingPrototypeConfig gamingPrototypeConfig = GamingPrototypeConfig
			.get("./strategies/strategy1/conf/gaming-prototype.properties");

	public static void main(String[] args) {
		String processDir = args[0];
		String strategyDir = args[1];
		String outputsFinalDir = args[2];
		String ip = gamingPrototypeConfig.getRmiServerIp();
		int port = gamingPrototypeConfig.getRmiServerPort();

		System.out.println("Running grounding.jar");
		System.out.println("processDir: " + processDir + "\nstrDir: " + strategyDir + "\nfinal: " + outputsFinalDir);

		int index = outputsFinalDir.lastIndexOf(File.separator);

		outputsFinalDir = outputsFinalDir.substring(0, index);

		outputsFinalDir += "/strategy1";

		Individual winningIndividual = SerializationUtil.readIndividual(outputsFinalDir);

		configureAndStartup(winningIndividual, ip, port);
	}

	public static void configureAndStartup(Individual individual, String ip, int port) {
		GaRmiClient rmiClient = new GaRmiClient(ip, port);

		System.out.println("individual: " + individual.toString());

		rmiClient.configureAndStartup(individual.getConfig());
	}

}
