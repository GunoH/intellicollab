package nl.guno.collab.intellij;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;

public class BootstrapActivity implements StartupActivity {

    @Override
    public void runActivity(@NotNull Project project) {
        Properties props = new Properties();
        try {
            props.load(getClass().getResourceAsStream("/log4j.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        PropertyConfigurator.configure(props);
    }
}
