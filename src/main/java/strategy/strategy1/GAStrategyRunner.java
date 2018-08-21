package strategy.strategy1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import benchmark.Benchmark;
import util.FileUtil;

public class GAStrategyRunner {

	private static Properties generalGameProp;

	private static final int MAX_NUM_NO_CONSEQUENT_IMPROVEMENTS = 3;

	public static void main(String[] args) {

		loadConf();

		Individual individual = new Individual(Configurator.BIT_LENGTH);

		String score = null;
		double min_score = Double.MAX_VALUE;
		int num_iterations = 0;
		int num_no_consequent_improvements = 0;
		String filepath = null;
		while (num_iterations < Configurator.MAX_INDIVIDUAL_COUNT
				&& num_no_consequent_improvements < MAX_NUM_NO_CONSEQUENT_IMPROVEMENTS) {
			String time = String.valueOf(System.currentTimeMillis());
			filepath = "../../benchmarks/population/individual_" + time;
			Configurator.createConfigurationFile(filepath, individual);

			System.out.println("Calculating score for:" + filepath);

			while (score == null) {
				score = Configurator.getConfigValue(filepath, "score");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			double tmp_score = Double.parseDouble(score);
			if (tmp_score < min_score) {
				min_score = tmp_score;
				num_no_consequent_improvements = 0;
			} else {
				num_no_consequent_improvements++;
			}

			System.out.println("score: " + score);
			score = null;
			// new individual
			individual = individual.changeOneBit();
			System.out.println("new individual: " + individual.toString());
			num_iterations++;

		}

		if (num_iterations == Configurator.MAX_INDIVIDUAL_COUNT) {
			System.out.println("reached number of max individual count.");
		} else if (num_no_consequent_improvements == MAX_NUM_NO_CONSEQUENT_IMPROVEMENTS) {
			Configurator.setConfigValue(filepath, "no_more_improvements", "true");
			System.out.println("no more improvements.");
		}

		String winningId = findWinningIndividual();
		moveWinningIndividualToGroundingFolder(winningId);
		createGroundingRoutineForWinningStrategy(winningId);
		printWinningConfiguration(winningId);
	}

	private static void printWinningConfiguration(String winningId) {
		File individualFile = new File("../../benchmarks/population/individual_" + winningId);
		String content = FileUtil.readFile(individualFile.getAbsolutePath());
		System.out.println("Winning Configuration: ");
		System.out.println(content);
	}

	private static String findWinningIndividual() {
		Double bestScore = 10.0;
		String winningIndividual = null;
		for (File individual : new File("../../benchmarks/population/").listFiles()) {
			if (individual.getName().contains("individual_")) {
				String scoreString = Benchmark.getConfigValue(individual, "score");
				Double score = Double.parseDouble(scoreString);
				if (score < bestScore) {
					bestScore = score;
					winningIndividual = individual.getName().split("_")[1];
				}
			}

		}
		FileUtil.writeToFile("./output/f.value", String.valueOf(bestScore));
		return winningIndividual;
	}

	private static void moveWinningIndividualToGroundingFolder(String winningId) {
		System.out.println("moving winning individual " + winningId + " to grounding folder");
		File individualFolder = new File("../../benchmarks/testbed_" + winningId);
		File groundingFoler = new File("../../grounding");
		// should be like this but MovePlaceholderFilesToSourceCommand does not
		// copy folders only files: File groundingFoler = new File("./output");
		try {
			FileUtils.copyDirectory(individualFolder, groundingFoler);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createGroundingRoutineForWinningStrategy(String winningId) {
		System.out.println("create groundingroutine.bat for winning strategy");
		String gameSelection = readGameSelection(winningId);
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

		FileUtil.writeToFile("../../groundingroutine.bat", content.toString());
	}

	private static String readGameSelection(String winningId) {

		File file = new File("../../benchmarks/population/individual_" + winningId);

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

	private static void loadConf() {
		generalGameProp = new Properties();
		InputStream input = null;
		try {

			input = new FileInputStream("../../conf/game.conf");

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
