package com.quinity.ccollab.intellij.ui;

import com.quinity.ccollab.intellij.IntelliCcollabApplicationComponent;
import org.apache.commons.lang.StringUtils;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.net.MalformedURLException;
import java.net.URL;

public class IntelliCcollabConfigurationForm {
	private JPanel rootComponent;
	private JTextField urlField;
	private JLabel urlLabel;

	/**
	 * Method return root component of form.
	 */
	public JComponent getRootComponent() {
		return rootComponent;
	}

	public void setData(IntelliCcollabApplicationComponent data) {
		URL serverURL = data.getServerURL();
		if (serverURL != null) {
			urlField.setText(serverURL.toString());
		}
	}

	public void getData(IntelliCcollabApplicationComponent data) throws MalformedURLException {
		String urlText = urlField.getText();
		if (StringUtils.isNotEmpty(urlText)) {
			data.setServerURL(new URL(urlText));
		} else {
			data.setServerURL(null);
		}
	}

	public boolean isModified(IntelliCcollabApplicationComponent data) {
		if (urlField.getText() != null && data.getServerURL() != null) {
			return !urlField.getText().equals(data.getServerURL().toString());
		} else {
			return urlField.getText() != null || data.getServerURL() != null;
		}
	}
}
