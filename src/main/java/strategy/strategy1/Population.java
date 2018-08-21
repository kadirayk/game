package strategy.strategy1;

import java.util.Arrays;


public class Population {
	
	private Chromosome[] chromosomes;
	private int size;

	public int getSize(){
		return size;
	}
	
	public Population(int length) {
		chromosomes = new Chromosome[length];
		size = length;
	}

	public Population initializePopulation() {
		for (int i = 0; i < chromosomes.length; i++) {
			chromosomes[i] = new Chromosome(Configurator.CHROMOSOME_LENGTH).initializeChromosome();
		}
		sortChromosomeByFitness();
		return this;
	}

	public Chromosome[] getChromosomes() {
		return chromosomes;
	}

	public void sortChromosomeByFitness() {
		Arrays.sort(chromosomes, (chromosome1, chromosome2) -> {
			int flag = 0;
			if (chromosome1.getFitness() > chromosome2.getFitness()) {
				flag = -1;
			} else if (chromosome1.getFitness() < chromosome2.getFitness()) {
				flag = 1;
			}
			return flag;
		});

	}

//	Individual[] individuals;
//
//	/*
//	 * Constructors
//	 */
//	// Create a population
//	public Population(int populationSize, boolean initialise) {
//		individuals = new Individual[populationSize];
//		// Initialise population
//		if (initialise) {
//			// Loop and create individuals
//			for (int i = 0; i < size(); i++) {
//				Individual newIndividual = new Individual();
//				newIndividual.generateIndividual();
//				saveIndividual(i, newIndividual);
//			}
//		}
//	}
//
//	/* Getters */
//	public Individual getIndividual(int index) {
//		return individuals[index];
//	}
//
//	public Individual getFittest() {
//		Individual fittest = individuals[0];
//		// Loop through individuals to find fittest
//		for (int i = 0; i < size(); i++) {
//			if (fittest.getFitness() <= getIndividual(i).getFitness()) {
//				fittest = getIndividual(i);
//			}
//		}
//		return fittest;
//	}
//
//	/* Public methods */
//	// Get population size
//	public int size() {
//		return individuals.length;
//	}
//
//	// Save individual
//	public void saveIndividual(int index, Individual indiv) {
//		individuals[index] = indiv;
//	}
}