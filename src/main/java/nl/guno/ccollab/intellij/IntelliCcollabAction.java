package nl.guno.ccollab.intellij;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.smartbear.CollabClientException;
import com.smartbear.beans.ConfigUtils;
import com.smartbear.beans.IGlobalOptions;
import com.smartbear.beans.IScmOptions;
import com.smartbear.ccollab.CommandLineClient;
import com.smartbear.ccollab.client.ICollabClientInterface;
import com.smartbear.ccollab.datamodel.Engine;
import com.smartbear.ccollab.datamodel.User;
import com.smartbear.collections.Pair;

import java.io.IOException;

public abstract class IntelliCcollabAction extends AnAction {

    /**
     * Global and SCM options, created by {@link #init(com.intellij.openapi.project.Project)}
     */
    protected static IntelliCcollabGlobalOptions globalOptions;

    /**
     * SCM options, created by {@link #init(com.intellij.openapi.project.Project)}
     */
    protected static IScmOptions scmOptions;

    /**
     * Interface to user for prompting, etc...
     * created by {@link #init(com.intellij.openapi.project.Project)}
     */
    protected static ICollabClientInterface clientInterface;

    /**
     * Connection to Code Collaborator server
     * created by {@link #init(com.intellij.openapi.project.Project)}
     */
    protected static Engine engine;

    /**
     * Currently logged-in user
     * created by {@link #init(com.intellij.openapi.project.Project)}
     */
    protected static User user;

    protected static void init(Project project) throws CollabClientException, IOException {
        // If we've already initialized, don't do it again.
        if ( engine != null ) {
            return;
        }

        //load options from config files
        Pair<IGlobalOptions, IScmOptions> configOptions = ConfigUtils.loadConfigFiles();
        globalOptions = new IntelliCcollabGlobalOptions(configOptions.getA());

        if (globalOptions.settingsIncomplete()) {
            Messages.showInfoMessage(MessageResources.message("error.mandatorySettingsMissing"),
                    MessageResources.message("errorDialog.errorOccurred.title"));
            return;
        }

        scmOptions = configOptions.getB();

        //initialize client interface
        clientInterface = new CommandLineClient(globalOptions);

        //connect to server and log in (throws exception if authentication fails, can't find server, etc...)
        LoginTask loginTask = new LoginTask(project, globalOptions, clientInterface);
        loginTask.queue();

        user = loginTask.getUser();

        if (user != null) {
            engine = user.getEngine();
        }
    }

    /**
     * Called to clean up a previous call to <code>init()</code>.
     * <p/>
     * <b>THIS IS CRITICAL</b>.  If you do not close out your <code>CollabClientConnection</code>
     * object, data might not be flushed out to the server!
     */
    protected void finished() {
        if (engine != null) {
            engine.close(true);
        }
    }

}