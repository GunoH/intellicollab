package nl.guno.ccollab.intellij;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.smartbear.beans.IGlobalOptions;
import com.smartbear.ccollab.client.CollabClientIncompatibleVersionException;
import com.smartbear.ccollab.client.CollabClientLoginCredentialsInvalidException;
import com.smartbear.ccollab.client.CollabClientServerConnectivityException;
import com.smartbear.ccollab.client.CollabClientServerUrlConnectionException;
import com.smartbear.ccollab.client.CollabClientUserDoesNotExistException;
import com.smartbear.ccollab.client.ICollabClientInterface;
import com.smartbear.ccollab.client.LoginUtils;
import com.smartbear.ccollab.datamodel.DataModelException;
import com.smartbear.ccollab.datamodel.User;
import org.jetbrains.annotations.NotNull;

public class LoginTask extends Task.Modal {

    private static Logger logger = Logger.getInstance(LoginTask.class.getName());

    private boolean success;

    private User user;

    private IGlobalOptions globalOptions;
    private ICollabClientInterface clientInterface;

    public LoginTask(Project project, IGlobalOptions globalOptions, ICollabClientInterface clientInterface) {
        super(project, MessageResources.message("task.login.title"), false);

        this.globalOptions = globalOptions;
        this.clientInterface = clientInterface;
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {

        progressIndicator.setText(MessageResources.message("progressIndicator.login"));

        try {
            user = LoginUtils.login(globalOptions, clientInterface);
        } catch (DataModelException e) {
            logger.info("Error when logging on to code collaborator server.", e);
            return;
        } catch (CollabClientServerUrlConnectionException e) {
            logger.info("Error when logging on to code collaborator server.", e);
            return;
        } catch (CollabClientLoginCredentialsInvalidException e) {
            logger.info("Error when logging on to code collaborator server.", e);
            return;
        } catch (CollabClientIncompatibleVersionException e) {
            logger.info("Error when logging on to code collaborator server.", e);
            return;
        } catch (CollabClientUserDoesNotExistException e) {
            logger.info("Error when logging on to code collaborator server.", e);
            return;
        } catch (CollabClientServerConnectivityException e) {
            logger.info("Error when logging on to code collaborator server.", e);
            return;
        }
        success = true;
    }

    @Override
    public void onSuccess() {
        if (!success) {
            Messages.showErrorDialog(MessageResources.message("task.login.connectionException.text"),
                    MessageResources.message("task.login.connectionException.title"));
        }
    }

    public User getUser() {
        return user;
    }
}