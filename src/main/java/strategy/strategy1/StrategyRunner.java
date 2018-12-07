package strategy.strategy1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import model.Interview;
import util.FileUtil;
import util.SerializationUtil;

public class StrategyRunner {

	private static Properties generalGameProp;

	public static void main(String[] args) {
		System.out.println("Running strategy1 for game");

		String port = getRandomAvailablePort();

		changeServerPort(port);

		changeClientPort(port);
		
		System.out.println("Game will start on port: " + port);

		loadConf();

//		Interview interview = SerializationUtil.readAsJSON("../../interview_data/");
//
//		String gameSelection = interview.getQuestionByPath("step1.q1").getAnswer();
//
//		System.out.println("Game selection: " + gameSelection);
//		createGroundingRoutine(gameSelection);

		System.out.println("Strategy is ready");
	}

	private static void changeClientPort(String port) {
		StringBuilder content = new StringBuilder();
		content.append("@echo off\n");
		content.append("title PROSECO Gaming\n");
		content.append("cd lib\n");
		content.append("ga-client client.abs.conf rtsp://127.0.0.1:").append(port).append("/desktop");

		FileUtil.writeToFile("../../client/run_client.bat", content.toString());
	}

	private static void changeServerPort(String port) {
		Properties commonServerProp = new Properties();
		InputStream input = null;
		String commonServerConfFile = "../../src/config/common/server-common.conf";

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

	private static void createGroundingRoutine(String gameSelection) {
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

		FileUtil.writeToFile("../../groundingroutine.bat", content.toString());
	}

	private static void loadConf() {
		generalGameProp = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream("../../conf/game.conf");

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
