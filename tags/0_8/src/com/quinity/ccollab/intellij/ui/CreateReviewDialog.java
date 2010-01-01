package com.quinity.ccollab.intellij.ui;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyEvent;
import java.awt.*;
import java.util.ResourceBundle;

import javax.swing.*;

import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.Spacer;

public class CreateReviewDialog extends JDialog {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JComboBox selectTheReviewCreatorComboBox;
	private JPanel buttonPane;
	private JPanel creatorPane;
	private JPanel reviewerPane;
	private JComboBox selectTheReviewerComboBox;
	private JPanel reviewNamePane;
	private JTextField reviewNameTextField;
	private JPanel headerPane;
    private JPanel projectPane;
    private JComboBox projectComboBox;

    public CreateReviewDialog() {
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);

		buttonOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});

		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});

		// call onCancel() when cross is clicked
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});

		// call onCancel() on ESCAPE
		contentPane.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

	private void onOK() {
// add your code here
		dispose();
	}

	private void onCancel() {
// add your code here if necessary
		dispose();
	}

	public static void main(String[] args) {
		CreateReviewDialog dialog = new CreateReviewDialog();
		dialog.pack();
		dialog.setVisible(true);
		System.exit(0);
	}

}
