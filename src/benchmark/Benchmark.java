package benchmark;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ProcessBuilder.Redirect;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.zeroturnaround.zip.ZipUtil;

import model.FileUtil;
import model.Interview;
import strategy.strategy1.SerializationUtil;

public class Benchmark {

	static List<String> population = new ArrayList<>();

	static List<String> processedPopulation = new ArrayList<>();

	static int populationSize = 1;

	static String port;

	private static Properties generalGameProp;

	public static void main(String[] args) throws InterruptedException {
		File populationDir = new File("population/");

		System.out.println("Benchmarks start");

		while (processedPopulation.size() < populationSize) {
			File currentFile = getNextFile(populationDir);
			if (currentFile != null) {
				String bitrate = getConfigValue(currentFile, "video-specific[b]");
				String popSize = getConfigValue(currentFile, "populationSize");
				if (bitrate == null || popSize == null) {
					Thread.sleep(2000);
					continue;
				}
				if (popSize != null && populationSize == 1) {
					populationSize = Integer.parseInt(popSize);
				}
				System.out.println("running benchmark for " + currentFile.getName());
				setupGroundingRoutine(currentFile.getAbsolutePath(), bitrate);
				processedPopulation.add(currentFile.getName());
			}
			Thread.sleep(2000);
		}

		System.out.println("benchmarks end");

	}

	private static String findClientPath(String fileName) {
		String[] nameArray = fileName.split("_");
		String timeStamp = nameArray[nameArray.length - 1];
		File testBed = new File("testbed_" + timeStamp + "/");

		String clientPath = testBed.getAbsolutePath() + File.separator + "client";

		ZipUtil.pack(new File(clientPath), new File(clientPath + ".zip"));

		return clientPath + ".zip";

	}

	private static File getNextFile(File dir) {
		File nextFile = null;

		File[] fileList = dir.listFiles();
		if (fileList != null) {
			for (File file : fileList) {
				if (file.getName().contains("individual") && !processedPopulation.contains(file.getName())) {
					return file;
				}
			}
		}

		return nextFile;
	}

	private static File copyExecutionFilesToTestBed(String fileName) {
		String[] nameArray = fileName.split("_");
		String timeStamp = nameArray[nameArray.length - 1];
		File testBed = new File("testbed_" + timeStamp + "/");
		testBed.mkdirs();

		File srcDir = new File("../src");
		File srcTargetDir = new File(testBed.getAbsolutePath() + File.separator + "src");

		File clientDir = new File("../client");
		File clientTargetDir = new File(testBed.getAbsolutePath() + File.separator + "client");

		File interviewDir = new File("../interview_data");
		File interviewTargetDir = new File(testBed.getAbsolutePath() + File.separator + "interview_data");

		File confDir = new File("../conf");
		File confTargetDir = new File(testBed.getAbsolutePath() + File.separator + "conf");

		try {
			FileUtils.copyDirectory(srcDir, srcTargetDir);
			FileUtils.copyDirectory(clientDir, clientTargetDir);
			FileUtils.copyDirectory(interviewDir, interviewTargetDir);
			FileUtils.copyDirectory(confDir, confTargetDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return testBed;
	}

	private static void setupGroundingRoutine(String fileName, String bitrate) throws InterruptedException {
		File testBed = copyExecutionFilesToTestBed(fileName);
		if (port == null) {
			port = getRandomAvailablePort();
		}
		changeServerPort(testBed, port);

		changeClientPort(testBed, port);

		loadConf(testBed);

		Interview interview = SerializationUtil.readAsJSON(testBed.getAbsolutePath() + "/interview_data/");

		String gameSelection = interview.getQuestionByPath("step1.q1").getAnswer();

		System.out.println("Game selection: " + gameSelection);
		createGroundingRoutine(testBed, gameSelection);

		File currentFile = new File(fileName);
		System.out.println("136:" + currentFile.getAbsolutePath());
		writeConfValue(currentFile, "client", findClientPath(fileName));
		executeGroundingRoutine(testBed);

		String score = null;
		while (score == null) {
			score = getConfigValue(currentFile, "score");
			Thread.sleep(2000);
		}

		System.out.println("Game will start on port: " + port);
	}

	private static void executeGroundingRoutine(File testBed) {
		final ProcessBuilder pb = new ProcessBuilder(
				testBed.getAbsoluteFile() + File.separator + "groundingroutine.bat").redirectOutput(Redirect.INHERIT)
						.redirectError(Redirect.INHERIT);
		System.out.print("Execute grounding process...");
		Process p;
		try {
			p = pb.start();
			while (p.isAlive()) {
				try {
					Thread.sleep(1000);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (final IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("DONE.");

	}

	public static void writeConfValue(File file, String key, String value) {
		Properties props = new Properties();
		OutputStream output = null;
		InputStream input = null;

		try {

			input = new FileInputStream(file);

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
			output = new FileOutputStream(file);

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

	public static String getConfigValue(File file, String key) {
		Properties props = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream(file);

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

	private static void changeClientPort(File testbed, String port) {
		StringBuilder content = new StringBuilder();
		content.append("@echo off\n");
		content.append("title PROSECO Gaming\n");
		content.append("cd lib\n");
		content.append("ga-client client.abs.conf rtsp://127.0.0.1:").append(port).append("/desktop");

		FileUtil.writeToFile(testbed.getAbsolutePath() + "/client/run_client.bat", content.toString());
	}

	private static void changeServerPort(File testbed, String port) {
		Properties commonServerProp = new Properties();
		InputStream input = null;
		String commonServerConfFile = testbed.getAbsolutePath() + "/src/config/common/server-common.conf";

		try {

			input = new FileInputStream(commonServerConfFile);

			commonServerProp.load(input);

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

		OutputStream output = null;

		try {

			output = new FileOutputStream(commonServerConfFile);

			commonServerProp.setProperty("server-port", port);

			commonServerProp.store(output, null);

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

	private static String getRandomAvailablePort() {
		String port = null;
		try {
			ServerSocket socket = new ServerSocket(0);
			port = String.valueOf(socket.getLocalPort());
			socket.close();
			return port;
		} catch (IOException e) {
			return port;
		}
	}

	private static void createGroundingRoutine(File testbed, String gameSelection) {
		StringBuilder content = new StringBuilder();

		content.append("@echo off\n");
		content.append("title grounding routine\n");
		content.append("cd /d %~dp0\n");
		content.append("cd src\n");

		String gameConf = generalGameProp.getProperty(gameSelection + ".conf");
		String gameServer = generalGameProp.getProperty(gameSelection + ".server");
		String gameExe = generalGameProp.getProperty(gameSelection + ".exe");

		if (StringUtils.isNotEmpty(gameExe)) {
			content.append(gameExe).append("\n").append("TIMEOUT /T 5\n");
		}

		content.append(gameServer).append(" config/").append(gameConf).append("\n");

		FileUtil.writeToFile(testbed + "/groundingroutine.bat", content.toString());
	}

	private static void loadConf(File testbed) {
		generalGameProp = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(testbed.getAbsolutePath() + "/conf/game.conf");

			generalGameProp.load(input);

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

	}

}
