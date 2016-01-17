package nl.guno.ccollab.intellij.settings;

import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.ide.passwordSafe.PasswordSafeException;
import com.intellij.openapi.components.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import nl.guno.ccollab.intellij.MessageResources;

@State(
        name = "IntelliCcollabSettings",
        storages = {@Storage(
                file = StoragePathMacros.APP_CONFIG + "/intelliccollab_settings.xml")})
public class IntelliCcollabSettings implements PersistentStateComponent<IntelliCcollabSettings.State> {

    private static final String SETTINGS_PASSWORD_KEY = "IntelliCcollab_server_password";
    private static final Logger LOG = Logger.getInstance(IntelliCcollabSettings.class.getName());

    private State myState = new State();

    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(State state) {
        myState = state;
    }

    public static class State {
        public String serverUrl = MessageResources.message("configuration.serverURL.default");
        public String username = null;
    }

    public static IntelliCcollabSettings getInstance() {
        return ServiceManager.getService(IntelliCcollabSettings.class);
    }

    public String getUsername() {
        return myState.username;
    }

    public void setUsername(String username) {
        myState.username = username;
    }

    public String getServerUrl() {
        return myState.serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        myState.serverUrl = serverUrl;
    }

    public String getPassword() {
        final String username = getUsername();
        if (StringUtil.isEmptyOrSpaces(username)) return "";

        String password;
        try {
            password = PasswordSafe.getInstance().getPassword(null, IntelliCcollabSettings.class,
                    SETTINGS_PASSWORD_KEY);
        }
        catch (PasswordSafeException e) {
            LOG.info("Couldn't get password for key [" + SETTINGS_PASSWORD_KEY + "]", e);
            password = "";
        }

        return StringUtil.notNullize(password);
    }

    public void setPassword(String password) {
        try {
            PasswordSafe.getInstance().storePassword(null, IntelliCcollabSettings.class, SETTINGS_PASSWORD_KEY, password);
        }
        catch (PasswordSafeException e) {
            LOG.info("Couldn't set password for key [" + SETTINGS_PASSWORD_KEY + "]", e);
        }
    }
}
