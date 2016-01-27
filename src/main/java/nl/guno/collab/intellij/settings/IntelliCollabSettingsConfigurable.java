package nl.guno.collab.intellij.settings;

import javax.swing.*;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;

public class IntelliCollabSettingsConfigurable implements SearchableConfigurable {

    private IntelliCollabSettingsPanel mySettingsPane;

    public IntelliCollabSettingsConfigurable() {
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "IntelliCollab";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @NotNull
    @Override
    public String getId() {
        return "intellicollab";
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
            mySettingsPane = new IntelliCollabSettingsPanel();
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
