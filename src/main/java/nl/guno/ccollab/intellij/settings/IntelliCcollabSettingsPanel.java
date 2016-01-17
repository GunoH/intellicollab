package nl.guno.ccollab.intellij.settings;

import javax.swing.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;

public class IntelliCcollabSettingsPanel {

    private IntelliCcollabSettings mySettings;

    private JPanel rootComponent;
    private JTextField urlField;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public IntelliCcollabSettingsPanel() {
        mySettings = IntelliCcollabSettings.getInstance();
        reset();
    }

    public void reset() {
        setUrl(mySettings.getServerUrl());
        setUsername(mySettings.getUsername());
        setPassword(mySettings.getPassword());
    }

    public boolean isModified() {
        return !Comparing.equal(mySettings.getServerUrl(), getUrl())
                || !Comparing.equal(mySettings.getUsername(), getUsername())
                || !Comparing.equal(mySettings.getPassword(), getPassword());
    }

    public void apply() {
        mySettings.setServerUrl(getUrl());
        mySettings.setUsername(getUsername());
        mySettings.setPassword(getPassword());
    }


    public JComponent getPanel() {
        return rootComponent;
    }

    @NotNull
    public String getUrl() {
        return urlField.getText().trim();
    }

    @NotNull
    public String getUsername() {
        return usernameField.getText().trim();
    }

    public void setUrl(@NotNull final String url) {
        urlField.setText(url);
    }

    public void setUsername(@Nullable final String username) {
        usernameField.setText(username);
    }

    @NotNull
    private String getPassword() {
        return String.valueOf(passwordField.getPassword());
    }

    private void setPassword(@NotNull final String password) {
        // Show password as blank if password is empty
        passwordField.setText(StringUtil.isEmpty(password) ? null : password);
    }
}