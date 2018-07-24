package strategy.strategy1;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import benchmark.Benchmark;
import util.FileUtil;

public class GAStrategyRunner {

	public static void main(String[] args) {

		List<byte[]> candidates = new ArrayList<>();

		for (int i = 0; i < 8; i++) {
			String binary = Integer.toBinaryString(i);
			binary = String.format("%03d", Integer.parseInt(binary));
			candidates.add(ByteUtil.toByteArray(binary));
		}

		String score = null;
		Collections.shuffle(candidates);
		int i = 0;
		while (i < candidates.size()) {
			String time = String.valueOf(System.currentTimeMillis());
			String filepath = "../../benchmarks/population/individual_" + time;
			Configurator.createConfigurationFile(filepath, candidates.get(i), String.valueOf(candidates.size()));

			System.out.println("Checking score for:" + filepath);

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

			i++;
		}

		findWinningIndividual();

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

	// // Set a candidate solution
	// FitnessCalc.setSolution("101010");
	//
	// // Create an initial population
	// Population myPop = new Population(6, true);
	//
	// // Evolve our population until we reach an optimum solution
	// int generationCount = 0;
	// while (myPop.getFittest().getFitness() < FitnessCalc.getMaxFitness()) {
	// generationCount++;
	// System.out.println("Generation: " + generationCount + " Fittest: " +
	// myPop.getFittest().getFitness());
	// myPop = Algorithm.evolvePopulation(myPop);
	// }
	// System.out.println("Solution found!");
	// System.out.println("Generation: " + generationCount);
	// System.out.println("Genes:");
	// System.out.println(myPop.getFittest());
	// System.out.println(myPop.getFittest().getFitness());
	//
	// }

}
