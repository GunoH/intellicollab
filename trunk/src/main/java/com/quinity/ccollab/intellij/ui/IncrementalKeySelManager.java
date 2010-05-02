package com.quinity.ccollab.intellij.ui;

import com.smartbear.ccollab.datamodel.User;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import java.util.Date;

class IncrementalKeySelManager implements JComboBox.KeySelectionManager {
 
	private long lastKeyPressTime = new Date().getTime();
	private StringBuffer searchString = new StringBuffer("");
 
	public int selectionForKey(char aKey, ComboBoxModel aModel) {
		try {
			long actKeyPressTime = new Date().getTime();
	
			if (lastKeyPressTime + 1000 < actKeyPressTime) {
				searchString = new StringBuffer("");
			}
	
			if (aKey >= 33 && aKey <= 127) {
				searchString = searchString.append(aKey);
			}
	
			lastKeyPressTime = actKeyPressTime;
	
			for (int i = 0; i < aModel.getSize(); i++) {
				User user = (User) aModel.getElementAt(i);
				if (user == null) {
					continue;
				}
				String listItem = user.getDisplayName().toUpperCase();
	
				if (listItem.startsWith(searchString.toString().toUpperCase())) {
					return i;
				}
			}
			return -1;
		} catch (Exception ex) {
			return -1;
		}
	}
}
