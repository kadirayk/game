package strategy.strategy1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import model.InterviewFillout;
import model.Question;
import rmi.ConfigurationData;
import rmi.client.GaRmiClient;
import util.FileUtil;
import util.SerializationUtil;

public class Strategy {

	public static Properties commonGameProp;
	private static final int MAX_NUM_NO_CONSEQUENT_IMPROVEMENTS = 3;
	
	private static String ip;
	private static int  port;

	public static void main(String[] args) {
		if (!isValidArgs(args)) {
			return;
		}
		String processDir = args[0];
		String inputsDir = args[1];
		String outputsDir = args[2];
		String timeout = args[3];
		ip = args[4];
		port = Integer.valueOf(args[5]);
		boolean isRemote = args[6].equals("remote") ? true : false;
		

		System.out.format("Running Gaming Strategy with processDir: %s\ninputsDir: %s\noutputsDir:%s\ntimeout:%s\n",
				processDir, inputsDir, outputsDir, timeout);
		System.out.format("With %s: %s:%s", args[6], ip, port);
		
		
		loadCommonGameProp();
		
		if(!isRemote) {
			startVM();
		}

		runHillClimb(outputsDir, processDir);

	}

	private static void startVM() {
		Runtime rt = Runtime.getRuntime();
		try {
			// TODO: check for linux
			// rt.exec("VBoxManage startvm \"win10-32\"");
			rt.exec("\"C:\\Program Files\\Oracle\\VirtualBox\\VBoxManage.exe\" startvm win10_32");
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

		while (num_iterations < Configurator.MAX_INDIVIDUAL_COUNT - 1
				&& num_no_consequent_improvements < MAX_NUM_NO_CONSEQUENT_IMPROVEMENTS) {
			Individual tmpIndividual = bestIndividual.changeOneBit();
			System.out.println("iteration: " + num_iterations);

			Double tmpScore = configureAndEvaluate(processDir, tmpIndividual);

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

		if (num_iterations == Configurator.MAX_INDIVIDUAL_COUNT - 1) {
			System.out.println("reached number of max individual count.");
		} else if (num_no_consequent_improvements == MAX_NUM_NO_CONSEQUENT_IMPROVEMENTS) {
			System.out.println("no more improvements.");
		}

		FileUtil.writeToFile(outputsDir + "/score", String.valueOf(bestScore));
		SerializationUtil.writeIndividual(outputsDir+"/final", bestIndividual);


	}

	private static Double calculateInitialScore(String outputsDir, String processDir, Individual individual) {
		System.out.println("Calculating score for initial:" + individual.toString());

		Double score = configureAndEvaluate(processDir, individual);

		return score;
	}

	public static Double configureAndEvaluate(String processDir, Individual individual) {
		GaRmiClient rmiClient = new GaRmiClient("131.234.250.150", 1099);
		
		System.out.println("individual: " + individual.toString());
		
		ConfigurationData config = new ConfigurationData(Configurator.createConfiguration(individual));

		InterviewFillout interviewFillout = SerializationUtil.readAsJSON(processDir + "/interview/");
		Question q = new Question();
		q.setId("game_selection");
		String gameSelection = interviewFillout.getAnswer(q);
		String gameConf = commonGameProp.getProperty(gameSelection + ".conf");
		String gameServer = commonGameProp.getProperty(gameSelection + ".server");
		String gameWindow = commonGameProp.getProperty(gameSelection + ".window");
		String gameExe = commonGameProp.getProperty(gameSelection + ".exe");

		config.setGameSelection(gameSelection);
		config.setGameConf(gameConf);
		config.setGameServer(gameServer);
		config.setGameWindow(gameWindow);
		config.setGameExe(gameExe);
		individual.setConfig(config);

		Double score = rmiClient.configureAndEvaluate(config);
		return score;
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
}
