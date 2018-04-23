package strategy.strategy1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
		while (!"10".equals(score) || i < candidates.size()) {
			String time = String.valueOf(System.currentTimeMillis());
			String filepath = "../../benchmarks/population/individual_" + time;
			Configurator.createConfigurationFile(filepath, candidates.get(i));

			while (score == null) {
				System.out.println("Checking score for:" + filepath);
				score = Configurator.getConfigValue(filepath, "score");
				System.out.println("score=" + score);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			i++;
		}

	}

	private static String getScore(String filePath) {
		String score = null;

		return score;
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
