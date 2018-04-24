package strategy.strategy1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Configurator {

	private static Map<String, String> videoSpecificBitrate = new HashMap<>();

	static {
		videoSpecificBitrate.put("000", "250000");
		videoSpecificBitrate.put("001", "500000");
		videoSpecificBitrate.put("010", "1000000");
		videoSpecificBitrate.put("011", "2000000");
		videoSpecificBitrate.put("100", "3000000");
		videoSpecificBitrate.put("101", "4000000");
		videoSpecificBitrate.put("110", "5000000");
		videoSpecificBitrate.put("111", "6000000");
	}

	public static String getVideoSpecificBitrate(String binaryString) {
		return videoSpecificBitrate.get(binaryString);
	}

	public static void createConfigurationFile(String filePath, byte[] config, String populationSize) {
		String bitrate = getVideoSpecificBitrate(ByteUtil.arrayToString(config));
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
			props.setProperty("populationSize", populationSize);

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
		String value = null;
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
