package nl.guno.ccollab.intellij;

import java.net.MalformedURLException;

import javax.swing.*;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.DefaultJDOMExternalizer;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import nl.guno.ccollab.intellij.ui.IntelliCcollabConfigurationForm;
import org.jetbrains.annotations.Nullable;

public class IntelliCcollabApplicationComponent implements ApplicationComponent, Configurable, JDOMExternalizable {
    private IntelliCcollabConfigurationForm form;
    
    /* The settings themselves; these need to be public for IntelliJ to save them. */
    
    public String serverURL;
    public String username;
    public String password;

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
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
            form = new IntelliCcollabConfigurationForm();
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
