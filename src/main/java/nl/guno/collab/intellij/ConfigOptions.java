package nl.guno.collab.intellij;

import java.io.IOException;

import com.smartbear.beans.ConfigUtils;
import com.smartbear.beans.IGlobalOptions;
import com.smartbear.beans.IScmOptions;
import com.smartbear.collections.Pair;

public class ConfigOptions {
    
    private static ConfigOptions instance;
    private Pair<IGlobalOptions, IScmOptions> configOptions;

    private ConfigOptions() throws IOException {
        this.configOptions = ConfigUtils.loadConfigFiles();
    }

    public static ConfigOptions getInstance() throws IOException {
        if (instance == null) {
            instance = new ConfigOptions();
        }
        return instance;
    }

    IGlobalOptions getA() {
        return configOptions.getA();
    }

    IScmOptions getB() {
        return configOptions.getB();
    }
}
