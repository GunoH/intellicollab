package nl.guno.collab.intellij.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import javax.swing.*;

import org.apache.commons.lang.StringUtils;

import nl.guno.ccollab.intellij.IntelliCcollabSettings;
import nl.guno.ccollab.intellij.MessageResources;

public class IntelliCollabConfigurationForm {
    private JPanel rootComponent;
    private JTextField urlField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    
    public JComponent getRootComponent() {
        return rootComponent;
    }

    public void setData(IntelliCcollabSettings data) {
        String serverUrlFromConfig = data.getServerURL();

        if (StringUtils.isNotEmpty(serverUrlFromConfig)) {
            urlField.setText(serverUrlFromConfig);
        } else {
            urlField.setText(MessageResources.message("configuration.serverURL.default"));
        }
        usernameField.setText(data.getUsername());
        passwordField.setText(data.getPassword());
    }

    public void getData(IntelliCcollabSettings data) throws MalformedURLException {
        String urlText = urlField.getText();
        if (StringUtils.isNotEmpty(urlText)) {
            // Validate the URL.
            new URL(urlText);
            data.setServerURL(urlText);
        } else {
            data.setServerURL(null);
        }
        urlText = urlField.getText();
        if (StringUtils.isNotEmpty(urlText)) {
            // Validate the URL.
            new URL(urlText);
            data.setServerURL(urlText);
        } else {
            data.setServerURL(null);
        }

        data.setUsername(usernameField.getText());
        data.setPassword(String.valueOf(passwordField.getPassword()));
    }

    public boolean isModified(IntelliCcollabSettings data) {

        if (data.getServerURL() == null) {
            return urlField.getText() != null;
        }
        if (data.getUsername() == null) {
            return usernameField.getText() != null;
        }
        if (data.getPassword() == null) {
            return passwordField.getPassword() != null;
        }

        if (urlField.getText() == null) {
            return false;
        }
        if (usernameField.getText() == null) {
            return false;
        }
        if (passwordField.getPassword() == null) {
            return false;
        }

        if (!urlField.getText().equals(data.getServerURL())) {
            return true;
        }
        if (!usernameField.getText().equals(data.getUsername())) {
            return true;
        }
        return !Arrays.equals(passwordField.getPassword(), data.getPassword().toCharArray());
    }
}
