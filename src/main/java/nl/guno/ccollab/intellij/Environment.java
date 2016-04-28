package nl.guno.ccollab.intellij;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.jetbrains.annotations.NotNull;

import nl.guno.ccollab.intellij.settings.IntelliCcollabSettings;

class Environment {

	private static final int EXIT_STATUS_SUCCESS = 0;
    static final String REQUIRED_SVN_VERSION = "1.6";

    private String output;

    boolean checkConnection() throws IOException {
        return exec("ping -n 1 " + PluginUtil.extractHostFromUrl(IntelliCcollabSettings.getInstance().getServerUrl()));
	}

    void checkSVNExecutable() throws SVNWrongVersionException, SVNNotAvailableException, IOException {
        if (!exec("svn --version")) {
            throw new SVNNotAvailableException();
        }

        if (!output.contains(REQUIRED_SVN_VERSION)) {
            throw new SVNWrongVersionException();
        }
    }

    private boolean exec(@NotNull String command) throws IOException {
        output = null;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CommandLine cmdLine = CommandLine.parse(command);
        DefaultExecutor executor = new DefaultExecutor();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        executor.setStreamHandler(streamHandler);
        ExecuteWatchdog watchdog = new ExecuteWatchdog(5000);
        executor.setWatchdog(watchdog);
        try {
            int exitStatus = executor.execute(cmdLine);
            output = outputStream.toString();
            return EXIT_STATUS_SUCCESS == exitStatus;

		} catch (IOException e) {
            System.err.println("Error evaluating command '" + command + "': " + e);
            System.err.println(outputStream.toString());
            return false;
		}
	}

    public static void main(String[] args) throws InterruptedException, IOException {
        try {
            new Environment().checkSVNExecutable();
            System.out.println("Correct version installed.");
        } catch (SVNNotAvailableException e) {
            System.err.println(MessageResources.message("action.error.svnNotAvailable.text", REQUIRED_SVN_VERSION));
        } catch (SVNWrongVersionException e) {
            System.err.println(MessageResources.message("action.error.svnWrongVersion.text", REQUIRED_SVN_VERSION));
        }
    }

    class SVNNotAvailableException extends Exception {}
    class SVNWrongVersionException extends Exception {}
}
