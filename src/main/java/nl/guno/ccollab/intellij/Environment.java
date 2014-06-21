package nl.guno.ccollab.intellij;

import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;

public class Environment {

	private static final int EXIT_STATUS_SUCCESS = 0;
	private static final String HOST = "codecollaborator.quinity.net";

	public static boolean checkConnection() throws InterruptedException {
		return exec("ping -n 1 " + HOST);
	}

	public static boolean checkCVSExecutable() throws InterruptedException {
		return exec("cvs -v");
	}

	private static boolean exec(String command) throws InterruptedException {

		CommandLine cmdLine = CommandLine.parse(command);
		DefaultExecutor executor = new DefaultExecutor();
		ExecuteWatchdog watchdog = new ExecuteWatchdog(5000);
		executor.setWatchdog(watchdog);
		try {
			int exitstatus = executor.execute(cmdLine);
			return EXIT_STATUS_SUCCESS == exitstatus;
		} catch (IOException e) {
			return false;
		}
	}
}
