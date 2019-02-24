package grounding;

import java.io.File;

import model.InterviewFillout;
import model.Question;
import rmi.client.GaMiniOsServerRmiClient;
import strategy.strategy1.Individual;
import util.GamingPrototypeConfig;
import util.SerializationUtil;

public class Grounding {

	private static final GamingPrototypeConfig gamingPrototypeConfig = GamingPrototypeConfig
			.get("./strategies/strategy1/conf/gaming-prototype.properties");

	public static void main(String[] args) {
		String processDir = "../../../../processes/" + args[0];
		String strategyDir = args[1];
		String outputsFinalDir = args[2];
		String ip = getGaMiniOsServerIp(processDir);
		int port = gamingPrototypeConfig.getServerRmiServerPort();

		System.out.println("Running grounding.jar");
		System.out.println("processDir: " + processDir + "\nstrDir: " + strategyDir + "\nfinal: " + outputsFinalDir);

		int index = outputsFinalDir.lastIndexOf(File.separator);

		outputsFinalDir = outputsFinalDir.substring(0, index);

		outputsFinalDir += "/strategy1";

		Individual winningIndividual = SerializationUtil.readIndividual(outputsFinalDir);

		configureAndStartup(winningIndividual, ip, port);
	}

	public static void configureAndStartup(Individual individual, String ip, int port) {
		GaMiniOsServerRmiClient rmiClient = new GaMiniOsServerRmiClient(ip, port);

		System.out.println("individual: " + individual.toString());

		rmiClient.configureAndStartup(individual.getConfig());
	}

	public static String getGaMiniOsServerIp(String processDir) {
		InterviewFillout interviewFillout = SerializationUtil.readAsJSON(processDir + "/interview/");
		Question q = new Question();
		q.setId("server_entry");
		String serverIp = interviewFillout.getAnswer(q);
		return serverIp;
	}

}
