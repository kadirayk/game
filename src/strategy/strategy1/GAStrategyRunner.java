package strategy.strategy1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import benchmark.Benchmark;
import util.FileUtil;

public class GAStrategyRunner {

	private static Properties generalGameProp;

	public static void main(String[] args) {

		loadConf();

		// List<byte[]> candidates = initiateCandidates();

		Population population = new Population(Configurator.POPULATION_SIZE).initializePopulation();
		Algorithm algorithm = new Algorithm();

		String score = null;
		int i = 0;
		while (i < 8) {
			String time = String.valueOf(System.currentTimeMillis());
			String filepath = "../../benchmarks/population/individual_" + time;
			Configurator.createConfigurationFile(filepath, population);

			System.out.println("Calculating score for:" + filepath);

			String path = new File(".").getAbsolutePath();
			int startIndex = path.lastIndexOf("game-") + "game-".length();
			int endIndex = startIndex + 10;
			String id = path.substring(startIndex, endIndex);

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
			score = null;
			population = algorithm.evolve(population);
			System.out.println("after evolution: " + population.getChromosomes()[0].getGenes().toString());
			i++;

		}

		// String score = null;
		// Collections.shuffle(candidates);
		// int i = 0;
		// while (i < candidates.size()) {
		// String time = String.valueOf(System.currentTimeMillis());
		// String filepath = "../../benchmarks/population/individual_" + time;
		// Configurator.createConfigurationFile(filepath, candidates.get(i),
		// String.valueOf(candidates.size()));
		//
		// System.out.println("Checking score for:" + filepath);
		//
		// String path = new File(".").getAbsolutePath();
		// int startIndex = path.lastIndexOf("game-") + "game-".length();
		// int endIndex = startIndex + 10;
		// String id = path.substring(startIndex, endIndex);
		//
		// while (score == null) {
		// score = Configurator.getConfigValue(filepath, "score");
		// try {
		// Thread.sleep(3000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		//
		// System.out.println("score: " + score);
		// score = null;
		//
		// i++;
		// }

		String winningId = findWinningIndividual();
		moveWinningIndividualToGroundingFolder(winningId);
		createGroundingRoutineForWinningStrategy(winningId);
	}

	private static List<byte[]> initiateCandidates() {
		List<byte[]> candidates = new ArrayList<>();

		return candidates;
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
