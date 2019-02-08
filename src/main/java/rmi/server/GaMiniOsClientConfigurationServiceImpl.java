package rmi.server;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import model.Command;
import rmi.GaMiniOsClientConfigurationService;
import rmi.client.GaMiniOsServerRmiClient;
import util.FileUtil;
import util.SerializationUtil;

public class GaMiniOsClientConfigurationServiceImpl extends UnicastRemoteObject
		implements GaMiniOsClientConfigurationService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected GaMiniOsClientConfigurationServiceImpl() throws RemoteException {
		super();
	}

	@Override
	public Double startGaClientAndEvaluate(String gaMiniOsServerIp, Integer gaMiniOsServerPort) throws RemoteException {
		System.out.println("gaMiniOsServerIp:" + gaMiniOsServerIp + " gaMiniOsServerPort:" + gaMiniOsServerPort);
		createGameServerStartScript(gaMiniOsServerIp);

		final ProcessBuilder pb = new ProcessBuilder("groundingroutine.bat").redirectOutput(Redirect.INHERIT)
				.redirectError(Redirect.INHERIT);
		System.out.print("evaluating...");
		Double score = 0.0;
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

		GaMiniOsServerRmiClient rmiClient = new GaMiniOsServerRmiClient(gaMiniOsServerIp, gaMiniOsServerPort);
		rmiClient.stopServer();

		String responseTimeFilePath = "C:/ga/responseTime.json";
		score = calculateScore(responseTimeFilePath);

		return score;
	}

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

	private void createGameServerStartScript(String GaMiniOsServerIp) {
		StringBuilder content = new StringBuilder();

		content.append("@echo off\n");
		content.append("title starting ga client \n");
		content.append("cd /d %~dp0\n");
		content.append("cd C:/ga/\n");

		content.append("waitfor WaitForServerToBeReady /t 5\n ");
		content.append("ga-client ").append("config/client.rel.conf ").append("rtsp://" + GaMiniOsServerIp + ":")
				.append("8554").append("/desktop");

		FileUtil.writeToFile("groundingroutine.bat", content.toString());
	}

}
