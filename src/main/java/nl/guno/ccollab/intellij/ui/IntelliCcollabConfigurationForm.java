package nl.guno.ccollab.intellij.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.Messages;
import com.smartbear.CollabClientException;
import com.smartbear.beans.ConfigUtils;
import com.smartbear.beans.IGlobalOptions;
import com.smartbear.beans.IScmOptions;
import com.smartbear.collections.Pair;
import nl.guno.ccollab.intellij.IntelliCcollabApplicationComponent;
import nl.guno.ccollab.intellij.MessageResources;

public class IntelliCcollabConfigurationForm {
    private JPanel rootComponent;
    private JTextField httpUrlField;
    private JTextField regularUrlField;
    private JTextField proxyPortField;
    private JTextField proxyHostField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton autofillButton;

    private static final Logger logger = Logger.getInstance(IntelliCcollabConfigurationForm.class.getName());

    public IntelliCcollabConfigurationForm() {
        autofillButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    autofill();
                } catch (Exception ex) {
                    logger.error("Exception when reading metadata from filesystem. ", ex);
                    Messages.showErrorDialog(MessageResources.message("configuration.error.couldNotReadMetadata.text"),
                            MessageResources.message("configuration.error.couldNotReadMetadata.title"));
                }
            }
        });
    }

    /**
     * Fills the preferences with the values retrieved from the filesystem metadata.
     */
    private void autofill() throws IOException, CollabClientException {
        Pair<IGlobalOptions, IScmOptions> configOptions = ConfigUtils.loadConfigFiles();
        IGlobalOptions options = configOptions.getA();

        httpUrlField.setText(options.getUrl().toString());
        regularUrlField.setText(options.getUrl().toString());
        proxyHostField.setText(options.getServerProxyHost());
        proxyPortField.setText(options.getServerProxyPort());
        usernameField.setText(options.getUser());
        passwordField.setText(options.getPassword());
    }


    /**
     * Method return root component of form.
     */
    public JComponent getRootComponent() {
        return rootComponent;
    }

    public void setData(IntelliCcollabApplicationComponent data) {
        String regularServerUrlFromConfig = data.getRegularServerURL();
        String httpServerUrlFromConfig = data.getHttpServerURL();

        if (StringUtils.isNotEmpty(regularServerUrlFromConfig)) {
            regularUrlField.setText(regularServerUrlFromConfig);
        } else {
            regularUrlField.setText(MessageResources.message("configuration.serverURL.default"));
        }
        if (StringUtils.isNotEmpty(httpServerUrlFromConfig)) {
            httpUrlField.setText(httpServerUrlFromConfig);
        } else {
            httpUrlField.setText(MessageResources.message("configuration.serverURL.http.default"));
        }
        proxyHostField.setText(data.getServerProxyHost());
        proxyPortField.setText(data.getServerProxyPort());
        usernameField.setText(data.getUsername());
        passwordField.setText(data.getPassword());
    }

    public void getData(IntelliCcollabApplicationComponent data) throws MalformedURLException {
        String urlText = regularUrlField.getText();
        if (StringUtils.isNotEmpty(urlText)) {
            // Validate the URL.
            new URL(urlText);
            data.setRegularServerURL(urlText);
        } else {
            data.setRegularServerURL(null);
        }
        urlText = httpUrlField.getText();
        if (StringUtils.isNotEmpty(urlText)) {
            // Validate the URL.
            new URL(urlText);
            data.setHttpServerURL(urlText);
        } else {
            data.setHttpServerURL(null);
        }

        data.setServerProxyHost(proxyHostField.getText());
        data.setServerProxyPort(proxyPortField.getText());
        data.setUsername(usernameField.getText());
        data.setPassword(String.valueOf(passwordField.getPassword()));
    }

    public boolean isModified(IntelliCcollabApplicationComponent data) {

        if (data.getRegularServerURL() == null) {
            return regularUrlField.getText() != null;
        }
        if (data.getHttpServerURL() == null) {
            return httpUrlField.getText() != null;
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

        if (regularUrlField.getText() == null) {
            return false;
        }
        if (httpUrlField.getText() == null) {
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

        if (!regularUrlField.getText().equals(data.getRegularServerURL())) {
            return true;
        }
        if (!httpUrlField.getText().equals(data.getHttpServerURL())) {
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
