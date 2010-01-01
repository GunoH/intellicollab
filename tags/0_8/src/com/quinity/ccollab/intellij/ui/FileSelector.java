package com.quinity.ccollab.intellij.ui;

import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vcs.FilePath;
import com.quinity.ccollab.intellij.MessageResources;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class FileSelector extends JDialog implements CheckBoxListListener {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel buttonPane;
    private JPanel headerPane;
    private JPanel checkboxListPane;
    private CheckBoxList myCheckBoxList;

    private List<Pair<FilePath, Boolean>> myInitialList;
    private List<Pair<FilePath, Boolean>> myWorkingList;
    private DefaultListModel myListModel;

    private boolean okPressed;
    
    public FileSelector(List<Pair<FilePath, Boolean>> list) {
        myInitialList = list;
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
        myListModel = new DefaultListModel();
        myCheckBoxList = new CheckBoxList(myListModel, this);
    }

    public void reset() {
        myWorkingList = new ArrayList<Pair<FilePath, Boolean>>(myInitialList);
        update();
    }

    public void update() {
        myListModel.clear();
        for (Pair<FilePath, Boolean> pair : myWorkingList) {
            myListModel.addElement(createCheckBox(pair.first, pair.second.booleanValue()));
        }
    }

    public JCheckBox createCheckBox(FilePath path, boolean checked) {
        return new JCheckBox(FileUtil.toSystemDependentName(path.getPath()), checked);
    }

    public static void main(String[] args) {

        List<Pair<FilePath, Boolean>> l = new ArrayList<Pair<FilePath, Boolean>>();
//        l.add(Pair.create("aap", Boolean.TRUE));
//        l.add(Pair.create("noot", Boolean.FALSE));
//        l.add(Pair.create("mies", Boolean.TRUE));
//        l.add(Pair.create("vuur", Boolean.TRUE));
//        l.add(Pair.create("wim", Boolean.TRUE));
//        l.add(Pair.create("kees", Boolean.FALSE));

        FileSelector dialog = new FileSelector(l);

        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public void checkBoxSelectionChanged(int index, boolean value) {
        final Pair<FilePath, Boolean> pair = myWorkingList.remove(index);
        myWorkingList.add(index, Pair.create(pair.first, Boolean.valueOf(value)));
    }

    public FilePath[] retrieveSelectedFiles() {
        List<FilePath> result = new ArrayList<FilePath>();
        for (Pair<FilePath, Boolean> pair : myWorkingList) {
            if (pair.second) {
                result.add(pair.first);
            }
        }
        
        return result.toArray(new FilePath[result.size()]);
    }

    public boolean isOkPressed() {
        return okPressed;
    }
}
