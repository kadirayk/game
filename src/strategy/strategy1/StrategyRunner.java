package strategy.strategy1;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import model.FileUtil;
import model.Interview;

public class StrategyRunner {

	private static Properties prop;

	public static void main(String[] args) {
		System.out.println("Running strategy1 for game");

		loadConf();

		Interview interview = SerializationUtil.readAsJSON("../../interview_data/");

		String gameSelection = interview.getQuestionByPath("step1.q1").getAnswer();

		System.out.println("Game selection: " + gameSelection);
		createGroundingRoutine(gameSelection);

		System.out.println("Strategy is ready");
	}

	private static void createGroundingRoutine(String gameSelection) {
		StringBuilder content = new StringBuilder();

		content.append("@echo off\n");
		content.append("title grounding routine\n");
		content.append("cd /d %~dp0\n");
		content.append("cd src\n");

		String gameConf = prop.getProperty(gameSelection + ".conf");
		String gameServer = prop.getProperty(gameSelection + ".server");
		String gameExe = prop.getProperty(gameSelection + ".exe");

		if (StringUtils.isNotEmpty(gameExe)) {
			content.append(gameExe).append("\n").append("TIMEOUT /T 5\n");
		}

		content.append(gameServer).append(" config/").append(gameConf).append("\n");

		FileUtil.writeToFile("../../groundingroutine.bat", content.toString());
	}

	private static void loadConf() {
		prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream("../../conf/game.conf");

			prop.load(input);

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
