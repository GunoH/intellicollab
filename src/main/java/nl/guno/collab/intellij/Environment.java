package nl.guno.collab.intellij;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import nl.guno.collab.intellij.settings.IntelliCollabSettings;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.jetbrains.annotations.NotNull;

class Environment {

	private static final int EXIT_STATUS_SUCCESS = 0;

    boolean checkConnection() {
        String host = PluginUtil.extractHostFromUrl(IntelliCollabSettings.getInstance().getServerUrl());

        return host != null
                && exec(Platform.determine().pingCommand(host));

    }

    void checkSVNExecutable() throws SVNNotAvailableException {
        if (!exec(Platform.determine().svnCommand())) {
            throw new SVNNotAvailableException();
        }
    }

    private boolean exec(@NotNull String command) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CommandLine cmdLine = CommandLine.parse(command);
        DefaultExecutor executor = new DefaultExecutor();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        executor.setStreamHandler(streamHandler);
        ExecuteWatchdog watchdog = new ExecuteWatchdog(5000);
        executor.setWatchdog(watchdog);
        try {
            int exitStatus = executor.execute(cmdLine);
            return EXIT_STATUS_SUCCESS == exitStatus;

		} catch (IOException e) {
            System.err.println("Error evaluating command '" + command + "': " + e);
            System.err.println(outputStream.toString());
            return false;
		}
	}

    public static void main(String[] args) {
        try {
            new Environment().checkSVNExecutable();
            System.out.println("Correct version installed.");
        } catch (SVNNotAvailableException e) {
            System.err.println(MessageResources.message("action.error.svnNotAvailable.text"));
        }
    }

    class SVNNotAvailableException extends Exception {}
}
