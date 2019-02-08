package rmi.server;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

import org.ini4j.Wini;

import model.Command;
import rmi.ConfigurationData;
import rmi.GaMiniOsServerConfigurationService;
import util.FileUtil;
import util.GaMiniOsServerConfig;
import util.SerializationUtil;

/**
 * 
 * @author kadirayk
 *
 */
public class GaMiniOsServerConfigurationServiceImpl extends UnicastRemoteObject
		implements GaMiniOsServerConfigurationService {

	protected GaMiniOsServerConfigurationServiceImpl() throws RemoteException {
		super();
	}

	private static ConfigurationData finalConfig;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static GaMiniOsServerConfig config;

	@Override
	public Double configureAndEvaluate(ConfigurationData config) throws RemoteException {

		Double score = 0.0;

		for (Map.Entry<String, String> entry : config.getConfiguration().entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}

		configureGA(config);

		createGameServerStartScript(config);

		final ProcessBuilder pb = new ProcessBuilder("groundingroutine.bat").redirectOutput(Redirect.INHERIT)
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

		stopGameServer(config);

		String responseTimeFilePath = GaMiniOsServerRmiServer.gaFolderPath + "/responseTime.json";
		score = calculateScore(responseTimeFilePath);

		return score;
	}

	private void createGameServerStartScript(ConfigurationData conf) {
		StringBuilder content = new StringBuilder();

		content.append("@echo off\n");
		content.append("title grounding routine\n");
		content.append("cd /d %~dp0\n");
		content.append("cd " + GaMiniOsServerRmiServer.gaFolderPath + "/\n");

		String gameConf = conf.getGameConf();
		String gameServer = conf.getGameServer();
		String gameExe = conf.getGameExe();
		String gameSelection = conf.getGameSelection();

		if (gameExe != null && !gameExe.isEmpty()) {
			content.append(gameExe).append("\n").append("TIMEOUT /T 5\n");
		}

		content.append(gameServer).append(" config/").append(gameConf).append(" >> ")
				.append(gameSelection + ".log 2>&1").append("\n");
		content.append("waitfor WaitForServerToBeReady /t 5\n ");
		content.append("ga-client ").append("config/client.rel.conf ").append("rtsp://localhost:").append("8554")
				.append("/desktop");

		FileUtil.writeToFile("groundingroutine.bat", content.toString());
	}

	private void createGameServerStartScriptWithoutClient(ConfigurationData conf) {
		StringBuilder content = new StringBuilder();

		content.append("@echo off\n");
		content.append("title grounding routine\n");
		content.append("cd /d %~dp0\n");
		content.append("cd " + GaMiniOsServerRmiServer.gaFolderPath + "/\n");

		String gameConf = conf.getGameConf();
		String gameServer = conf.getGameServer();
		String gameExe = conf.getGameExe();
		String gameSelection = conf.getGameSelection();

		if (gameSelection.equalsIgnoreCase("Neverball")) {
			setNeverballResolution(conf);
		}

		if (gameExe != null && !gameExe.isEmpty()) {
			content.append(gameExe).append("\n").append("TIMEOUT /T 5\n");
		}

		content.append(gameServer).append(" config/").append(gameConf).append(" >> ")
				.append(gameSelection + ".log 2>&1").append("\n");

		FileUtil.writeToFile("groundingroutine.bat", content.toString());
	}

	private void setNeverballResolution(ConfigurationData conf) {

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		Double hostWidth = screenSize.getWidth();
		Double clientWidth = Double.parseDouble(conf.getScreenWidth());
		Double clientHeight = Double.parseDouble(conf.getScreenHeight());

		config = GaMiniOsServerConfig.get("./rmi-server.properties");
		Double scaleFactor = config.getResolutionScaleFactor();

		String confWidth = null;
		String confHeight = null;
		
		confWidth = String.valueOf((int) (hostWidth * scaleFactor)); // full width was problematic so; 0.9
		Double ratio = clientHeight / clientWidth;
		confHeight = String.valueOf( (int) (hostWidth * ratio * scaleFactor));

		String appDataPath = System.getenv("APPDATA");
		String rcFile = appDataPath + "/Neverball/neverballrc";
		String rcFileContent = FileUtil.readFile(rcFile);
		String[] rcLines = rcFileContent.split("\n");
		StringBuilder newRcFileContent = new StringBuilder();
		for (String line : rcLines) {
			if (line.startsWith("width")) {
				String[] words = line.split("\\s+");
				words[1] = confWidth;
				line = words[0] + "                     " + words[1];
			} else if (line.startsWith("height")) {
				String[] words = line.split("\\s+");
				words[1] = confHeight;
				line = words[0] + "                    " + words[1];
			}
			newRcFileContent.append(line).append("\n");
		}
		FileUtil.writeToFile(rcFile, newRcFileContent.toString());
	}

	private static void stopGameServer(ConfigurationData config) {
		String gameWindowTitle = config.getGameWindow();
		String killcommand = "taskkill /F /FI \"WindowTitle eq " + gameWindowTitle + "\" /T";
		try {
			Runtime.getRuntime().exec(killcommand);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void configureGA(ConfigurationData config) {
		try {
			File confFile = new File(GaMiniOsServerRmiServer.gaFolderPath + "/config/common/video-x264-param.conf");
			Wini ini = new Wini(confFile);
			for (Map.Entry<String, String> e : config.getConfiguration().entrySet()) {
				ini.put("video", e.getKey(), e.getValue());
			}
			ini.store();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

//	private static List<Command> readResponseTimes(String path) {
//		List<Command> commandList = null;
//		ObjectMapper mapper = new ObjectMapper();
//		try {
//			Command[] commands = mapper.readValue(new File(path), Command[].class);
//			commandList = Arrays.asList(commands);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return commandList;
//	}

	private static Double calculateScore(String responseTimeFilePath) {
		List<Command> commandList = SerializationUtil.readResponseTimes(responseTimeFilePath);
		Double totalDelay = 0.0;
		int numberOfCommandsWithReceiveTime = 0;
		for (Command c : commandList) {
			if (c.getReceivetimeStamp() != null && c.getReceivetimeStamp() > 0) {
				totalDelay += c.getDelay();
				numberOfCommandsWithReceiveTime++;
			}
		}

		return totalDelay / numberOfCommandsWithReceiveTime;
	}

	@Override
	public String configureAndStartUp(ConfigurationData config) throws RemoteException {
		finalConfig = config;
		configureGA(config);
		createGameServerStartScriptWithoutClient(config);

		final ProcessBuilder pb = new ProcessBuilder("groundingroutine.bat").redirectOutput(Redirect.INHERIT)
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

		return "Started final configuration";
	}
	
	@Override
	public Boolean stopServerByWindowTitle(String gameWindowTitle) throws RemoteException {
		String killcommand = "taskkill /F /FI \"WindowTitle eq " + gameWindowTitle + "\" /T";
		try {
			Runtime.getRuntime().exec(killcommand);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}


	@Override
	public Boolean stopServer() throws RemoteException {
		String gameWindowTitle = finalConfig.getGameWindow();
		String killcommand = "taskkill /F /FI \"WindowTitle eq " + gameWindowTitle + "\" /T";
		try {
			Runtime.getRuntime().exec(killcommand);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public Boolean startServer() throws RemoteException {
		configureAndStartUp(finalConfig);
		return true;
	}

}
