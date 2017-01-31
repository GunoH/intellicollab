package nl.guno.collab.intellij;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.smartbear.CollabClientException;

public class BootstrapActivity implements StartupActivity {

    private static final Logger logger = Logger.getInstance(BootstrapActivity.class.getName());

    @Override
    public void runActivity(@NotNull Project project) {
        try {
            Context.initialize(project); 
        } catch (CollabClientException | IOException | InterruptedException e) {
            logger.error("Could not initialize client.", e);
        }
    }
}
