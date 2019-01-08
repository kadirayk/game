package deployment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Deployment {
	public static String processId;
	public static String host;
	public static String port;

	public static void main(String[] args) {
//		port = "8181";
		processId = args[0];
		host = args[1];
		port = args[2];

		SpringApplication.run(Deployment.class, args);
	}

}
