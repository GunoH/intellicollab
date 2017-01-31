package nl.guno.collab.intellij;

import java.io.IOException;

import com.intellij.openapi.project.Project;
import com.smartbear.CollabClientException;
import com.smartbear.beans.IScmOptions;
import com.smartbear.ccollab.datamodel.Engine;
import com.smartbear.ccollab.datamodel.User;

class Context {

    /**
     * SCM options, created by {@link #initialize(Project)}
     */
    static IScmOptions scmOptions;

    /**
     * Connection to Collaborator server
     * created by {@link #initialize(Project)}
     */
    static Engine engine;

    /**
     * Currently logged-in user
     * created by {@link #initialize(Project)}
     */
    static User user;

    static boolean initialize(final Project project) throws CollabClientException, IOException, InterruptedException {
        // If we've already initialized, don't do it again.
        if ( engine != null ) {
            return true;
        }

        LoginHelper loginHelper = new LoginHelper(project);
        if (!loginHelper.login()) {
            return false;
        }

        scmOptions = ConfigOptions.getInstance().getB();
        user = loginHelper.getUser();

        if (user != null) {
            engine = user.getEngine();
        }

        return true;
    }

    /**
     * Called to clean up a previous call to {@link #initialize(Project)}.
     * <p/>
     * <b>THIS IS CRITICAL</b>.  If you do not close out your {@code CollabClientConnection}
     * object, data might not be flushed out to the server!
     */
    static void deInitialize() {
        if (engine != null) {
            engine.close(true);
        }
    }

}
