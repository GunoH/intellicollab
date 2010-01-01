/*******************************************************************
 $Id$

 U mag de onderstaande melding niet verwijderen of wijzigen.
 You are not allowed to delete or change the statement below.

 Copyright (c) Quinity B.V. 2001 - 2009

 Intellectueel eigendom van Quinity B.V.
 Intellectual property of Quinity B.V.

 Deze documentatie en de programmatuur die door deze documentatie beschreven wordt mogen niet worden gebruikt door 
 andere applicaties dan waarvoor Quinity voorafgaand schriftelijk toestemming heeft gegeven. Deze documentatie en de
 programmatuur die door deze documentatie beschreven wordt mogen niet worden gebruikt door andere organisaties dan 
 waarvoor Quinity voorafgaand schriftelijk toestemming heeft gegeven. Deze documentatie en de programmatuur die door 
 deze documentatie beschreven wordt mogen niet openbaar worden gemaakt door middel van druk, fotokopie, microfilm of 
 op welke andere wijze ook, zonder voorafgaande schriftelijke toestemming van Quinity B.V.

 This documentation and the software / source code that is described by this documentation may not be used by 
 applications without prior written permission by Quinity B.V. This documentation and the software / source code
 that is described by this documentation may not be used by organisations without written prior permission by 
 Quinity B.V. This documentation and the software / source code that is described by this documentation may not be made 
 public by means of print, (photo)copy, microfilm, or any other method without prior written permission by Quinity B.V.

 -------------- CHANGE HISTORY ----------------------

 *****************************************************************************************************/
package com.quinity.ccollab.intellij.ui;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Insets;
import java.util.ResourceBundle;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.GridConstraints;

public class OptionSelectorNoForm /*extends JDialog */ {
	private CheckBoxList checkBoxList1;
	private JPanel buttons;
	private JPanel label;
	private JButton okButton;
	private JButton cancelButton;
	private JScrollPane myScrollPane;

	private ResourceBundle bundle = ResourceBundle.getBundle("com/quinity/ccollab/intellij/ui/IntelliCCollab");

	public OptionSelectorNoForm() {
		setupUI();

//		addKeyListener(new KeyAdapter() {
//			@Override public void keyTyped(KeyEvent ke) {
//				if (ke.getID() == KeyEvent.VK_ESCAPE) {
//					onCancel();
//				}
//			}
//		});
	}

	private void createUIComponents() {
		// TODO: place custom component creation code here 
	}

	private void onOK() {
//		dispose();
	}

	private void onCancel() {
//		dispose();
	}

	private void setupUI() {
		createUIComponents();

		final JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
		label = new JPanel();
		label.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		panel1.add(label, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
		final JLabel label = new JLabel();
		loadLabelText(label, bundle.getString("label.chooseFiles"));
		label.add(label, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		myScrollPane = new JScrollPane();
		panel1.add(myScrollPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		myScrollPane.setViewportView(checkBoxList1);
		buttons = new JPanel();
		buttons.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
		panel1.add(buttons, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
		okButton = new JButton();
		loadButtonText(okButton, bundle.getString("button.ok"));
		okButton.setToolTipText(bundle.getString("button.ok.information"));
		buttons.add(okButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		cancelButton = new JButton();
		loadButtonText(cancelButton, bundle.getString("button.cancel"));
		cancelButton.setToolTipText(bundle.getString("button.cancel.information"));
		buttons.add(cancelButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));


		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOK();

			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});

	}

	private void loadLabelText(JLabel component, String text) {
		StringBuilder result = new StringBuilder();
		boolean haveMnemonic = false;
		char mnemonic = '\0';
		int mnemonicIndex = -1;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '&') {
				i++;
				if (i == text.length()) break;
				if (!haveMnemonic && text.charAt(i) != '&') {
					haveMnemonic = true;
					mnemonic = text.charAt(i);
					mnemonicIndex = result.length();
				}
			}
			result.append(text.charAt(i));
		}
		component.setText(result.toString());
		if (haveMnemonic) {
			component.setDisplayedMnemonic(mnemonic);
			component.setDisplayedMnemonicIndex(mnemonicIndex);
		}
	}

	private void loadButtonText(AbstractButton component, String text) {
		StringBuilder result = new StringBuilder();
		boolean haveMnemonic = false;
		char mnemonic = '\0';
		int mnemonicIndex = -1;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '&') {
				i++;
				if (i == text.length()) break;
				if (!haveMnemonic && text.charAt(i) != '&') {
					haveMnemonic = true;
					mnemonic = text.charAt(i);
					mnemonicIndex = result.length();
				}
			}
			result.append(text.charAt(i));
		}
		component.setText(result.toString());
		if (haveMnemonic) {
			component.setMnemonic(mnemonic);
			component.setDisplayedMnemonicIndex(mnemonicIndex);
		}
	}
}
