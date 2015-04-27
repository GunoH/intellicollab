package nl.guno.ccollab.intellij.ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.swing.*;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.ListCellRendererWrapper;
import com.smartbear.ccollab.datamodel.GroupDescription;
import com.smartbear.ccollab.datamodel.IDropDownItem;
import com.smartbear.ccollab.datamodel.ReviewAccess;
import com.smartbear.ccollab.datamodel.User;
import nl.guno.ccollab.intellij.FetchMetadataTask;
import nl.guno.ccollab.intellij.MessageResources;

public class CreateReviewDialog extends DialogWrapper {
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

    public CreateReviewDialog(FetchMetadataTask fetchMetadataTask, User[] userList, List<GroupDescription> groupList,
                              User currentUser, Project project, String reviewTitle) {

        super(project);

        init();

        IDropDownItem[] bugzillaInstantieList = fetchMetadataTask.getBugzillaInstantie().getDropDownItems(true);

        this.userList = new ArrayList<User>();
        this.userList.addAll(Arrays.asList(userList));

        this.bugzillaInstantieList = new ArrayList<IDropDownItem>();
        this.bugzillaInstantieList.addAll(Arrays.asList(bugzillaInstantieList));

        this.groupList = groupList;

        fillDropDowns();

        authorComboBoxModel.setSelectedItem(currentUser);

        prepareUI(fetchMetadataTask);

        titleTextField.setText(reviewTitle);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return groupComboBox;
    }

    private void prepareUI(FetchMetadataTask fetchMetadataTask) {
        setTitle(MessageResources.message("dialog.createReview.title"));

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

        setTabBehaviorOnTextAreas();
    }

    /** Makes sure tab and shift-tab move the focus instead of inserting tab characters. */
    private void setTabBehaviorOnTextAreas() {
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

    private void createUIComponents() {

        authorComboBoxModel = new DefaultComboBoxModel();
        authorComboBox = new ComboBox(authorComboBoxModel);
        authorComboBox.setRenderer(new MyListCellRenderer<User>());
        authorComboBox.setKeySelectionManager(new MyKeySelManager());

        reviewerComboBoxModel = new DefaultComboBoxModel();
        reviewerComboBox = new ComboBox(reviewerComboBoxModel);
        reviewerComboBox.setRenderer(new MyListCellRenderer<User>());
        reviewerComboBox.setKeySelectionManager(new MyKeySelManager());

        observerComboBoxModel = new DefaultComboBoxModel();
        observerComboBox = new ComboBox(observerComboBoxModel);
        observerComboBox.setRenderer(new MyListCellRenderer<User>());
        observerComboBox.setKeySelectionManager(new MyKeySelManager());

        groupComboBoxModel = new DefaultComboBoxModel();
        groupComboBox = new ComboBox(groupComboBoxModel);
        groupComboBox.setRenderer(new MyListCellRenderer<GroupDescription>());
        groupComboBox.setKeySelectionManager(new MyKeySelManager());

        bugzillaInstantieComboBoxModel = new DefaultComboBoxModel();
        bugzillaInstantieComboBox = new ComboBox(bugzillaInstantieComboBoxModel);
        bugzillaInstantieComboBox.setRenderer(new MyListCellRenderer<IDropDownItem>());
        bugzillaInstantieComboBox.setKeySelectionManager(new MyKeySelManager());

        reviewAccessComboBoxModel = new DefaultComboBoxModel();
        reviewAccessComboBox = new ComboBox(reviewAccessComboBoxModel);
        reviewAccessComboBox.setRenderer(new MyListCellRenderer<ReviewAccess>());
        reviewAccessComboBox.setKeySelectionManager(new MyKeySelManager());
    }

    private void fillDropDowns() {

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

    @Nullable
    @Override
    protected ValidationInfo doValidate() {

        if (groupComboBoxModel.getSelectedItem() == null) {
            return new ValidationInfo("Field is mandatory", groupComboBox);
        }

        if (StringUtils.isEmpty(titleTextField.getText())) {
            return new ValidationInfo("Field is mandatory", titleTextField);
        }

        if (authorComboBoxModel.getSelectedItem() == null) {
            return new ValidationInfo("Field is mandatory", authorComboBox);
        }

        if (reviewerComboBoxModel.getSelectedItem() == null) {
            return new ValidationInfo("Field is mandatory", reviewerComboBox);
        }

        return null;
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
        return "yes".equalsIgnoreCase((String) restrictUploadsToReviewComboBox.getSelectedItem());
    }

    public ReviewAccess getReviewAccess() {
        return (ReviewAccess) reviewAccessComboBoxModel.getSelectedItem();
    }

    private class MyListCellRenderer<T extends IDropDownItem> extends ListCellRendererWrapper<T> {

        @Override
        public void customize(JList list, T item, int index, boolean isSelected, boolean hasFocus) {
            if (isSelected) {
                //noinspection UseJBColor
                setBackground(Color.BLUE);
            } else {
                setBackground(list.getBackground());
            }

            String name = "";
            if (item != null) {
                name = item.getDisplayName();
            }
            setText(name);
        }
    }

    private class MyKeySelManager extends IncrementalKeySelManager {
        @Override
        protected String getDisplayedText(Object object) {
            return ((IDropDownItem)object).getDisplayName().toUpperCase();
        }
    }

}
