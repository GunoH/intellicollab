package nl.guno.ccollab.intellij;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.smartbear.beans.IGlobalOptions;
import com.smartbear.ccollab.client.CollabClientServerConnectivityException;
import com.smartbear.ccollab.client.ICollabClientInterface;
import com.smartbear.ccollab.client.LoginUtils;
import com.smartbear.ccollab.datamodel.User;

public class LoginTask extends Task.Modal {

    private static Logger logger = Logger.getInstance(LoginTask.class.getName());

    private Project project;
    private boolean success;

    private User user;

    private IGlobalOptions globalOptions;
    private ICollabClientInterface clientInterface;

    public LoginTask(Project project, IGlobalOptions globalOptions, ICollabClientInterface clientInterface) {
        super(project, MessageResources.message("task.login.title"), false);

        this.project = project;
        this.globalOptions = globalOptions;
        this.clientInterface = clientInterface;
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {

        progressIndicator.setText(MessageResources.message("progressIndicator.login"));

        try {
            user = LoginUtils.login(globalOptions, clientInterface);
        } catch (CollabClientServerConnectivityException e) {
            logger.info("Error when logging on to code collaborator server.", e);
            return;
        }
        success = true;
    }

    @Override
    public void onSuccess() {
        if (!success) {
            PluginUtil.createBalloon(
                    project,
                    MessageResources.message("task.login.connectionException.text"), 
                    MessageType.ERROR);
        }
    }

    public User getUser() {
        return user;
    }
}
