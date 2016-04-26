package nl.guno.ccollab.intellij.settings;

import javax.swing.*;

import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;

public class IntelliCcollabSettingsPanel {

    private IntelliCcollabSettings mySettings;

    private JPanel rootComponent;
    private JTextField urlField;
    private JTextField usernameField;
    private JPasswordField passwordField;

    IntelliCcollabSettingsPanel() {
        mySettings = IntelliCcollabSettings.getInstance();
        reset();
    }

    void reset() {
        urlField.setText(mySettings.getServerUrl());
        usernameField.setText(mySettings.getUsername());

        final String password = mySettings.getPassword();
        // Show password as blank if password is empty
        passwordField.setText(StringUtil.isEmpty(password) ? null : password);
    }

    boolean isModified() {
        return !Comparing.equal(mySettings.getServerUrl(), urlField.getText().trim())
                || !Comparing.equal(mySettings.getUsername(), usernameField.getText().trim())
                || !Comparing.equal(mySettings.getPassword(), String.valueOf(passwordField.getPassword()));
    }

    void apply() {
        mySettings.setServerUrl(urlField.getText().trim());
        mySettings.setUsername(usernameField.getText().trim());
        mySettings.setPassword(String.valueOf(passwordField.getPassword()));
    }


    JComponent getPanel() {
        return rootComponent;
    }
}