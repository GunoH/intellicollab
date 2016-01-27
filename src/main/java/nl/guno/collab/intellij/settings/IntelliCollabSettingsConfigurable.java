package nl.guno.collab.intellij.settings;

import javax.swing.*;

import nl.guno.ccollab.intellij.settings.IntelliCcollabSettingsPanel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;

public class IntelliCollabSettingsConfigurable implements SearchableConfigurable {

    private IntelliCcollabSettingsPanel mySettingsPane;

    public IntelliCollabSettingsConfigurable() {
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "IntelliCcollab";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @NotNull
    @Override
    public String getId() {
        return "intelliccollab";
    }

    @Nullable
    @Override
    public Runnable enableSearch(String option) {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (mySettingsPane == null) {
            mySettingsPane = new IntelliCcollabSettingsPanel();
        }
        return mySettingsPane.getPanel();
    }

    @Override
    public boolean isModified() {
        return mySettingsPane != null && mySettingsPane.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        if (mySettingsPane != null) {
            mySettingsPane.apply();
        }
    }

    @Override
    public void reset() {
        if (mySettingsPane != null) {
            mySettingsPane.reset();
        }
    }

    @Override
    public void disposeUIResources() {
        mySettingsPane = null;
    }
}
