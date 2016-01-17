package nl.guno.ccollab.intellij.settings;

import javax.swing.*;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.ide.passwordSafe.PasswordSafeException;
import com.intellij.openapi.components.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.util.text.StringUtil;
import nl.guno.ccollab.intellij.MessageResources;

public class IntelliCcollabSettingsConfigurable implements SearchableConfigurable {

    private IntelliCcollabSettingsPanel mySettingsPane;

    public IntelliCcollabSettingsConfigurable() {
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

    public void apply() throws ConfigurationException {
        if (mySettingsPane != null) {
            mySettingsPane.apply();
        }
    }

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
