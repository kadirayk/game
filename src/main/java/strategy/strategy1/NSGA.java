package strategy.strategy1;

import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

public class NSGA {

	public static void main(String[] args) {
		NondominatedPopulation result = new Executor().withProblem("UF1").withAlgorithm("NSGAII")
				.withMaxEvaluations(10000).run();

		// display the results
		System.out.format("Objective1  Objective2%n");

		for (Solution solution : result) {
			System.out.format("%.4f      %.4f%n", solution.getObjective(0), solution.getObjective(1));
		}
	}

}
