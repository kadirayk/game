package strategy.strategy1;

import java.util.Arrays;
import java.util.Random;

import rmi.ConfigurationData;

public class Individual {
	private int[] bits;
	ConfigurationData config;

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
		bits = new int[length];
		for (int i = 0; i < bits.length; i++) {
			if (Math.random() >= 0.5) {
				bits[i] = 1;
			} else {
				bits[i] = 0;
			}
		}
	}

	public int[] getBits() {
		return bits;
	}

	@Override
	public String toString() {
		return Arrays.toString(this.bits);
	}

	public Individual changeOneBit() {
		int length = bits.length;
		int randomBitIndex = new Random().nextInt(length);
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