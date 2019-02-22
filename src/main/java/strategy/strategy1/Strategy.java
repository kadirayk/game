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
import util.GamingPrototypeConfig;
import util.RmiVmType;
import util.SerializationUtil;

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
		System.out.format("With %s: %s:%s", args[6], getGaMiniOsServerIp(), gamingPrototypeConfig.getRmiServerPort());

		loadCommonGameProp();

		startVM(rmiVmType);

		// runHillClimb(outputsDir, processDir);

		runNSGAII();

	}

	private static void runNSGAII() {
		GaEvaluationProblem problem = new GaEvaluationProblem();
		NondominatedPopulation result = new Executor().withProblem(problem).withAlgorithm("NSGAII")
				.withProperty("populationSize", 2).withMaxEvaluations(4).run();

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

		SerializationUtil.writeIndividual(outputsDir, bestIndividual);

	}

	private static void startVM(String rmiVmType) {
		if (RmiVmType.REMOTE.value().equals(rmiVmType)) {
			return; // if remote vm can not send start command
		}

		Runtime rt = Runtime.getRuntime();
		try {
			String command = null;
			if (RmiVmType.LOCAL_LINUX.value().equals(rmiVmType)) {
				command = "VBoxManage startvm win10_32";
			} else if (RmiVmType.LOCAL_WINDOWS.value().equals(rmiVmType)) {
				command = "\"C:\\Program Files\\Oracle\\VirtualBox\\VBoxManage.exe\" startvm win10_32";
			} else {
				System.err.println("invalid VM Type");
				return;
			}
			rt.exec(command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void runHillClimb(String outputsDir, String processDir) {
		Individual bestIndividual = new Individual(Configurator.BIT_LENGTH);

		double bestScore = calculateInitialScore(outputsDir, processDir, bestIndividual);

		System.out.println("initial score:" + bestScore);
		int num_iterations = 0;
		int num_no_consequent_improvements = 0;

		while (num_iterations < gamingPrototypeConfig.getMaxSizeOfPopulation() - 1
				&& num_no_consequent_improvements < gamingPrototypeConfig.getMaxNumNoConsequentImprovements()) {
			Individual tmpIndividual = bestIndividual.changeOneBit();
			System.out.println("iteration: " + num_iterations);

			GaEvaluation evaluation = configureAndEvaluate(processDir, tmpIndividual);
			Double tmpScore = evaluation.getResponseDelay();

			if (tmpScore < bestScore) {
				bestScore = tmpScore;
				bestIndividual = tmpIndividual;
				num_no_consequent_improvements = 0;
			} else {
				num_no_consequent_improvements++;
			}

			System.out.println("score: " + tmpScore);
			num_iterations++;
		}

		if (num_iterations == gamingPrototypeConfig.getMaxSizeOfPopulation() - 1) {
			System.out.println("reached number of max individual count.");
		} else if (num_no_consequent_improvements == gamingPrototypeConfig.getMaxNumNoConsequentImprovements()) {
			System.out.println("no more improvements.");
		}

		// FileUtil.writeToFile(outputsDir + "/score", String.valueOf(bestScore));
		SerializationUtil.writeIndividual(outputsDir, bestIndividual);

	}

	private static Double calculateInitialScore(String outputsDir, String processDir, Individual individual) {
		System.out.println("Calculating score for initial:" + individual.toString());

		GaEvaluation evaluation = configureAndEvaluate(processDir, individual);

		Double score = evaluation.getResponseDelay();

		return score;
	}

	public static GaEvaluation configureAndEvaluate(String processDir, Map<String, String> configuration) {
		GaMiniOsServerRmiClient gaServerRmiClient = new GaMiniOsServerRmiClient(getGaMiniOsServerIp(),
				gamingPrototypeConfig.getRmiServerPort());

		GaMiniOsClientRmiClient gaClientRmiClient = new GaMiniOsClientRmiClient(getGaMiniOsClientIp(),
				gamingPrototypeConfig.getRmiServerPort());

		InterviewFillout interviewFillout = SerializationUtil.readAsJSON(processDir + "/interview/");
		Question gameSelectionQuestion = new Question();
		gameSelectionQuestion.setId("game_selection");
		String gameSelection = interviewFillout.getAnswer(gameSelectionQuestion);
//		System.out.println("game selection: " + gameSelection);
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
				gamingPrototypeConfig.getRmiServerPort());

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
		System.out.println("Result:");
		System.out.println("delay:" + evaluation.getResponseDelay());
		System.out.println("fps:" + evaluation.getFps());
		System.out.println("encodingError:" + evaluation.getEncodingError());

		return evaluation;
	}

	public static GaEvaluation configureAndEvaluate(String processDir, Individual individual) {
		GaMiniOsServerRmiClient gaServerRmiClient = new GaMiniOsServerRmiClient(getGaMiniOsServerIp(),
				gamingPrototypeConfig.getRmiServerPort());

		GaMiniOsClientRmiClient gaClientRmiClient = new GaMiniOsClientRmiClient(getGaMiniOsClientIp(),
				gamingPrototypeConfig.getRmiServerPort());

		InterviewFillout interviewFillout = SerializationUtil.readAsJSON(processDir + "/interview/");
		Question gameSelectionQuestion = new Question();
		gameSelectionQuestion.setId("game_selection");
		String gameSelection = interviewFillout.getAnswer(gameSelectionQuestion);
		System.out.println("game selection: " + gameSelection);
		String gameConf = commonGameProp.getProperty(gameSelection + ".conf");
		String gameServer = commonGameProp.getProperty(gameSelection + ".server");
		String gameWindow = commonGameProp.getProperty(gameSelection + ".window");
		String gameExe = commonGameProp.getProperty(gameSelection + ".exe");

		// stop in any case
		gaServerRmiClient.stopServerByWindowTitle(gameWindow);

		System.out.println("individual: " + individual.toString());

		ConfigurationData config = new ConfigurationData(Configurator.createConfiguration(individual));

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
		individual.setConfig(config);

		gaServerRmiClient.configureAndStartup(config);

		GaMiniOsClientEvaluation clientEvaluation = gaClientRmiClient.startGaClientAndEvaluate(getGaMiniOsServerIp(),
				gamingPrototypeConfig.getRmiServerPort());

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
