package nl.guno.ccollab.intellij.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.swing.*;

import org.apache.commons.lang.StringUtils;

import com.smartbear.ccollab.datamodel.GroupDescription;
import com.smartbear.ccollab.datamodel.IDropDownItem;
import com.smartbear.ccollab.datamodel.MetaDataSelectItem;
import com.smartbear.ccollab.datamodel.ReviewAccess;
import com.smartbear.ccollab.datamodel.User;
import nl.guno.ccollab.intellij.FetchMetadataTask;
import nl.guno.ccollab.intellij.MessageResources;

public class CreateReviewDialog extends JDialog {
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox authorComboBox;
    private JComboBox reviewerComboBox;
    private JComboBox observerComboBox;
    private JComboBox groupComboBox;
    private JComboBox bugzillaInstantieComboBox;
    private JComboBox reviewAccessComboBox;
    private JComboBox restrictUploadsToReviewComboBox;
    private JTextField titleTextField;
    private JTextArea overviewTextArea;
    private JTextField bugzillaNummerTextField;
    private JTextField foTextField;
    private JTextField toTextField;
    private JPanel contentPane;
    private JPanel buttonPane;
    private JPanel authorPane;
    private JPanel reviewerPane;
    private JPanel titlePane;
    private JPanel headerPane;
    private JPanel projectPane;
    private JPanel overviewPane;
    private JPanel basicInfoPane;
    private JPanel bugzillaPane;
    private JPanel participantsPane;
    private JPanel observerPane;
    private JPanel restrictionPane;
    private JPanel FOPane;
    private JPanel TOPane;
    private JPanel rnFOPane;
    private JPanel rnMigratiePadPane;
    private JPanel rnTOPane;
    private JTextArea rnFOTextArea;
    private JTextArea rnTOTextArea;
    private JTextArea rnMigratiePadTextArea;

    /**
     * De default achtergrondkleur van een combobox; deze bewaren we zodat we na het tonen van een eventuele foutmelding de
     * achtergrondkleur kunnen restoren.
     */
    private Color defaultComboboxBackground;

    /**
     * De default achtergrondkleur van een textfield; deze bewaren we zodat we na het tonen van een eventuele foutmelding de
     * achtergrondkleur kunnen restoren.
     */
    private Color defaultTextFieldBackground;

    /**
     * De achtergrondkleur voor niet-validerende velden.
     */
    private final Color highlightBackground = Color.yellow;
    
    private DefaultComboBoxModel reviewerComboBoxModel;
    private DefaultComboBoxModel authorComboBoxModel;
    private DefaultComboBoxModel observerComboBoxModel;
    private DefaultComboBoxModel groupComboBoxModel;
    private DefaultComboBoxModel bugzillaInstantieComboBoxModel;
    private DefaultComboBoxModel reviewAccessComboBoxModel;

    /**
     * Lijst met gebruikers waar uit gekozen kan worden in de userinterface.
     */
    private final List<User> userList;

    /**
     * Lijst met bugzillaInstanties waar uit gekozen kan worden in de userinterface.
     */
    private final List<IDropDownItem> bugzillaInstantieList;

    /**
     * Lijst met groepen waar uit gekozen kan worden in de userinterface.
     */
    private final List<GroupDescription> groupList;

    /**
     * Geeft aan of de gebruiker op de OK knop heeft gedrukt of niet.
     */
    private boolean okPressed;

    /** Maximale lengte van het 'Overview' veld. */
    private static final int MAXLENGTH_OVERVIEW = 4000;

    /** Maximale lengte van het 'Title' veld. */
    private static final int MAXLENGTH_TITLE = 255;

    /** Maximale lengte van het 'Bugzillanummer' veld. */
    private static final int MAXLENGTH_BUGZILLANUMMER = 255;

    /** Maximale lengte van het 'FO' veld. */
    private static final int MAXLENGTH_FO = 255;

    /** Maximale lengte van het 'TO' veld. */
    private static final int MAXLENGTH_TO = 255;

    /** Maximale lengte van het 'Relese notes: FO' veld. */
    private static final int MAXLENGTH_RNFO = 4000;

    /** Maximale lengte van het 'Release notes: TO' veld. */
    private static final int MAXLENGTH_RNTO = 4000;

    /** Maximale lengte van het 'Release notes: Migratiepad' veld. */
    private static final int MAXLENGTH_RNMIGRATIEPAD = 4000;

    public CreateReviewDialog(FetchMetadataTask fetchMetadataTask, User[] userList, List<GroupDescription> groupList, User currentUser) {

        IDropDownItem[] bugzillaInstantieList = fetchMetadataTask.getBugzillaInstantie().getDropDownItems(true);
        
        this.userList = new ArrayList<User>();
        this.userList.addAll(Arrays.asList(userList));

        this.bugzillaInstantieList = new ArrayList<IDropDownItem>();
        this.bugzillaInstantieList.addAll(Arrays.asList(bugzillaInstantieList));

        this.groupList = groupList;

        update();

        authorComboBoxModel.setSelectedItem(currentUser);

        prepareUI(fetchMetadataTask);
    }

    private void prepareUI(FetchMetadataTask fetchMetadataTask) {
        setTitle(MessageResources.message("dialog.createReview.title"));
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

        UIDefaults defaults = UIManager.getDefaults();
        defaultComboboxBackground = defaults.getColor("Combobox.background");
        defaultTextFieldBackground = defaults.getColor("TextField.background");

        // Set tooltip texts
        setToolTipText(overviewTextArea, fetchMetadataTask.getOverview().getDescription());
        setToolTipText(bugzillaInstantieComboBox, fetchMetadataTask.getBugzillaInstantie().getDescription());
        setToolTipText(bugzillaNummerTextField, fetchMetadataTask.getBugzillanummer().getDescription());
        setToolTipText(foTextField, fetchMetadataTask.getFO().getDescription());
        setToolTipText(toTextField, fetchMetadataTask.getTO().getDescription());
        setToolTipText(rnFOTextArea, fetchMetadataTask.getRNFO().getDescription());
        setToolTipText(rnTOTextArea, fetchMetadataTask.getRNTO().getDescription());
        setToolTipText(rnMigratiePadTextArea, fetchMetadataTask.getRNMigratiePad().getDescription());
        
        // Set max length on JTextComponents.
        titleTextField.setDocument(new InputLimiterDocument(MAXLENGTH_TITLE));
        overviewTextArea.setDocument(new InputLimiterDocument(MAXLENGTH_OVERVIEW));
        bugzillaNummerTextField.setDocument(new InputLimiterDocument(MAXLENGTH_BUGZILLANUMMER));
        foTextField.setDocument(new InputLimiterDocument(MAXLENGTH_FO));
        toTextField.setDocument(new InputLimiterDocument(MAXLENGTH_TO));
        rnFOTextArea.setDocument(new InputLimiterDocument(MAXLENGTH_RNFO));
        rnTOTextArea.setDocument(new InputLimiterDocument(MAXLENGTH_RNTO));
        rnMigratiePadTextArea.setDocument(new InputLimiterDocument(MAXLENGTH_RNMIGRATIEPAD));

        // Make sure tab and shift-tab move the focus instead of inserting tab characters.
        overviewTextArea.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                new HashSet<KeyStroke>(Arrays.asList(KeyStroke.getKeyStroke("pressed TAB"))));
        overviewTextArea.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
                new HashSet<KeyStroke>(Arrays.asList(KeyStroke.getKeyStroke("shift pressed TAB"))));
        rnFOTextArea.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                new HashSet<KeyStroke>(Arrays.asList(KeyStroke.getKeyStroke("pressed TAB"))));
        rnFOTextArea.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
                new HashSet<KeyStroke>(Arrays.asList(KeyStroke.getKeyStroke("shift pressed TAB"))));
        rnTOTextArea.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                new HashSet<KeyStroke>(Arrays.asList(KeyStroke.getKeyStroke("pressed TAB"))));
        rnTOTextArea.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
                new HashSet<KeyStroke>(Arrays.asList(KeyStroke.getKeyStroke("shift pressed TAB"))));
        rnMigratiePadTextArea.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                new HashSet<KeyStroke>(Arrays.asList(KeyStroke.getKeyStroke("pressed TAB"))));
        rnMigratiePadTextArea.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
                new HashSet<KeyStroke>(Arrays.asList(KeyStroke.getKeyStroke("shift pressed TAB"))));

    }

    private void setToolTipText(JComponent component, String text) {
        if (text != null && text.length() > 0) {
            component.setToolTipText(text);
        }
    }

    @SuppressWarnings({"BoundFieldAssignment"})
    private void createUIComponents() {

        authorComboBoxModel = new DefaultComboBoxModel();
        authorComboBox = new JComboBox(authorComboBoxModel);
        authorComboBox.setRenderer(new UserComboboxRenderer());
        authorComboBox.setKeySelectionManager(new IncrementalKeySelManager() {
            @Override
            protected String getDisplayedText(Object object) {
                return ((User)object).getDisplayName().toUpperCase();
            }
        });

        reviewerComboBoxModel = new DefaultComboBoxModel();
        reviewerComboBox = new JComboBox(reviewerComboBoxModel);
        reviewerComboBox.setRenderer(new UserComboboxRenderer());
        reviewerComboBox.setKeySelectionManager(new IncrementalKeySelManager() {
            @Override
            protected String getDisplayedText(Object object) {
                return ((User)object).getDisplayName().toUpperCase();
            }
        });

        observerComboBoxModel = new DefaultComboBoxModel();
        observerComboBox = new JComboBox(observerComboBoxModel);
        observerComboBox.setRenderer(new UserComboboxRenderer());
        observerComboBox.setKeySelectionManager(new IncrementalKeySelManager() {
            @Override
            protected String getDisplayedText(Object object) {
                return ((User)object).getDisplayName().toUpperCase();
            }
        });

        groupComboBoxModel = new DefaultComboBoxModel();
        groupComboBox = new JComboBox(groupComboBoxModel);
        groupComboBox.setRenderer(new GroupDescriptionRenderer());
        groupComboBox.setKeySelectionManager(new IncrementalKeySelManager() {
            @Override
            protected String getDisplayedText(Object object) {
                return ((GroupDescription)object).getDisplayName().toUpperCase();
            }
        });

        bugzillaInstantieComboBoxModel = new DefaultComboBoxModel();
        bugzillaInstantieComboBox = new JComboBox(bugzillaInstantieComboBoxModel);
        bugzillaInstantieComboBox.setRenderer(new IDropDownItemComboboxRenderer());
        bugzillaInstantieComboBox.setKeySelectionManager(new IncrementalKeySelManager() {
            @Override
            protected String getDisplayedText(Object object) {
                return ((MetaDataSelectItem)object).getDisplayName().toUpperCase();
            }
        });

        reviewAccessComboBoxModel = new DefaultComboBoxModel();
        reviewAccessComboBox = new JComboBox(reviewAccessComboBoxModel);
        reviewAccessComboBox.setRenderer(new ReviewAccessComboboxRenderer());
        reviewAccessComboBox.setKeySelectionManager(new IncrementalKeySelManager() {
            @Override
            protected String getDisplayedText(Object object) {
                return ((ReviewAccess)object).getDisplayName().toUpperCase();
            }
        });
    }

    void update() {

        authorComboBoxModel.removeAllElements();
        reviewerComboBoxModel.removeAllElements();
        observerComboBoxModel.removeAllElements();
        groupComboBoxModel.removeAllElements();
        bugzillaInstantieComboBoxModel.removeAllElements();
        reviewAccessComboBoxModel.removeAllElements();

        // Set default to empty value
        reviewerComboBoxModel.addElement(null);
        observerComboBoxModel.addElement(null);
        groupComboBoxModel.addElement(null);

        for (User user : userList) {
            authorComboBoxModel.addElement(user);
            reviewerComboBoxModel.addElement(user);
            observerComboBoxModel.addElement(user);
        }

        for (IDropDownItem item : bugzillaInstantieList) {
            bugzillaInstantieComboBoxModel.addElement(item);
        }

        for (GroupDescription group : groupList) {
            groupComboBoxModel.addElement(group);
        }

        reviewAccessComboBoxModel.addElement(ReviewAccess.ANYONE);
        reviewAccessComboBoxModel.addElement(ReviewAccess.PARTICIPANTS);
    }

    private boolean validateUserInput() {
        boolean result = true;
        if (groupComboBoxModel.getSelectedItem() == null) {
            
            groupComboBox.setBackground(highlightBackground);

            if (result) {
                // This is the first error, so set focus to this field.
                groupComboBox.grabFocus();
            }
            result = false;
        } else {
            groupComboBox.setBackground(defaultComboboxBackground);
        }

        if (StringUtils.isEmpty(titleTextField.getText())) {
            titleTextField.setBackground(highlightBackground);
            if (result) {
                // This is the first error, so set focus to this field.
                titleTextField.grabFocus();
            }
            result = false;
        } else {
            titleTextField.setBackground(defaultTextFieldBackground);
        }

        if (authorComboBoxModel.getSelectedItem() == null) {
            authorComboBox.setBackground(highlightBackground);
            if (result) {
                // This is the first error, so set focus to this field.
                authorComboBox.grabFocus();
            }
            result = false;
        } else {
            authorComboBox.setBackground(defaultComboboxBackground);
        }

        if (reviewerComboBoxModel.getSelectedItem() == null) {
            reviewerComboBox.setBackground(highlightBackground);
            if (result) {
                // This is the first error, so set focus to this field.
                reviewerComboBox.grabFocus();
            }
            result = false;
        } else {
            reviewerComboBox.setBackground(defaultComboboxBackground);
        }

        return result;
    }

    private void onOK() {
        if (!validateUserInput()) {
            return;
        }

        okPressed = true;
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public GroupDescription getSelectedGroup() {
        return (GroupDescription) groupComboBoxModel.getSelectedItem();
    }

    public User getSelectedAuthor() {
        return (User) authorComboBoxModel.getSelectedItem();
    }

    public User getSelectedReviewer() {
        return (User) reviewerComboBoxModel.getSelectedItem();
    }

    public User getSelectedObserver() {
        return (User) observerComboBoxModel.getSelectedItem();
    }

    public IDropDownItem getSelectedBugzillaInstantie() {
        return (IDropDownItem) bugzillaInstantieComboBoxModel.getSelectedItem();
    }

    public String getEnteredTitle() {
        return titleTextField.getText();
    }

    public String getEnteredOverview() {
        return overviewTextArea.getText();
    }

    public String getEnteredBugzillanummer() {
        return bugzillaNummerTextField.getText();
    }

    public String getEnteredFO() {
        return foTextField.getText();
    }

    public String getEnteredTO() {
        return toTextField.getText();
    }

    public String getEnteredRNFO() {
        return rnFOTextArea.getText();
    }

    public String getEnteredRNTO() {
        return rnTOTextArea.getText();
    }

    public String getEnteredRNMigratiePad() {
        return rnMigratiePadTextArea.getText();
    }

    public boolean isUploadRestricted() {
        return "yes".equalsIgnoreCase((String)restrictUploadsToReviewComboBox.getSelectedItem());
    }

    public ReviewAccess getReviewAccess() {
        return (ReviewAccess) reviewAccessComboBoxModel.getSelectedItem();
    }

    public boolean isOkPressed() {
        return okPressed;
    }

    class UserComboboxRenderer extends JLabel implements ListCellRenderer {

        public UserComboboxRenderer() {
            setOpaque(true);
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, 
                                                      boolean cellHasFocus) {

            User user = (User) value;
            
            if (isSelected) {
                setBackground(Color.BLUE);
            } else {
                setBackground(list.getBackground());
            }

            String name = "";
            if (user != null) {
                name = user.getDisplayName();
            }
            setText(name);

            return this;
        }
    }

    class ReviewAccessComboboxRenderer extends JLabel implements ListCellRenderer {

        public ReviewAccessComboboxRenderer() {
            setOpaque(true);
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, 
                                                      boolean cellHasFocus) {
            
            ReviewAccess reviewAccess = (ReviewAccess) value;
            
            if (isSelected) {
                setBackground(Color.BLUE);
            } else {
                setBackground(list.getBackground());
            }

            String name = "";
            if (reviewAccess != null) {
                name = reviewAccess.getDisplayName();
            }
            setText(name);

            return this;
        }
    }

    class IDropDownItemComboboxRenderer extends JLabel implements ListCellRenderer {

        public IDropDownItemComboboxRenderer() {
            setOpaque(true);
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, 
                                                      boolean cellHasFocus) {
            
            IDropDownItem item = (IDropDownItem) value;

            if (isSelected) {
                setBackground(Color.BLUE);
            } else {
                setBackground(list.getBackground());
            }

            String name = "";
            if (item != null) {
                name = item.getDisplayName();
            }
            setText(name);

            return this;
        }
    }

    class GroupDescriptionRenderer extends JLabel implements ListCellRenderer {

        public GroupDescriptionRenderer() {
            setOpaque(true);
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, 
                                                      boolean cellHasFocus) {
            
            GroupDescription groupDescription = (GroupDescription) value;

            if (isSelected) {
                setBackground(Color.BLUE);
            } else {
                setBackground(list.getBackground());
            }

            String name = "";
            if (groupDescription != null) {
                name = groupDescription.getDisplayName();
            }
            setText(name);

            return this;
        }

    }
}
