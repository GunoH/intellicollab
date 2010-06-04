package com.quinity.ccollab.intellij;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.DefaultJDOMExternalizer;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import com.quinity.ccollab.intellij.ui.IntelliCcollabConfigurationForm;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
import javax.swing.JComponent;
import java.net.MalformedURLException;

public class IntelliCcollabApplicationComponent implements ApplicationComponent, Configurable, JDOMExternalizable {
    private IntelliCcollabConfigurationForm form;
    public String serverURL;
	
    public void initComponent() {
    }

    public void disposeComponent() {
    }

    @NotNull
	public String getComponentName() {
        return "IntelliCcollabApplicationComponent";
    }

    public String getServerURL() {
        return serverURL;
    }

    public void setServerURL(final String serverURL) {
        this.serverURL = serverURL;
    }

    public String getDisplayName() {
        // Return name of configuration icon in Settings dialog
        return "IntelliCcollab";
    }

    public Icon getIcon() {
        return IconLoader.getIcon("/icons/codecollaborator.png");
    }

    public String getHelpTopic() {
        return null;
    }

    public JComponent createComponent() {
        if (form == null) {
            form = new IntelliCcollabConfigurationForm();
        }
        return form.getRootComponent();
    }

    public boolean isModified() {
        return form != null && form.isModified(this);
    }

    public void apply() throws ConfigurationException {
        if (form != null) {
			try {
				form.getData(this);
			} catch (MalformedURLException e) {
				throw new ConfigurationException("Invalid URL");
			}
		}
    }

    public void reset() {
        if (form != null) {
            // Reset form data from component
            form.setData(this);
        }
    }

    public void disposeUIResources() {
        form = null;
    }

    public void readExternal(Element element) throws InvalidDataException {
        DefaultJDOMExternalizer.readExternal(this, element);
    }

    public void writeExternal(Element element) throws WriteExternalException {
        DefaultJDOMExternalizer.writeExternal(this, element);
    }
}
