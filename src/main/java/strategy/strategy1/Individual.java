package strategy.strategy1;

import java.util.Arrays;
import java.util.Random;

import rmi.ConfigurationData;

public class Individual {
	private int[] bits;
	ConfigurationData config;
	private int[] seeds;
	
	private static int seedIndex;

	public Individual() {
		// TODO Auto-generated constructor stub
	}

	public void setBits(int[] bits) {
		this.bits = bits;
	}

	public ConfigurationData getConfig() {
		return config;
	}

	public void setConfig(ConfigurationData config) {
		this.config = config;
	}

	private Individual(int[] bits) {
		this.bits = bits;
	}

	public Individual(int length) {
		seedIndex=0;
		seeds = new int[length];
		for (int i = 0; i < seeds.length; i++) {
			seeds[i] = i;
		}

		bits = new int[length];
		for (int i = 0; i < bits.length; i++) {
			bits[i] = new Random(seeds[i]).nextInt(length);
		}
	}

	public int[] getBits() {
		return bits;
	}

	@Override
	public String toString() {
		return Arrays.toString(this.bits);
	}

	public static void main(String[] args) {
		int rbits = new Random(123).nextInt(16);
		int rbit = new Random(123).nextInt(1);
		System.out.println("s:" + rbits + "t:" + rbit);

	}

	public Individual changeOneBit() {
		int length = bits.length;
		int randomBitIndex = new Random(123).nextInt(length);
		seedIndex = (seedIndex+1) % seeds.length;
		int currentVal = bits[randomBitIndex];
		if (currentVal == 0) {
			currentVal = 1;
		} else {
			currentVal = 0;
		}
		int[] newBits = Arrays.copyOf(bits, bits.length);
		newBits[randomBitIndex] = currentVal;
		// return new object for immutability
		return new Individual(newBits);
	}

}
