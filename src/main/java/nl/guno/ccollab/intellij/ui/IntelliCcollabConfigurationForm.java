package nl.guno.ccollab.intellij.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import javax.swing.*;

import org.apache.commons.lang.StringUtils;

import nl.guno.ccollab.intellij.IntelliCcollabApplicationComponent;
import nl.guno.ccollab.intellij.MessageResources;

public class IntelliCcollabConfigurationForm {
    private JPanel rootComponent;
    private JTextField urlField;
    private JTextField proxyPortField;
    private JTextField proxyHostField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    
    /**
     * Method return root component of form.
     */
    public JComponent getRootComponent() {
        return rootComponent;
    }

    public void setData(IntelliCcollabApplicationComponent data) {
        String serverUrlFromConfig = data.getServerURL();

        if (StringUtils.isNotEmpty(serverUrlFromConfig)) {
            urlField.setText(serverUrlFromConfig);
        } else {
            urlField.setText(MessageResources.message("configuration.serverURL.default"));
        }
        proxyHostField.setText(data.getServerProxyHost());
        proxyPortField.setText(data.getServerProxyPort());
        usernameField.setText(data.getUsername());
        passwordField.setText(data.getPassword());
    }

    public void getData(IntelliCcollabApplicationComponent data) throws MalformedURLException {
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

        data.setServerProxyHost(proxyHostField.getText());
        data.setServerProxyPort(proxyPortField.getText());
        data.setUsername(usernameField.getText());
        data.setPassword(String.valueOf(passwordField.getPassword()));
    }

    public boolean isModified(IntelliCcollabApplicationComponent data) {

        if (data.getServerURL() == null) {
            return urlField.getText() != null;
        }
        if (data.getServerProxyHost() == null) {
            return proxyHostField.getText() != null;
        }
        if (data.getServerProxyPort() == null) {
            return proxyPortField.getText() != null;
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
        if (proxyHostField.getText() == null) {
            return false;
        }
        if (proxyPortField.getText() == null) {
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
        if (!proxyHostField.getText().equals(data.getServerProxyHost())) {
            return true;
        }
        if (!proxyPortField.getText().equals(data.getServerProxyPort())) {
            return true;
        }
        if (!usernameField.getText().equals(data.getUsername())) {
            return true;
        }
        return !Arrays.equals(passwordField.getPassword(), data.getPassword().toCharArray());
    }
}
