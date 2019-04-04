package rmi.server;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

import org.ini4j.Wini;

import rmi.ConfigurationData;
import rmi.GaMiniOsServerConfigurationService;
import util.FileUtil;
import util.GaMiniOsServerConfig;

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

		moveMouse();
		setReplayFile(gameSelection);

		if (gameExe != null && !gameExe.isEmpty()) {
			content.append(gameExe).append("\n").append("TIMEOUT /T 5\n");
		}

		content.append(gameServer).append(" config/").append(gameConf).append(" >> ")
				.append(gameSelection + ".log 2>&1").append("\n");

		FileUtil.writeToFile("groundingroutine.bat", content.toString());
	}

	private void setReplayFile(String gameSelection) {
		if (gameSelection.equalsIgnoreCase("Neverball")) {
			try {
				copy(GaMiniOsServerRmiServer.gaFolderPath + "/keyPress_neverball.json",
						GaMiniOsServerRmiServer.gaFolderPath + "/keyPress.json");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (gameSelection.equalsIgnoreCase("Kobo")) {
			try {
				copy(GaMiniOsServerRmiServer.gaFolderPath + "/keyPress_kobo.json",
						GaMiniOsServerRmiServer.gaFolderPath + "/keyPress.json");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void copy(String sourcePath, String destinationPath) throws IOException {
		Files.copy(Paths.get(sourcePath), new FileOutputStream(destinationPath));
	}

	private void setNeverballResolution(ConfigurationData conf) {

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		Double hostWidth = screenSize.getWidth();
		Double clientWidth = Double.parseDouble(conf.getScreenWidth());
		Double clientHeight = Double.parseDouble(conf.getScreenHeight());

		Double scaleFactor = getResolutionScaleFactor(conf);

		String confWidth = null;
		String confHeight = null;

		confWidth = String.valueOf((int) (hostWidth * scaleFactor)); // full width was problematic so; 0.9
		Double ratio = clientHeight / clientWidth;
		confHeight = String.valueOf((int) (hostWidth * ratio * scaleFactor));

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

	private Double getResolutionScaleFactor(ConfigurationData conf) {
		Double scalingFactor = Double.valueOf(conf.getConfiguration().get("scaling-factor"));
		GaMiniOsServerConfig config = GaMiniOsServerConfig.get("./rmi-server.properties");
		Double screenSize = config.getScreenSize();
		return screenSize / scalingFactor; // lowerbound=100 => scale 0.9 upperbound=200 => scale 0.45

	}

	private static void configureGA(ConfigurationData config) {
		try {
			File confFile = new File(GaMiniOsServerRmiServer.gaFolderPath + "/config/common/video-x264-param.conf");
			Wini ini = new Wini(confFile);
			for (Map.Entry<String, String> e : config.getConfiguration().entrySet()) {
				if (e.getKey().contains("scaling-factor")) {
					//
				} else {
					ini.put("video", e.getKey(), e.getValue());
				}
			}
			ini.store();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String args[]) {
		moveMouse();
	}

	public static void moveMouse() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();

		// Search the devices for the one that draws the specified point.
		for (GraphicsDevice device : gs) {
			GraphicsConfiguration[] configurations = device.getConfigurations();
			for (GraphicsConfiguration config : configurations) {
				Rectangle bounds = config.getBounds();

				Point s = new Point(bounds.width, 0);

				try {
					Robot r = new Robot(device);
					r.mouseMove(s.x, s.y);
				} catch (AWTException e) {
					e.printStackTrace();
				}

			}
		}
	}

	@Override
	public void configureAndStartUp(ConfigurationData config) throws RemoteException {
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
	}

	private static Double calculateEncodingError(String encodingErrorFilePath) {
		System.out.println("encodingErrorpath:" + encodingErrorFilePath);
		String fileContent = FileUtil.readFile(encodingErrorFilePath);
		Double total = 0.0;
		int numRecords = 0;

		if (fileContent == null || fileContent.length() < 2) {
			// dummy value
			return 0.0012;
		}

		String[] values = fileContent.split("\n");

		for (String val : values) {
			total += Double.valueOf(val);
			numRecords++;
		}

		return total / numRecords;
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
	public Double stopServer() throws RemoteException {
		String gameWindowTitle = finalConfig.getGameWindow();
		String killcommand = "taskkill /F /FI \"WindowTitle eq " + gameWindowTitle + "\" /T";
		try {
			Runtime.getRuntime().exec(killcommand);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String encodingErrorFilePath = null;
		if (finalConfig.getGameSelection().equalsIgnoreCase("Neverball")) {
			encodingErrorFilePath = GaMiniOsServerRmiServer.gaFolderPath
					+ "/../NeverballPortable/App/Neverball/avg_encoding_error.txt";
		} else if (finalConfig.getGameSelection().equalsIgnoreCase("Kobo")) {
			encodingErrorFilePath = GaMiniOsServerRmiServer.gaFolderPath
					+ "/../KoboDeluxePortable/App/kobo/avg_encoding_error.txt";
		} else {
			System.err.println("Game folder not defined");
			throw new RemoteException("Define game folder");
		}
		Double encodingError = calculateEncodingError(encodingErrorFilePath);
		return encodingError;
	}

	@Override
	public Boolean startServer() throws RemoteException {
		configureAndStartUp(finalConfig);
		return true;
	}

}
