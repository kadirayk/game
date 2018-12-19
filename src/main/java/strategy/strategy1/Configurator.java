package strategy.strategy1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import model.Interview;
import rmi.ConfigurationData;
import util.SerializationUtil;

public class Configurator {

	public static Map<String, String> videoSpecificBitrate = new HashMap<>();
	public static Map<String, String> videoFps = new HashMap<>();
	public static Map<String, String> videoSpecificMethod = new HashMap<>();
	public static Map<String, String> videoEncoder = new HashMap<>();
	public static Map<String, String> videoSpecificRange = new HashMap<>();
	public static Map<String, String> videoRenderer = new HashMap<>();
	public static Map<String, String> videoSpecificIntraRefresh = new HashMap<>();
	public static Map<String, String> videoDecoder = new HashMap<>();
	public static Map<String, String> videoSpecificRefs = new HashMap<>();

	public static final int BIT_LENGTH = 15;

//	public static final int MAX_INDIVIDUAL_COUNT = 8;

	static {
		videoSpecificBitrate.put("000", "250000");
		videoSpecificBitrate.put("001", "500000");
		videoSpecificBitrate.put("010", "1000000");
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

	}

	public static String getVideoSpecificBitrate(String binaryString) {
		return videoSpecificBitrate.get(binaryString);
	}

	public static int getChromosomeSize() {
		int size = videoSpecificBitrate.size() + videoFps.size() + videoSpecificMethod.size()
				+ videoSpecificRange.size() + videoRenderer.size() + videoSpecificIntraRefresh.size()
				+ videoSpecificRefs.size();
		return size;
	}

	public static void informNoMoreImprovements(String filePath) {
		Properties props = new Properties();

		OutputStream output = null;

		try {
			File directory = new File(".");
			filePath = directory.getCanonicalPath() + File.separator + filePath;
			File confFile = new File(filePath);
			confFile.getParentFile().mkdirs();
			confFile.createNewFile();
			output = new FileOutputStream(confFile);

			props.setProperty("no_more_improvements", "true");

			props.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public static Map<String, String> createConfiguration(Individual individual) {
		String bitrateGenes = ConvertArrayToString(Arrays.copyOfRange(individual.getBits(), 0, 3));
		String fpsGenes = ConvertArrayToString(Arrays.copyOfRange(individual.getBits(), 3, 6));
		String methodGenes = ConvertArrayToString(Arrays.copyOfRange(individual.getBits(), 6, 10));
		String rangeGenes = ConvertArrayToString(Arrays.copyOfRange(individual.getBits(), 10, 12));
		String rendererGenes = ConvertArrayToString(Arrays.copyOfRange(individual.getBits(), 12, 13));
		String intraRefreshGenes = ConvertArrayToString(Arrays.copyOfRange(individual.getBits(), 13, 14));
		String refsGenes = ConvertArrayToString(Arrays.copyOfRange(individual.getBits(), 14, 15));

		String bitrate = videoSpecificBitrate.get(bitrateGenes);
		String fps = videoFps.get(fpsGenes);
		String method = videoSpecificMethod.get(methodGenes);
		String range = videoSpecificRange.get(rangeGenes);
		String renderer = videoRenderer.get(rendererGenes);
		String intraRefresh = videoSpecificIntraRefresh.get(intraRefreshGenes);
		String refs = videoSpecificRefs.get(refsGenes);
		

		Map<String, String> configuration = new HashMap<>();

		configuration.put("video-specific[b]", bitrate);
		configuration.put("video-fps", fps);
		configuration.put("video-specific[me_method]", method);
		configuration.put("video-specific[me_range]", range);
		configuration.put("video-renderer", renderer);
		configuration.put("video-specific[intra_refresh]", intraRefresh);
		configuration.put("video-specific[refs]", refs);
		

		return configuration;

	}

	@Deprecated
	public static void createConfigurationFile(String filePath, Individual individual) {
		String bitrateGenes = ConvertArrayToString(Arrays.copyOfRange(individual.getBits(), 0, 3));
		String fpsGenes = ConvertArrayToString(Arrays.copyOfRange(individual.getBits(), 3, 6));
		String methodGenes = ConvertArrayToString(Arrays.copyOfRange(individual.getBits(), 6, 10));
		String rangeGenes = ConvertArrayToString(Arrays.copyOfRange(individual.getBits(), 10, 12));
		String rendererGenes = ConvertArrayToString(Arrays.copyOfRange(individual.getBits(), 12, 13));
		String intraRefreshGenes = ConvertArrayToString(Arrays.copyOfRange(individual.getBits(), 13, 14));
		String refsGenes = ConvertArrayToString(Arrays.copyOfRange(individual.getBits(), 14, 15));

		String bitrate = videoSpecificBitrate.get(bitrateGenes);
		String fps = videoFps.get(fpsGenes);
		String method = videoSpecificMethod.get(methodGenes);
		String range = videoSpecificRange.get(rangeGenes);
		String renderer = videoRenderer.get(rendererGenes);
		String intraRefresh = videoSpecificIntraRefresh.get(intraRefreshGenes);
		String refs = videoSpecificRefs.get(refsGenes);

		Properties props = new Properties();

		OutputStream output = null;

		try {
			File directory = new File(".");
			filePath = directory.getCanonicalPath() + File.separator + filePath;
			File confFile = new File(filePath);
			confFile.getParentFile().mkdirs();
			confFile.createNewFile();
			output = new FileOutputStream(confFile);

			props.setProperty("video-specific[b]", bitrate);
			props.setProperty("video-fps", fps);
			props.setProperty("video-specific[me_method]", method);
			props.setProperty("video-specific[me_range]", range);
			props.setProperty("video-renderer", renderer);
			props.setProperty("video-specific[intra_refresh]", intraRefresh);
			props.setProperty("video-specific[refs]", refs);
//			props.setProperty("populationSize", String.valueOf(MAX_INDIVIDUAL_COUNT));

			props.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	private static String ConvertArrayToString(int[] array) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			str.append(String.valueOf(array[i]));
		}
		return str.toString();
	}

	public static void setConfigValue(String filePath, String key, String value) {
		Properties props = new Properties();
		OutputStream output = null;
		InputStream input = null;

		try {

			File directory = new File(".");
			filePath = directory.getCanonicalPath() + File.separator + filePath;

			input = new FileInputStream(filePath);

			props.load(input);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		try {
			output = new FileOutputStream(filePath);

			props.setProperty(key, value);

			props.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public static String getConfigValue(String filePath, String key) {
		Properties props = new Properties();
		InputStream input = null;

		try {

			File directory = new File(".");
			filePath = directory.getCanonicalPath() + File.separator + filePath;

			input = new FileInputStream(filePath);

			props.load(input);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return props.getProperty(key);
	}

}
