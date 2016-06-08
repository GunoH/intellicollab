package nl.guno.collab.intellij.ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;

import com.smartbear.ccollab.datamodel.client.IDropDownItem;
import com.smartbear.ccollab.datamodel.client.ReviewAccess;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.ListCellRendererWrapper;
import com.smartbear.ccollab.datamodel.GroupDescription;
import com.smartbear.ccollab.datamodel.User;
import nl.guno.collab.intellij.FetchMetadataTask;
import nl.guno.collab.intellij.MessageResources;

public class CreateReviewDialog extends DialogWrapper {

    private static final String PATTERN_BUGNUMBER = ".*[bB]ug[zZ]?i?l?l?a? #?([0-9]*).*";

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
     * Users available in user interface.
     */
    private final List<User> userList;

    /**
     * Bugzilla instances available in user interface.
     */
    private final List<IDropDownItem> bugzillaInstantieList;

    /**
     * Groups available in user interface.
     */
    private final List<GroupDescription> groupList;

    /** Max length of 'Overview' field. */
    private static final int MAXLENGTH_OVERVIEW = 4000;

    /** Max length of 'Title' field. */
    private static final int MAXLENGTH_TITLE = 255;

    /** Max length of 'Bugzillanummer' field. */
    private static final int MAXLENGTH_BUGZILLANUMMER = 255;

    /** Max length of 'FO' field. */
    private static final int MAXLENGTH_FO = 255;

    /** Max length of 'TO' field. */
    private static final int MAXLENGTH_TO = 255;

    /** Max length of 'Relese notes: FO' field. */
    private static final int MAXLENGTH_RNFO = 4000;

    /** Max length of 'Release notes: TO' field. */
    private static final int MAXLENGTH_RNTO = 4000;

    /** Max length of 'Release notes: Migratiepad' field. */
    private static final int MAXLENGTH_RNMIGRATIEPAD = 4000;

    public CreateReviewDialog(FetchMetadataTask fetchMetadataTask, List<User> userList, List<GroupDescription> groupList,
                              User currentUser, Project project, String reviewTitle) {

        super(project);

        init();

        List<? extends IDropDownItem> bugzillaInstantieList = fetchMetadataTask.getBugzillaInstantie().getDropDownItems(true);

        this.userList = new ArrayList<User>();
        this.userList.addAll(userList);

        this.bugzillaInstantieList = new ArrayList<IDropDownItem>();
        this.bugzillaInstantieList.addAll(bugzillaInstantieList);

        this.groupList = groupList;

        fillDropDowns();

        authorComboBoxModel.setSelectedItem(currentUser);

        prepareUI(fetchMetadataTask);

        titleTextField.setText(reviewTitle);


        Matcher matcher = Pattern.compile(PATTERN_BUGNUMBER).matcher(reviewTitle);
        if (matcher.matches()) {
            bugzillaNummerTextField.setText(matcher.group(1));
            // By default, select the first entry: Bugzilla
            bugzillaInstantieComboBoxModel.setSelectedItem(bugzillaInstantieList.get(0));
        }
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
        setToolTipText(bugzillaNummerTextField, fetchMetadataTask.getBugzillaNummer().getDescription());
        setToolTipText(foTextField, fetchMetadataTask.getFo().getDescription());
        setToolTipText(toTextField, fetchMetadataTask.getTo().getDescription());
        setToolTipText(rnFOTextArea, fetchMetadataTask.getRnfo().getDescription());
        setToolTipText(rnTOTextArea, fetchMetadataTask.getRnto().getDescription());
        setToolTipText(rnMigratiePadTextArea, fetchMetadataTask.getRnMigratiePad().getDescription());
        
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
                new HashSet<KeyStroke>(Collections.singletonList(KeyStroke.getKeyStroke("pressed TAB"))));
        overviewTextArea.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
                new HashSet<KeyStroke>(Collections.singletonList(KeyStroke.getKeyStroke("shift pressed TAB"))));
        rnFOTextArea.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                new HashSet<KeyStroke>(Collections.singletonList(KeyStroke.getKeyStroke("pressed TAB"))));
        rnFOTextArea.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
                new HashSet<KeyStroke>(Collections.singletonList(KeyStroke.getKeyStroke("shift pressed TAB"))));
        rnTOTextArea.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                new HashSet<KeyStroke>(Collections.singletonList(KeyStroke.getKeyStroke("pressed TAB"))));
        rnTOTextArea.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
                new HashSet<KeyStroke>(Collections.singletonList(KeyStroke.getKeyStroke("shift pressed TAB"))));
        rnMigratiePadTextArea.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                new HashSet<KeyStroke>(Collections.singletonList(KeyStroke.getKeyStroke("pressed TAB"))));
        rnMigratiePadTextArea.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
                new HashSet<KeyStroke>(Collections.singletonList(KeyStroke.getKeyStroke("shift pressed TAB"))));
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
