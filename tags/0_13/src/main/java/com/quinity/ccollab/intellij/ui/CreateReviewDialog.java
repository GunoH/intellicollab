package com.quinity.ccollab.intellij.ui;

import com.intellij.openapi.ui.ComboBox;
import com.quinity.ccollab.intellij.MessageResources;
import com.smartbear.ccollab.datamodel.GroupDescription;
import com.smartbear.ccollab.datamodel.IDropDownItem;
import com.smartbear.ccollab.datamodel.MetaDataSelectItem;
import com.smartbear.ccollab.datamodel.ReviewAccess;
import com.smartbear.ccollab.datamodel.User;
import org.apache.commons.lang.StringUtils;

import java.awt.Color;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

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

	/**
	 * De default border voor invoervelden; deze bewaren we zodat we na het tonen van een eventuele foutmelding de
	 * border kunnen restoren.
	 */
	private Border defaultBorder;

	/**
	 * De border voor niet-validerende velden.
	 */
	private Border highlightBorder;

	private DefaultComboBoxModel reviewerComboBoxModel;
	private DefaultComboBoxModel authorComboBoxModel;
	private DefaultComboBoxModel observerComboBoxModel;
	private DefaultComboBoxModel groupComboBoxModel;
	private DefaultComboBoxModel bugzillaInstantieComboBoxModel;
	private DefaultComboBoxModel reviewAccessComboBoxModel;

	/**
	 * Lijst met gebruikers waar uit gekozen kan worden in de userinterface.
	 */
	private List<User> userList;

	/**
	 * Lijst met bugzillaInstanties waar uit gekozen kan worden in de userinterface.
	 */
	private List<IDropDownItem> bugzillaInstantieList;

	/**
	 * Lijst met groepen waar uit gekozen kan worden in de userinterface.
	 */
	private List<GroupDescription> groupList;

	/**
	 * Geeft aan of de gebruiker op de OK knop heeft gedrukt of niet.
	 */
	private boolean okPressed;

	/**
	 * Maximale lengte van het 'title' veld.
	 */
	private static final int OVERVIEW_MAXLENGTH = 2000000000;

	/**
	 * Maximale lengte van het 'overview' veld.
	 */
	private static final int TITLE_MAXLENGTH = 255;

	/**
	 * Maximale lengte van het 'Bugzillanummer' veld.
	 */
	private static final int BUGZILLANUMMER_MAXLENGTH = 255;

	public CreateReviewDialog(User[] userList, List<GroupDescription> groupList, IDropDownItem[] bugzillaInstantieList, 
							  User currentUser) {

		this.userList = new ArrayList<User>();
		this.userList.addAll(Arrays.asList(userList));

		this.bugzillaInstantieList = new ArrayList<IDropDownItem>();
		this.bugzillaInstantieList.addAll(Arrays.asList(bugzillaInstantieList));

		this.groupList = groupList;

		update();

		authorComboBoxModel.setSelectedItem(currentUser);

		prepareUI();
	}

	private void prepareUI() {
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

		// Bewaar de default border.
		defaultBorder = titleTextField.getBorder();

		highlightBorder = BorderFactory.createLineBorder(Color.red);

		titleTextField.setDocument(new InputLimiterDocument(TITLE_MAXLENGTH));
		overviewTextArea.setDocument(new InputLimiterDocument(OVERVIEW_MAXLENGTH));
		bugzillaNummerTextField.setDocument(new InputLimiterDocument(BUGZILLANUMMER_MAXLENGTH));
	}

	@SuppressWarnings({"BoundFieldAssignment"})
	private void createUIComponents() {

		authorComboBoxModel = new DefaultComboBoxModel();
		authorComboBox = new ComboBox(authorComboBoxModel, -1);
		authorComboBox.setRenderer(new UserComboboxRenderer());
		authorComboBox.setKeySelectionManager(new IncrementalKeySelManager() {
			@Override
			protected String getDisplayedText(Object object) {
				return ((User)object).getDisplayName().toUpperCase();
			}
		});

		reviewerComboBoxModel = new DefaultComboBoxModel();
		reviewerComboBox = new ComboBox(reviewerComboBoxModel, -1);
		reviewerComboBox.setRenderer(new UserComboboxRenderer());
		reviewerComboBox.setKeySelectionManager(new IncrementalKeySelManager() {
			@Override
			protected String getDisplayedText(Object object) {
				return ((User)object).getDisplayName().toUpperCase();
			}
		});

		observerComboBoxModel = new DefaultComboBoxModel();
		observerComboBox = new ComboBox(observerComboBoxModel, -1);
		observerComboBox.setRenderer(new UserComboboxRenderer());
		observerComboBox.setKeySelectionManager(new IncrementalKeySelManager() {
			@Override
			protected String getDisplayedText(Object object) {
				return ((User)object).getDisplayName().toUpperCase();
			}
		});

		groupComboBoxModel = new DefaultComboBoxModel();
		groupComboBox = new ComboBox(groupComboBoxModel, -1);
		groupComboBox.setRenderer(new GroupDescriptionRenderer());
		groupComboBox.setKeySelectionManager(new IncrementalKeySelManager() {
			@Override
			protected String getDisplayedText(Object object) {
				return ((GroupDescription)object).getDisplayName().toUpperCase();
			}
		});

		bugzillaInstantieComboBoxModel = new DefaultComboBoxModel();
		bugzillaInstantieComboBox = new ComboBox(bugzillaInstantieComboBoxModel, -1);
		bugzillaInstantieComboBox.setRenderer(new IDropDownItemComboboxRenderer());
		bugzillaInstantieComboBox.setKeySelectionManager(new IncrementalKeySelManager() {
			@Override
			protected String getDisplayedText(Object object) {
				return ((MetaDataSelectItem)object).getDisplayName().toUpperCase();
			}
		});

		reviewAccessComboBoxModel = new DefaultComboBoxModel();
		reviewAccessComboBox = new ComboBox(reviewAccessComboBoxModel, -1);
		reviewAccessComboBox.setRenderer(new ReviewAccessComboboxRenderer());
		reviewAccessComboBox.setKeySelectionManager(new IncrementalKeySelManager() {
			@Override
			protected String getDisplayedText(Object object) {
				return ((ReviewAccess)object).getDisplayName().toUpperCase();
			}
		});
		
		overviewTextArea = new JTextArea();
		// Make sure tab and shift-tab move the focus instead of inserting tab characters.
		overviewTextArea.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, 
				new HashSet<KeyStroke>(Arrays.asList(KeyStroke.getKeyStroke("pressed TAB"))));
		overviewTextArea.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, 
				new HashSet<KeyStroke>(Arrays.asList(KeyStroke.getKeyStroke("shift pressed TAB"))));
	}

	public void update() {

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
			groupComboBox.setBorder(highlightBorder);
			if (result) {
				// This is the first error, so set focus to this field.
				groupComboBox.grabFocus();
			}
			result = false;
		} else {
			groupComboBox.setBorder(defaultBorder);
		}

		if (StringUtils.isEmpty(titleTextField.getText())) {
			titleTextField.setBorder(highlightBorder);
			if (result) {
				// This is the first error, so set focus to this field.
				titleTextField.grabFocus();
			}
			result = false;
		} else {
			titleTextField.setBorder(defaultBorder);
		}

		if (authorComboBoxModel.getSelectedItem() == null) {
			authorComboBox.setBorder(highlightBorder);
			if (result) {
				// This is the first error, so set focus to this field.
				authorComboBox.grabFocus();
			}
			result = false;
		} else {
			authorComboBox.setBorder(defaultBorder);
		}

		if (reviewerComboBoxModel.getSelectedItem() == null) {
			reviewerComboBox.setBorder(highlightBorder);
			if (result) {
				// This is the first error, so set focus to this field.
				reviewerComboBox.grabFocus();
			}
			result = false;
		} else {
			reviewerComboBox.setBorder(defaultBorder);
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

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			User user = (User) value;

			if (isSelected) {
				setBackground(Color.BLUE);
			} else {
				setBackground(list.getBackground());
			}

			String name = "";
			if (value != null) {
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

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			ReviewAccess reviewAccess = (ReviewAccess) value;

			if (isSelected) {
				setBackground(Color.BLUE);
			} else {
				setBackground(list.getBackground());
			}

			String name = "";
			if (value != null) {
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

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			IDropDownItem item = (IDropDownItem) value;

			if (isSelected) {
				setBackground(Color.BLUE);
			} else {
				setBackground(list.getBackground());
			}

			String name = "";
			if (value != null) {
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

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			GroupDescription item = (GroupDescription) value;

			if (isSelected) {
				setBackground(Color.BLUE);
			} else {
				setBackground(list.getBackground());
			}

			String name = "";
			if (value != null) {
				name = item.getDisplayName();
			}
			setText(name);

			return this;
		}
	}
}
