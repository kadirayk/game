package strategy.strategy1;

import java.util.Arrays;

public class Chromosome {
	private boolean isFitnessChanged = true;
	private int fitness = 0;
	private int[] genes;

	public Chromosome(int length) {
		genes = new int[length];
	}

	public Chromosome initializeChromosome() {
		for (int i = 0; i < genes.length; i++) {
			if (Math.random() >= 0.5) {
				genes[i] = 1;
			} else {
				genes[i] = 0;
			}
		}
		return this;
	}

	public int[] getGenes() {
		isFitnessChanged = true;
		return genes;
	}

	public int getFitness() {
		if (isFitnessChanged) {
			fitness = recalculateFitness();
			isFitnessChanged = false;
		}
		return fitness;
	}

	public int recalculateFitness() {
		int chromosomeFitness = 0;
		for (int i = 0; i < genes.length; i++) {

		}
		return chromosomeFitness;
	}

	public String toString() {
		return Arrays.toString(this.genes);
	}
}