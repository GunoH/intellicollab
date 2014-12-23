package nl.guno.ccollab.intellij;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

class Environment {

	private static final int EXIT_STATUS_SUCCESS = 0;
	private static final String HOST = "codecollaborator.quinity.net";
    static final String REQUIRED_CVS_VERSION = "1.11.20";
    static final String REQUIRED_SVN_VERSION = "1.6";

    private String output;

    public boolean checkConnection() throws InterruptedException {
		return exec("ping -n 1 " + HOST);
	}

    public void checkCVSExecutable() throws InterruptedException, CVSNotAvailableException, CVSWrongVersionException {
        if (!exec("cvs -v")) {
            throw new CVSNotAvailableException();
        }

        if (!output.contains(REQUIRED_CVS_VERSION)) {
            throw new CVSWrongVersionException();
        }
    }

    public void checkSVNExecutable() throws InterruptedException, SVNWrongVersionException, SVNNotAvailableException {
        if (!exec("svn --version")) {
            throw new SVNNotAvailableException();
        }

        if (!output.contains(REQUIRED_SVN_VERSION)) {
            throw new SVNWrongVersionException();
        }
    }

    private boolean exec(String command) {
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
            new Environment().checkSVNExecutable();
            System.out.println("Correct version installed.");
        } catch (CVSNotAvailableException e) {
            System.err.println(MessageResources.message("action.error.cvsNotAvaliable.text", REQUIRED_CVS_VERSION));
        } catch (CVSWrongVersionException e) {
            System.err.println(MessageResources.message("action.error.cvsWrongVersion.text", REQUIRED_CVS_VERSION));
        } catch (SVNNotAvailableException e) {
            System.err.println(MessageResources.message("action.error.svnNotAvaliable.text", REQUIRED_SVN_VERSION));
        } catch (SVNWrongVersionException e) {
            System.err.println(MessageResources.message("action.error.svnWrongVersion.text", REQUIRED_SVN_VERSION));
        }
    }

    public class CVSNotAvailableException extends Exception {}
    public class CVSWrongVersionException extends Exception {}
    public class SVNNotAvailableException extends Exception {}
    public class SVNWrongVersionException extends Exception {}
}
