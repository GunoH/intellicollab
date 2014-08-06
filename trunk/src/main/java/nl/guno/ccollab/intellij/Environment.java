package nl.guno.ccollab.intellij;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Environment {

	private static final int EXIT_STATUS_SUCCESS = 0;
	private static final String HOST = "codecollaborator.quinity.net";
    private static final String REQUIRED_VERSION = "1.11.20";

    private String output;

    public boolean checkConnection() throws InterruptedException {
		return exec("ping -n 1 " + HOST);
	}

    public void checkCVSExecutable() throws InterruptedException, CVSNotAvailableException, CVSWrongVersionException {
        if (!exec("cvs -v")) {
            throw new CVSNotAvailableException();
        }

        if (!output.contains(REQUIRED_VERSION)) {
            throw new CVSWrongVersionException();
        }
    }

    private boolean exec(String command) throws InterruptedException {
        output = null;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		CommandLine cmdLine = CommandLine.parse(command);
		DefaultExecutor executor = new DefaultExecutor();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        executor.setStreamHandler(streamHandler);
		ExecuteWatchdog watchdog = new ExecuteWatchdog(5000);
		executor.setWatchdog(watchdog);
		try {
			int exitstatus = executor.execute(cmdLine);
            output = outputStream.toString();
            return EXIT_STATUS_SUCCESS == exitstatus;

		} catch (IOException e) {
			return false;
		}
	}

    public static void main(String[] args) throws InterruptedException, CVSNotAvailableException, CVSWrongVersionException {
        try {
            new Environment().checkCVSExecutable();
            System.out.println("Correct version installed.");
        } catch (CVSNotAvailableException e) {
            System.err.println(MessageResources.message("action.error.cvsNotAvaliable.text"));
        } catch (CVSWrongVersionException e) {
            System.err.println(MessageResources.message("action.error.cvsWrongVersion.text"));
        }
    }

    public class CVSNotAvailableException extends Exception {}
    public class CVSWrongVersionException extends Exception {}
}
