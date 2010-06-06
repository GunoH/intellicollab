package com.quinity.ccollab.intellij.ui;

import com.quinity.ccollab.intellij.IntelliCcollabApplicationComponent;
import org.apache.commons.lang.StringUtils;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class IntelliCcollabConfigurationForm {
	private JPanel rootComponent;
	private JTextField urlField;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JLabel urlLabel;
	private JLabel usernameLabel;
	private JLabel passwordLabel;

	/**
	 * Method return root component of form.
	 */
	public JComponent getRootComponent() {
		return rootComponent;
	}

	public void setData(IntelliCcollabApplicationComponent data) {
		urlField.setText(data.getServerURL());
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
		
		data.setUsername(usernameField.getText());
		data.setPassword(String.valueOf(passwordField.getPassword()));
	}

	public boolean isModified(IntelliCcollabApplicationComponent data) {
		
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
