package com.quinity.ccollab.intellij.ui;

import com.quinity.ccollab.intellij.IntelliCcollabApplicationComponent;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
		urlField.setText(data.getServerURL());
	}

	public void getData(IntelliCcollabApplicationComponent data) {
		data.setServerURL(urlField.getText());
	}

	public boolean isModified(IntelliCcollabApplicationComponent data) {
		
		if (data.getServerURL() == null) {
			return urlField.getText() != null;
		}
		
		if (urlField.getText() == null) {
			return false;
		}
		
		return !urlField.getText().equals(data.getServerURL());
	}
}
