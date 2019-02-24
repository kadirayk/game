package util;

import java.io.IOException;

public class VMUtil {

	public static void startVM(String rmiVmType) {
		if (RmiVmType.REMOTE.value().equals(rmiVmType)) {
			return; // if remote vm can not send start command
		}

		Runtime rt = Runtime.getRuntime();
		try {
			String command = null;
			if (RmiVmType.LOCAL_LINUX.value().equals(rmiVmType)) {
				command = "VBoxManage startvm win10_32";
			} else if (RmiVmType.LOCAL_WINDOWS.value().equals(rmiVmType)) {
				command = "\"C:\\Program Files\\Oracle\\VirtualBox\\VBoxManage.exe\" startvm win10_32";
			} else {
				System.err.println("invalid VM Type");
				return;
			}
			rt.exec(command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
