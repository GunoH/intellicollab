package nl.guno.collab.intellij;

import java.io.IOException;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.smartbear.CollabClientException;

abstract class IntelliCollabAction extends AnAction {


    static boolean init(final Project project) throws CollabClientException, IOException, InterruptedException {
        return Context.initialize(project);
    }

    void finished() {
        Context.deInitialize();
    }
}
