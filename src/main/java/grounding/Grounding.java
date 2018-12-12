package grounding;

import rmi.client.GaRmiClient;
import strategy.strategy1.Individual;
import util.SerializationUtil;

public class Grounding {

	public static void main(String[] args) {
		String processDir = args[0];
		String strategyDir = args[1];
		String outputsFinalDir = args[2];
		String ip = args[3];
		int port = Integer.valueOf(args[4]);

		System.out.println("Running grounding.jar");
				
		int index = outputsFinalDir.lastIndexOf("/");
		
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
