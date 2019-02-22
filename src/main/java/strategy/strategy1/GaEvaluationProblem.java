package strategy.strategy1;

import java.util.HashMap;
import java.util.Map;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

import rmi.GaEvaluation;

public class GaEvaluationProblem extends AbstractProblem {

	public static Map<String, String> videoSpecificBitrate = new HashMap<>();
	public static Map<String, String> videoFps = new HashMap<>();
	public static Map<String, String> videoSpecificMethod = new HashMap<>();
	public static Map<String, String> videoEncoder = new HashMap<>();
	public static Map<String, String> videoSpecificRange = new HashMap<>();
	public static Map<String, String> videoRenderer = new HashMap<>();
	public static Map<String, String> videoSpecificIntraRefresh = new HashMap<>();
	public static Map<String, String> videoDecoder = new HashMap<>();
	public static Map<String, String> videoSpecificRefs = new HashMap<>();
	public static Map<String, String> scalingFactor = new HashMap<>();

	static {
		videoSpecificBitrate.put("000", "800000");
		videoSpecificBitrate.put("001", "1000000");
		videoSpecificBitrate.put("010", "1500000");
		videoSpecificBitrate.put("011", "2000000");
		videoSpecificBitrate.put("100", "3000000");
		videoSpecificBitrate.put("101", "4000000");
		videoSpecificBitrate.put("110", "5000000");
		videoSpecificBitrate.put("111", "6000000");

		videoFps.put("000", "8");
		videoFps.put("001", "16");
		videoFps.put("010", "24");
		videoFps.put("011", "32");
		videoFps.put("100", "40");
		videoFps.put("101", "48");
		videoFps.put("110", "56");
		videoFps.put("111", "60");

		videoSpecificMethod.put("0000", "dia");
		videoSpecificMethod.put("0001", "zero");
		videoSpecificMethod.put("0010", "full");
		videoSpecificMethod.put("0011", "epzs");
		videoSpecificMethod.put("0100", "esa");
		videoSpecificMethod.put("0101", "log");
		videoSpecificMethod.put("0110", "phods");
		videoSpecificMethod.put("0111", "X1");
		videoSpecificMethod.put("1000", "hex");
		videoSpecificMethod.put("1001", "umh");
		videoSpecificMethod.put("1010", "iter");
		videoSpecificMethod.put("1011", "dia");
		videoSpecificMethod.put("1100", "dia");
		videoSpecificMethod.put("1101", "dia");
		videoSpecificMethod.put("1110", "dia");
		videoSpecificMethod.put("1111", "dia");

		// videoEncoder
		// videoDecoder

		videoSpecificRange.put("00", "4");
		videoSpecificRange.put("01", "8");
		videoSpecificRange.put("10", "16");
		videoSpecificRange.put("11", "24");

		videoRenderer.put("0", "hardware");
		videoRenderer.put("1", "software");

		videoSpecificIntraRefresh.put("0", "0");
		videoSpecificIntraRefresh.put("1", "1");

		videoSpecificRefs.put("0", "0");
		videoSpecificRefs.put("1", "1");

		scalingFactor.put("00", "100");
		scalingFactor.put("01", "125");
		scalingFactor.put("10", "150");
		scalingFactor.put("11", "200");

	}

	public GaEvaluationProblem() {
		super(7, 3);
		// TODO Auto-generated constructor stub
	}

	public static Map<String, String> createConfiguration(Solution solution) {
		String videoSpecificBitrate = getActualVideoSpecificBitrate(EncodingUtils.getBinary(solution.getVariable(0)));
		String videoSpecificMethod = getActualVideoSpecificMethod(EncodingUtils.getBinary(solution.getVariable(1)));
		String videoSpecificRange = getActualVideoSpecificRange(EncodingUtils.getBinary(solution.getVariable(2)));
		String videoRenderer = getActualVideoRenderer(EncodingUtils.getBinary(solution.getVariable(3)));
		String videoSpecificIntraRefresh = getActualVideoSpecificIntraRefresh(
				EncodingUtils.getBinary(solution.getVariable(4)));
		String videoSpecificRefs = getActualVideoSpecificRefs(EncodingUtils.getBinary(solution.getVariable(5)));
		String scalingFactor = EncodingUtils.getInt(solution.getVariable(6))+"";
		Map<String, String> configuration = new HashMap<>();
		configuration.put("video-specific[b]", videoSpecificBitrate);
		configuration.put("video-specific[me_method]", videoSpecificMethod);
		configuration.put("video-specific[me_range]", videoSpecificRange);
		configuration.put("video-renderer", videoRenderer);
		configuration.put("video-specific[intra_refresh]", videoSpecificIntraRefresh);
		configuration.put("video-specific[refs]", videoSpecificRefs);
		configuration.put("scaling-factor", scalingFactor);
		return configuration;
	}

	@Override
	public void evaluate(Solution solution) {

		double[] f = new double[numberOfObjectives];

		Map<String, String> configuration = createConfiguration(solution);
		
		GaEvaluation evaluation = Strategy.configureAndEvaluate(Strategy.processDir, configuration);

		f[0] = evaluation.getResponseDelay(); // minimize
		f[1] = -evaluation.getFps(); // maximize
		f[2] = evaluation.getEncodingError(); // minimize

		solution.setObjectives(f);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(getNumberOfVariables(), getNumberOfObjectives());

		solution.setVariable(0, new BinaryVariable(3));// videoSpecificBitrate
		solution.setVariable(1, new BinaryVariable(4));// videoSpecificMethod
		solution.setVariable(2, new BinaryVariable(2)); // videoSpecificRange
		solution.setVariable(3, new BinaryVariable(1)); // videoRenderer
		solution.setVariable(4, new BinaryVariable(1)); // videoSpecificIntraRefresh;
		solution.setVariable(5, new BinaryVariable(1)); // videoSpecificRefs;
		solution.setVariable(6, EncodingUtils.newInt(100, 200)); // scaling factor

		return solution;
	}

	private static int toDigit(boolean b) {
		return b ? 1 : 0;
	}

	private static String toBinaryString(boolean[] bits) {
		String bitSetInt = "";
		for (int i = 0; i < bits.length; i++) {
			bitSetInt += toDigit(bits[i]);
		}
		return bitSetInt;
	}

	private static String getActualVideoSpecificBitrate(boolean[] bits) {
		String val = toBinaryString(bits);
		String actual = videoSpecificBitrate.get(val);
		System.out.println("videoSpecificBitrate: bits: " + val + " actual: " + actual);
		return actual;
	}

	private static String getActualVideoSpecificMethod(boolean[] bits) {
		String val = toBinaryString(bits);
		String actual= videoSpecificMethod.get(val);
		System.out.println("videoSpecificMethod: bits: " + val + " actual: " + actual);
		return actual;
	}

	private static String getActualVideoSpecificRange(boolean[] bits) {
		String val = toBinaryString(bits);
		String actual =  videoSpecificRange.get(val);
		System.out.println("videoSpecificRange: bits: " + val + " actual: " + actual);
		return actual;
	}

	private static String getActualVideoRenderer(boolean[] bits) {
		String val = toBinaryString(bits);
		String actual = videoRenderer.get(val);
		System.out.println("videoRenderer: bits: " + val + " actual: " + actual);
		return actual;
	}

	private static String getActualVideoSpecificIntraRefresh(boolean[] bits) {
		String val = toBinaryString(bits);
		String actual =  videoSpecificIntraRefresh.get(val);
		System.out.println("videoSpecificIntraRefresh: bits: " + val + " actual: " + actual);
		return actual;
	}

	private static String getActualVideoSpecificRefs(boolean[] bits) {
		String val = toBinaryString(bits);
		String actual = videoSpecificRefs.get(val);
		System.out.println("videoSpecificRefs: bits: " + val + " actual: " + actual);
		return actual;
	}

	private static String getActualScalingFactor(boolean[] bits) {
		String val = toBinaryString(bits);
		String actual = scalingFactor.get(val);
		System.out.println("ScalingFactor: bits: " + val + " actual: " + actual);
		return actual;
	}

	public static void main(String[] args) {
		boolean[] bit = { false, true, false };
		getActualVideoSpecificBitrate(bit);
	}
}
