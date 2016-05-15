package nl.guno.collab.intellij.settings;

import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.ide.passwordSafe.PasswordSafeException;
import com.intellij.openapi.components.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import nl.guno.collab.intellij.MessageResources;

@State(name = "IntelliCollabSettings")
public class IntelliCollabSettings implements PersistentStateComponent<IntelliCollabSettings.State> {

    private static final String SETTINGS_PASSWORD_KEY = "IntelliCollab_server_password";
    private static final Logger LOG = Logger.getInstance(IntelliCollabSettings.class.getName());

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

    public static IntelliCollabSettings getInstance() {
        return ServiceManager.getService(IntelliCollabSettings.class);
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
            password = PasswordSafe.getInstance().getPassword(null, IntelliCollabSettings.class,
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
            PasswordSafe.getInstance().storePassword(null, IntelliCollabSettings.class, SETTINGS_PASSWORD_KEY, password);
        }
        catch (PasswordSafeException e) {
            LOG.info("Couldn't set password for key [" + SETTINGS_PASSWORD_KEY + "]", e);
        }
    }
}
