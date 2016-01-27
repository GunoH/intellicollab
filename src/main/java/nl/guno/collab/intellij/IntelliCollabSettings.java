package nl.guno.collab.intellij;

import java.net.MalformedURLException;

import javax.swing.*;

import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.ide.passwordSafe.PasswordSafeException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import nl.guno.collab.intellij.ui.IntelliCollabConfigurationForm;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.DefaultJDOMExternalizer;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import org.jetbrains.annotations.Nullable;

public class IntelliCollabSettings implements ApplicationComponent, Configurable, JDOMExternalizable {
    private IntelliCollabConfigurationForm form;

    private static final String SETTINGS_PASSWORD_KEY = "IntelliCcollab_server_password";
    private static final Logger LOG = Logger.getInstance(IntelliCollabSettings.class.getName());

    /* The settings themselves; these need to be public for IntelliJ to save them. */
    
    @SuppressWarnings("AccessCanBeTightened")
    public String serverURL;
    @SuppressWarnings("AccessCanBeTightened")
    public String username;

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
    }

    @Override
    @NotNull
    public String getComponentName() {
        return getClass().getSimpleName();
    }

    public String getServerURL() {
        return serverURL;
    }

    public void setServerURL(@Nullable String serverURL) {
        this.serverURL = serverURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    @Override @NotNull
    public String getDisplayName() {
        return "IntelliCcollab";
    }

    @Override @Nullable
    public String getHelpTopic() {
        return null;
    }

    @Override
    public JComponent createComponent() {
        if (form == null) {
            form = new IntelliCollabConfigurationForm();
        }
        return form.getRootComponent();
    }

    @Override
    public boolean isModified() {
        return form != null && form.isModified(this);
    }

    @Override
    public void apply() throws ConfigurationException {
        if (form != null) {
            try {
                form.getData(this);
            } catch (MalformedURLException e) {
                throw new ConfigurationException("Invalid URL");
            }
        }
    }

    @Override
    public void reset() {
        if (form != null) {
            form.setData(this);
        }
    }

    @Override
    public void disposeUIResources() {
        form = null;
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        DefaultJDOMExternalizer.readExternal(this, element);
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        DefaultJDOMExternalizer.writeExternal(this, element);
    }
}
