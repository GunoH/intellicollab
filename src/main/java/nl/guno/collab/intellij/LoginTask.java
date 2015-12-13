package nl.guno.collab.intellij;

import java.util.Random;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.smartbear.beans.IGlobalOptions;
import com.smartbear.ccollab.client.CollabClientLoginCredentialsInvalidException;
import com.smartbear.ccollab.client.CollabClientServerConnectivityException;
import com.smartbear.ccollab.client.ICollabClientInterface;
import com.smartbear.ccollab.client.LoginUtils;
import com.smartbear.ccollab.datamodel.User;
import nl.guno.collab.intellij.ui.Notification;

class LoginTask extends Task.Modal {

    private static final int MAX_ATTEMPTS = 1;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();

    private static final Logger logger = Logger.getInstance(LoginTask.class.getName());

    private final Project project;
    private boolean success;

    private User user;
    
    private boolean authenticationErrorOccured;

    private final IGlobalOptions globalOptions;
    private final ICollabClientInterface clientInterface;

    public LoginTask(Project project, IGlobalOptions globalOptions, ICollabClientInterface clientInterface) {
        super(project, MessageResources.message("task.login.title"), false);

        this.project = project;
        this.globalOptions = globalOptions;
        this.clientInterface = clientInterface;
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {

        progressIndicator.setText(MessageResources.message("progressIndicator.login"));
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            logger.debug("Attempt #" + i + " to login");

            try {
                user = LoginUtils.login(globalOptions, clientInterface);
            } catch (CollabClientLoginCredentialsInvalidException e) {
                logger.info("Invalid username or password.", e);
                authenticationErrorOccured = true;
                return;
            } catch (CollabClientServerConnectivityException e) {
                logger.info("Error when logging on to collaborator server.", e);
                if (i == MAX_ATTEMPTS) {
                    return;
                }
                try {
                    logger.debug("Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    // Activity finished before we complete - exit.
                    logger.debug("Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            // increase backoff exponentially
            backoff *= 2;
        }
        success = true;
    }

    @Override
    public void onSuccess() {
        if (!success) {
            new Notification(
                    project,
                    MessageResources.message("task.login.connectionException.text"),
                    MessageType.ERROR).showBalloon();
        }
    }

    public User getUser() {
        return user;
    }

    public boolean authenticationErrorOccured() {
        return authenticationErrorOccured;
    }

    public boolean success() {
        return success;
    }
}
