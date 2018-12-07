package strategy.strategy1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import benchmark.Benchmark;
import util.FileUtil;

public class GAStrategyRunner {

	private static Properties generalGameProp;

	private static final int MAX_NUM_NO_CONSEQUENT_IMPROVEMENTS = 3;

	private static void executeBenchmark(String outputPath) throws Exception {
		System.out.print("Boot up internal benchmark service...");

		final File benchmarkExec = new File(outputPath + "/benchmarks/benchmarkService.bat");

		System.out.println("run " + benchmarkExec.getPath());

		final ProcessBuilder pb = new ProcessBuilder(benchmarkExec.getAbsolutePath()).redirectOutput(Redirect.INHERIT)
				.redirectError(Redirect.INHERIT);

		try {
			Process internalBenchmarkService = pb.start();

			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(internalBenchmarkService.getErrorStream()))) {
				String line;
				boolean serviceUpAndRunning = false;
				while (!serviceUpAndRunning && ((line = br.readLine()) != null)) {
					if (line.contains("Service up and running")) {
						serviceUpAndRunning = true;
					}
				}
			}
			System.out.println("DONE.");

		} catch (final IOException e) {
			System.err.println("ERROR: Could not boot benchmark service.");
			System.exit(1);
		}

	}

	public static void main(String[] args) {

		String executionCopyPath = args[2];

		executionCopyPath += "../../";

		File prototypeDir = new File("../../");
		File executionDir = new File(executionCopyPath);
		try {
			FileUtils.copyDirectory(prototypeDir, executionDir);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		final String executionPath = args[2] + "/../";

		new Thread(() -> {
			try {
				executeBenchmark(executionPath);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();

		loadConf(executionPath);

		Individual bestIndividual = new Individual(Configurator.BIT_LENGTH);

		String score = null;
		double bestScore = calculateInitialScore(executionPath, bestIndividual);
		int num_iterations = 0;
		int num_no_consequent_improvements = 0;
		String filepath = null;
		while (num_iterations < Configurator.MAX_INDIVIDUAL_COUNT - 1
				&& num_no_consequent_improvements < MAX_NUM_NO_CONSEQUENT_IMPROVEMENTS) {
			Individual tmpIndividual = bestIndividual.changeOneBit();

			String time = String.valueOf(System.currentTimeMillis());
			filepath = executionPath + "benchmarks/population/individual_" + time;
			Configurator.createConfigurationFile(filepath, tmpIndividual);

			System.out.println("Calculating score for:" + filepath + "bits: " + tmpIndividual.toString());

			while (score == null) {
				score = Configurator.getConfigValue(filepath, "score");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			double tmpScore = Double.parseDouble(score);
			if (tmpScore < bestScore) {
				bestScore = tmpScore;
				bestIndividual = tmpIndividual;
				num_no_consequent_improvements = 0;
			} else {
				num_no_consequent_improvements++;
			}

			System.out.println("score: " + score);
			score = null;

			num_iterations++;
		}

		if (num_iterations == Configurator.MAX_INDIVIDUAL_COUNT - 1) {
			System.out.println("reached number of max individual count.");
		} else if (num_no_consequent_improvements == MAX_NUM_NO_CONSEQUENT_IMPROVEMENTS) {
			String time = String.valueOf(System.currentTimeMillis());
			filepath = executionPath + "benchmarks/population/individual_" + time;
			Configurator.informNoMoreImprovements(filepath);
			System.out.println("no more improvements.");
		}

		String winningId = findWinningIndividual(executionPath);
		moveWinningIndividualToGroundingFolder(executionPath, winningId);
		createGroundingRoutineForWinningStrategy(executionPath, winningId);
		printWinningConfiguration(executionPath, winningId);
	}

	private static double calculateInitialScore(String executionPath, Individual individual) {
		String time = String.valueOf(System.currentTimeMillis());
		String filepath = executionPath + "benchmarks/population/individual_" + time;
		Configurator.createConfigurationFile(filepath, individual);

		System.out.println("Calculating score for initial:" + filepath + "bits: " + individual.toString());
		String score = null;
		while (score == null) {
			score = Configurator.getConfigValue(filepath, "score");
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println("score: " + score);

		double tmp_score = Double.parseDouble(score);
		return tmp_score;
	}

	private static void printWinningConfiguration(String executionPath, String winningId) {
		File individualFile = new File(executionPath + "/benchmarks/population/individual_" + winningId);
		String content = FileUtil.readFile(individualFile.getAbsolutePath());
		System.out.println("Winning Configuration: ");
		System.out.println(content);
	}

	private static String findWinningIndividual(String executionPath) {
		Double bestScore = 10.0;
		String winningIndividual = null;
		for (File individual : new File(executionPath + "benchmarks/population/").listFiles()) {
			if (individual.getName().contains("individual_")) {
				String scoreString = Benchmark.getConfigValue(individual, "score");
				if (scoreString != null) {
					Double score = Double.parseDouble(scoreString);
					if (score < bestScore) {
						bestScore = score;
						winningIndividual = individual.getName().split("_")[1];
					}
				}
			}

		}
		FileUtil.writeToFile("./output/f.value", String.valueOf(bestScore));
		return winningIndividual;
	}

	private static void moveWinningIndividualToGroundingFolder(String executionPath, String winningId) {
		System.out.println("moving winning individual " + winningId + " to grounding folder");
		File individualFolder = new File(executionPath + "benchmarks/testbed_" + winningId);
		File groundingFoler = new File(executionPath + "grounding");
		// should be like this but MovePlaceholderFilesToSourceCommand does not
		// copy folders only files: File groundingFoler = new File("./output");
		try {
			FileUtils.copyDirectory(individualFolder, groundingFoler);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createGroundingRoutineForWinningStrategy(String executionPath, String winningId) {
		System.out.println("create groundingroutine.bat for winning strategy");
		String gameSelection = readGameSelection(executionPath, winningId);
		StringBuilder content = new StringBuilder();

		content.append("@echo off\n");
		content.append("title grounding routine\n");
		content.append("cd /d %~dp0\n");
		content.append("cd grounding/src\n");

		String gameConf = generalGameProp.getProperty(gameSelection + ".conf");
		String gameServer = generalGameProp.getProperty(gameSelection + ".server");
		String gameExe = generalGameProp.getProperty(gameSelection + ".exe");

		if (StringUtils.isNotEmpty(gameExe)) {
			content.append(gameExe).append("\n").append("TIMEOUT /T 5\n");
		}

		content.append(gameServer).append(" config/").append(gameConf);

		FileUtil.writeToFile(executionPath + "groundingroutine.bat", content.toString());
	}

	private static String readGameSelection(String executionPath, String winningId) {

		File file = new File(executionPath + "benchmarks/population/individual_" + winningId);

		Properties props = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream(file);

			props.load(input);

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

		return props.getProperty("gameSelection");

	}

	private static void loadConf(String executionPath) {
		generalGameProp = new Properties();
		InputStream input = null;
		try {

			input = new FileInputStream(executionPath + "conf/game.conf");

			generalGameProp.load(input);

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

}
