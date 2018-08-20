package strategy.strategy1;


public class Algorithm {

	public static final int POPULATION_SIZE = 8;
	public static final int[] TARGET_CHROMOSOME = { 1, 1, 0, 1, 0, 0, 1, 1, 1, 0 };
	private static final double MUTATION_RATE = 0.25;
	public static final int NUM_OF_ELITE_CHROMOSOMES = 1;
	public static final int TURNAMENT_SELECTION_SIZE = 4;

	public Population evolve(Population population) {
		return mutatePopulation(crossoverPopulation(population));
	}

	private Population crossoverPopulation(Population population) {
		Population crossoverPopulation = new Population(population.getChromosomes().length);
		for (int i = 0; i < NUM_OF_ELITE_CHROMOSOMES; i++) {
			crossoverPopulation.getChromosomes()[i] = population.getChromosomes()[i];
		}
		for (int i = NUM_OF_ELITE_CHROMOSOMES; i < population.getChromosomes().length; i++) {
			Chromosome chromosome1 = selectTournamentPopulation(population).getChromosomes()[0];
			Chromosome chromosome2 = selectTournamentPopulation(population).getChromosomes()[0];
			crossoverPopulation.getChromosomes()[i] = crossoverChromosome(chromosome1, chromosome2);

		}

		return crossoverPopulation;
	}

	private Population mutatePopulation(Population population) {
		Population mutatePopulation = new Population(population.getChromosomes().length);
		for (int i = 0; i < NUM_OF_ELITE_CHROMOSOMES; i++) {
			mutatePopulation.getChromosomes()[i] = population.getChromosomes()[i];
		}
		for (int i = NUM_OF_ELITE_CHROMOSOMES; i < population.getChromosomes().length; i++) {
			mutatePopulation.getChromosomes()[i] = mutateChromosome(population.getChromosomes()[i]);
		}
		return mutatePopulation;
	}

	private Chromosome crossoverChromosome(Chromosome chromosome1, Chromosome chromosome2) {
		Chromosome crossoverChromosome = new Chromosome(chromosome1.getGenes().length);
		for (int i = 0; i < chromosome1.getGenes().length; i++) {
			if (Math.random() < 0.5) {
				crossoverChromosome.getGenes()[i] = chromosome1.getGenes()[i];
			} else {
				crossoverChromosome.getGenes()[i] = chromosome2.getGenes()[i];
			}
		}

		return crossoverChromosome;
	}

	private Chromosome mutateChromosome(Chromosome chromosome) {
		Chromosome mutateChromosome = new Chromosome(chromosome.getGenes().length);

		for (int i = 0; i < chromosome.getGenes().length; i++) {
			if (Math.random() < MUTATION_RATE) {
				if (Math.random() < 0.5) {
					mutateChromosome.getGenes()[i] = 1;
				} else {
					mutateChromosome.getGenes()[i] = 0;
				}
			} else {
				mutateChromosome.getGenes()[i] = chromosome.getGenes()[i];
			}
		}

		return mutateChromosome;

	}

	private Population selectTournamentPopulation(Population population) {
		Population tournamentPopulatiton = new Population(TURNAMENT_SELECTION_SIZE);
		for (int i = 0; i < TURNAMENT_SELECTION_SIZE; i++) {
			tournamentPopulatiton.getChromosomes()[i] = population
					.getChromosomes()[(int) (Math.random() * population.getChromosomes().length)];
		}
		tournamentPopulatiton.sortChromosomeByFitness();
		return tournamentPopulatiton;
	}
}