package strategy.strategy1;

public class Individual {

	// video-specific[b] : The desired bitrate for video encoder (unit: bps)
	//000 = 250.000
	//001 = 500.000
	//010 = 1.000.000
	//011 = 2.000.000
	//100 = 3.000.000
	//101 = 4.000.000
	//110 = 5.000.000
	//111 = 6.000.000
	
	//video-specific[me_method] : Motion estimation algorithms, can be zero, full, epzs, esa, dia, log, phods, hex, umh, and iter
	//00 = zero
	//01 = dia
	//10 = epzs
	//11 = full
	
	//filter-source-pixelformat = bgra or rgba
	//0 = rgba
	//1 = bgra
	
	static int defaultGeneLength = 6;
	private byte[] genes = new byte[defaultGeneLength];
	// Cache
	private int fitness = 0;

	// Create a random individual
	public void generateIndividual() {
		for (int i = 0; i < size(); i++) {
			byte gene = (byte) Math.round(Math.random());
			genes[i] = gene;
		}
	}

	/* Getters and setters */
	// Use this if you want to create individuals with different gene lengths
	public static void setDefaultGeneLength(int length) {
		defaultGeneLength = length;
	}

	public byte getGene(int index) {
		return genes[index];
	}

	public void setGene(int index, byte value) {
		genes[index] = value;
		fitness = 0;
	}

	/* Public methods */
	public int size() {
		return genes.length;
	}

	public int getFitness() {
		if (fitness == 0) {
			fitness = FitnessCalc.getFitness(this);
		}
		return fitness;
	}

	@Override
	public String toString() {
		String geneString = "";
		for (int i = 0; i < size(); i++) {
			geneString += getGene(i);
		}
		return geneString;
	}
}