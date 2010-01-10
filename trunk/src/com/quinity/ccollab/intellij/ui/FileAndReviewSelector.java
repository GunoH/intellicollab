package com.quinity.ccollab.intellij.ui;

import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.ui.ComboBox;
import com.quinity.ccollab.intellij.MessageResources;
import com.smartbear.ccollab.datamodel.Review;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class FileAndReviewSelector extends JDialog implements CheckBoxListListener {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel buttonPane;
    private JPanel headerPane;
    private JPanel checkboxListPane;
    private CheckBoxList fileCheckBoxList;
	private JPanel reviewPane;
	private JComboBox reviewComboBox;

	private List<Pair<FilePath, Boolean>> initialFileList;
	private List<Pair<FilePath, Boolean>> workingFileList;
	private DefaultListModel fileListModel;

	private List<Review> reviewList;
	private DefaultComboBoxModel reviewComboBoxModel;

	private boolean okPressed;
    
    public FileAndReviewSelector(List<Pair<FilePath, Boolean>> fileList, Review[] reviewList) {
        initialFileList = fileList;
		
		this.reviewList = new ArrayList<Review>();
		this.reviewList.addAll(Arrays.asList(reviewList));
		
		reset();

        prepareUI();
    }

    private void prepareUI() {
        setTitle(MessageResources.message("dialog.selectFilesForReview.title"));
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
            @Override
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
        okPressed = true;
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    private void createUIComponents() {
        fileListModel = new DefaultListModel();
        fileCheckBoxList = new CheckBoxList(fileListModel, this);
		
		reviewComboBoxModel = new DefaultComboBoxModel();
		reviewComboBox = new ComboBox(reviewComboBoxModel, -1);
		reviewComboBox.setRenderer(new ReviewComboboxRenderer());
	}

    public void reset() {
        workingFileList = new ArrayList<Pair<FilePath, Boolean>>(initialFileList);
        update();
    }

    public void update() {
        fileListModel.clear();
        for (Pair<FilePath, Boolean> pair : workingFileList) {
            fileListModel.addElement(createCheckBox(pair.first, pair.second.booleanValue()));
        }

		reviewComboBoxModel.removeAllElements();
		for (Review review : reviewList) {
			reviewComboBoxModel.addElement(review);
		}
	}

    public JCheckBox createCheckBox(FilePath path, boolean checked) {
        return new JCheckBox(FileUtil.toSystemDependentName(path.getPath()), checked);
    }

    public static void main(String[] args) {

        List<Pair<FilePath, Boolean>> fileList = new ArrayList<Pair<FilePath, Boolean>>();
        Review[] reviews = new Review[0];
		
		FileAndReviewSelector dialog = new FileAndReviewSelector(fileList, reviews);

        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public void checkBoxSelectionChanged(int index, boolean value) {
        final Pair<FilePath, Boolean> pair = workingFileList.remove(index);
        workingFileList.add(index, Pair.create(pair.first, Boolean.valueOf(value)));
    }

    public FilePath[] retrieveSelectedFiles() {
        List<FilePath> result = new ArrayList<FilePath>();
        for (Pair<FilePath, Boolean> pair : workingFileList) {
            if (pair.second.booleanValue()) {
                result.add(pair.first);
            }
        }
        
        return result.toArray(new FilePath[result.size()]);
    }

    public boolean isOkPressed() {
        return okPressed;
    }
	
	public Integer getSelectedReviewId() {
		return ((Review)reviewComboBox.getSelectedItem()).getId();
	}
}
