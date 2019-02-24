package deployment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.zeroturnaround.zip.ZipUtil;

import model.InterviewFillout;
import model.Question;
import rmi.client.GaMiniOsServerRmiClient;
import strategy.strategy1.Strategy;
import util.FileUtil;
import util.GamingPrototypeConfig;
import util.SerializationUtil;

@Controller
public class SpringController {

	private static final GamingPrototypeConfig gamingPrototypeConfig = GamingPrototypeConfig
			.get("./strategies/strategy1/conf/gaming-prototype.properties");

	@RequestMapping(value = "/downloadClient", method = RequestMethod.GET)
	public StreamingResponseBody getGameClient(HttpServletResponse response) throws IOException {

		configureClient(getGaMiniOsServerIp(), gamingPrototypeConfig.getGaServerPort());

		String clientPath = getGameClient();
		response.setContentType("text/html;charset=UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename=\"client.zip\"");
		InputStream inputStream = new FileInputStream(new File(clientPath));

		return outputStream -> {
			int nRead;
			byte[] data = new byte[1024];
			while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
				outputStream.write(data, 0, nRead);
			}
			inputStream.close();
		};
	}
	
	public static String getGaMiniOsServerIp() {
		InterviewFillout interviewFillout = SerializationUtil.readAsJSON("strategies/strategy1/");
		Question q = new Question();
		q.setId("server_entry");
		String serverIp = interviewFillout.getAnswer(q);
		return serverIp;
	}

	/**
	 * Stops GA server not this server itself
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/stopServer", method = RequestMethod.GET)
	@ResponseBody
	public String stopServer(HttpServletResponse response) throws IOException {
		String ip = getGaMiniOsServerIp(); //gamingPrototypeConfig.getServerVmIp();
		int port = gamingPrototypeConfig.getServerRmiServerPort();
		GaMiniOsServerRmiClient rmiClient = new GaMiniOsServerRmiClient(ip, port);
		rmiClient.stopServer();
		return "OK";
	}
	
	/**
	 * Starts GA server not this server itself
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/startServer", method = RequestMethod.GET)
	@ResponseBody
	public String startServer(HttpServletResponse response) throws IOException {
		String ip = getGaMiniOsServerIp(); //gamingPrototypeConfig.getServerVmIp();
		int port = gamingPrototypeConfig.getServerRmiServerPort();
		GaMiniOsServerRmiClient rmiClient = new GaMiniOsServerRmiClient(ip, port);
		rmiClient.startServer();
		return "OK";
	}
	
	@RequestMapping(value = "/play", method = RequestMethod.GET)
    public String greeting(Model model) {
        return "main";
    }

	private static void configureClient(String ip, String port) {
		StringBuilder content = new StringBuilder();
		content.append("@echo off\n");
		content.append("title PROSECO Gaming\n");
		content.append("cd lib\n");
		content.append("ga-client client.rel.conf rtsp://").append(ip).append(":").append(port).append("/desktop");

		FileUtil.writeToFile("./client/run_client.bat", content.toString());
	}

	private String getGameClient() {
		String clientPath = "./client";
		String zipFilePath = clientPath + ".zip";

		ZipUtil.pack(new File(clientPath), new File(zipFilePath));

		return zipFilePath;
	}
}
