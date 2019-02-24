package strategy.strategy1;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;

import model.InterviewFillout;
import model.Question;
import rmi.ConfigurationData;
import rmi.GaEvaluation;
import rmi.GaMiniOsClientEvaluation;
import rmi.client.GaMiniOsClientRmiClient;
import rmi.client.GaMiniOsServerRmiClient;
import util.FileUtil;
import util.GamingPrototypeConfig;
import util.SerializationUtil;
import util.VMUtil;

public class Strategy {

	private static final GamingPrototypeConfig gamingPrototypeConfig = GamingPrototypeConfig
			.get("./conf/gaming-prototype.properties");

	public static Properties commonGameProp;

	private static InterviewFillout interviewFillout;

	public static String processDir;
	public static String outputsDir;

	public static void main(String[] args) {
		if (!isValidArgs(args)) {
			return;
		}
		processDir = args[0];
		String inputsDir = args[1];
		outputsDir = args[2];
		String timeout = args[3];

		interviewFillout = SerializationUtil.readAsJSON(processDir + "/interview/");

		String rmiVmType = gamingPrototypeConfig.getRmiVmType();

		System.out.format("Running Gaming Strategy with processDir: %s\ninputsDir: %s\noutputsDir:%s\ntimeout:%s\n",
				processDir, inputsDir, outputsDir, timeout);
		System.out.format("With %s: %s:%s", args[6], getGaMiniOsServerIp(), gamingPrototypeConfig.getServerRmiServerPort());

		loadCommonGameProp();

		VMUtil.startVM(rmiVmType);

		runNSGAII();

	}

	private static void runNSGAII() {
		GaEvaluationProblem problem = new GaEvaluationProblem();
		NondominatedPopulation result = new Executor().withProblem(problem).withAlgorithm("NSGAII")
				.withProperty("populationSize", gamingPrototypeConfig.getMaxSizeOfPopulation())
				.withMaxEvaluations(gamingPrototypeConfig.getMaxEvaluations()).run();

		Map<String, String> configuration = GaEvaluationProblem.createConfiguration(result.get(0));

		InterviewFillout interviewFillout = SerializationUtil.readAsJSON(processDir + "/interview/");
		Question gameSelectionQuestion = new Question();
		gameSelectionQuestion.setId("game_selection");
		String gameSelection = interviewFillout.getAnswer(gameSelectionQuestion);
		String gameConf = commonGameProp.getProperty(gameSelection + ".conf");
		String gameServer = commonGameProp.getProperty(gameSelection + ".server");
		String gameWindow = commonGameProp.getProperty(gameSelection + ".window");
		String gameExe = commonGameProp.getProperty(gameSelection + ".exe");

		ConfigurationData config = new ConfigurationData(configuration);

		Question screenWidthQuestion = new Question();
		screenWidthQuestion.setId("screen_width");
		String screenWidth = interviewFillout.getAnswer(screenWidthQuestion);

		Question screenHeightQuestion = new Question();
		screenHeightQuestion.setId("screen_height");
		String screenHeight = interviewFillout.getAnswer(screenHeightQuestion);

		config.setScreenWidth(screenWidth);
		config.setScreenHeight(screenHeight);
		config.setGameSelection(gameSelection);
		config.setGameConf(gameConf);
		config.setGameServer(gameServer);
		config.setGameWindow(gameWindow);
		config.setGameExe(gameExe);

		Individual bestIndividual = new Individual();
		bestIndividual.setConfig(config);

		FileUtil.writeToFile(outputsDir + "/score", String.valueOf(result.get(0).getObjectives()[0]));
		SerializationUtil.writeIndividual(outputsDir, bestIndividual);

	}

	public static GaEvaluation configureAndEvaluate(String processDir, Map<String, String> configuration) {
		GaMiniOsServerRmiClient gaServerRmiClient = new GaMiniOsServerRmiClient(getGaMiniOsServerIp(),
				gamingPrototypeConfig.getServerRmiServerPort());

		GaMiniOsClientRmiClient gaClientRmiClient = new GaMiniOsClientRmiClient(getGaMiniOsClientIp(),
				gamingPrototypeConfig.getClientRmiServerPort());

		InterviewFillout interviewFillout = SerializationUtil.readAsJSON(processDir + "/interview/");
		Question gameSelectionQuestion = new Question();
		gameSelectionQuestion.setId("game_selection");
		String gameSelection = interviewFillout.getAnswer(gameSelectionQuestion);
		// System.out.println("game selection: " + gameSelection);
		String gameConf = commonGameProp.getProperty(gameSelection + ".conf");
		String gameServer = commonGameProp.getProperty(gameSelection + ".server");
		String gameWindow = commonGameProp.getProperty(gameSelection + ".window");
		String gameExe = commonGameProp.getProperty(gameSelection + ".exe");

		// stop in any case
		gaServerRmiClient.stopServerByWindowTitle(gameWindow);

		ConfigurationData config = new ConfigurationData(configuration);

		Question screenWidthQuestion = new Question();
		screenWidthQuestion.setId("screen_width");
		String screenWidth = interviewFillout.getAnswer(screenWidthQuestion);

		Question screenHeightQuestion = new Question();
		screenHeightQuestion.setId("screen_height");
		String screenHeight = interviewFillout.getAnswer(screenHeightQuestion);

		config.setScreenWidth(screenWidth);
		config.setScreenHeight(screenHeight);
		config.setGameSelection(gameSelection);
		config.setGameConf(gameConf);
		config.setGameServer(gameServer);
		config.setGameWindow(gameWindow);
		config.setGameExe(gameExe);

		gaServerRmiClient.configureAndStartup(config);

		GaMiniOsClientEvaluation clientEvaluation = gaClientRmiClient.startGaClientAndEvaluate(getGaMiniOsServerIp(),
				gamingPrototypeConfig.getClientRmiServerPort());

		try {
			Thread.sleep(500);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}

		Double encodingError = gaServerRmiClient.stopServer();

		GaEvaluation evaluation = new GaEvaluation(clientEvaluation.getFps(), clientEvaluation.getResponseDelay(),
				encodingError);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}

		System.out.println(" Result:");
		System.out.printf("|%-20s | %-20s | %-20s|\n","Response Delay", "FPS", "Encoding Error");
		System.out.println("--------------------------------------------------------------------");
		System.out.printf("|%-20s | %-20s | %-20s|\n",evaluation.getResponseDelay(), evaluation.getFps(), evaluation.getEncodingError());

		return evaluation;
	}

	public static void loadCommonGameProp() {
		commonGameProp = new Properties();
		InputStream input = null;
		try {

			input = new FileInputStream("./conf/game.conf");

			commonGameProp.load(input);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private static boolean isValidArgs(String[] args) {
		if (args.length < 4) {
			System.err.println("Invalid arguments to run strategy:\n" + args.toString());
			return false;
		}
		return true;
	}

	public static String getGaMiniOsServerIp() {
		Question q = new Question();
		q.setId("server_entry");
		String serverIp = interviewFillout.getAnswer(q);
		return serverIp;
	}

	private static String getGaMiniOsClientIp() {
		Question q = new Question();
		q.setId("client_entry");
		String clientIp = interviewFillout.getAnswer(q);
		return clientIp;
	}
}
