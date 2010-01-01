package com.quinity.ccollab.intellij.ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ResourceBundle;

import javax.swing.*;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.GridConstraints;

public class SpellCheckerSettingsPane implements Disposable {
	private JPanel root;
	private JPanel linkContainer;
	private JPanel panelForDictionaryChooser;
	private JPanel panelForAcceptedWords;
	private JPanel panelForFolderChooser;
	private OptionalChooserComponent optionalChooserComponent;
	//  private PathsChooserComponent pathsChooserComponent;
	private final List<Pair<String, Boolean>> allDictionaries = new ArrayList<Pair<String, Boolean>>();
	private final List<String> dictionariesFolders = new ArrayList<String>();
//  private final WordsPanel wordsPanel;
//  private final SpellCheckerManager manager;
private final SpellCheckerSettings settings;

	public SpellCheckerSettingsPane(SpellCheckerSettings settings, final Project project) {
		this.settings = settings;
//    manager = SpellCheckerManager.getInstance(project);
//    HyperlinkLabel link = new HyperlinkLabel(SpellCheckerBundle.message("link.to.inspection.settings"));
/*
    link.addHyperlinkListener(new HyperlinkListener() {
      public void hyperlinkUpdate(final HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
          final OptionsEditor optionsEditor = OptionsEditor.KEY.getData(DataManager.getInstance().getDataContext());
          final ErrorsConfigurable toolsConfigurable = ErrorsConfigurable.SERVICE.getInstance(project);
          if (optionsEditor != null && toolsConfigurable != null) {
            optionsEditor.select(toolsConfigurable).doWhenDone(new Runnable() {
              public void run() {
                toolsConfigurable.selectInspectionTool("SpellCheckingInspection");
              }
            });

          }

        }
      }
    });
*/
		linkContainer.setLayout(new BorderLayout());
//    linkContainer.add(link);

		// Fill in all the dictionaries folders (not implemented yet) and enabled dictionaries
//    fillAllDictionaries();

/*    pathsChooserComponent = new PathsChooserComponent(dictionariesFolders, new PathsChooserComponent.PathProcessor() {
	  public boolean addPath(List<String> paths, String path) {
		if (paths.contains(path)) {
		  final String title = SpellCheckerBundle.message("add.directory.title");
		  final String msg = SpellCheckerBundle.message("directory.is.already.included");
		  Messages.showErrorDialog(root, msg, title);
		  return false;
		}
		paths.add(path);

		final ArrayList<Pair<String, Boolean>> currentDictionaries = optionalChooserComponent.getValue();
		SPFileUtil.processFilesRecursively(path, new Consumer<String>() {
		  public void consume(final String s) {
			currentDictionaries.add(Pair.create(s, true));
		  }
		});
		optionalChooserComponent.update();
		return true;
	  }

	  public boolean removePath(List<String> paths, String path) {
		if (paths.remove(path)) {
		  final ArrayList<Pair<String, Boolean>> result = new ArrayList<Pair<String, Boolean>>();
		  final ArrayList<Pair<String, Boolean>> currentDictionaries = optionalChooserComponent.getValue();
		  for (Pair<String, Boolean> pair : currentDictionaries) {
			if (!pair.first.startsWith(FileUtil.toSystemDependentName(path))) {
			  result.add(pair);
			}
		  }
		  currentDictionaries.clear();
		  currentDictionaries.addAll(result);
		  optionalChooserComponent.update();
		  return true;
		}
		return false;
	  }
	});*/

		panelForFolderChooser.setLayout(new BorderLayout());
//    panelForFolderChooser.add(pathsChooserComponent.getContentPane(), BorderLayout.CENTER);


		optionalChooserComponent = new OptionalChooserComponent(allDictionaries) {
			@Override
			public JCheckBox createCheckBox(String path, boolean checked) {
/*
        if (isUserDictionary(path)) {
          path = FileUtil.toSystemIndependentName(path);
          final int i = path.lastIndexOf('/');
          if (i != -1) {
            final String name = path.substring(i + 1);
            return new JCheckBox("[user] " + name, checked);
          }
        }
*/
				return new JCheckBox("[bundled] " + FileUtil.toSystemDependentName(path), checked);
			}
		};

		panelForDictionaryChooser.setLayout(new BorderLayout());
		panelForDictionaryChooser.add(optionalChooserComponent.getContentPane(), BorderLayout.CENTER);

//    wordsPanel = new WordsPanel(manager);
		panelForAcceptedWords.setLayout(new BorderLayout());
//    panelForAcceptedWords.add(wordsPanel, BorderLayout.CENTER);

	}

	public JComponent getPane() {
		return root;
	}


	public boolean isModified() {
		return /*wordsPanel.isModified() || */optionalChooserComponent.isModified() /*|| pathsChooserComponent.isModified()*/;
	}

	public void apply() throws ConfigurationException {
		optionalChooserComponent.apply();
//    pathsChooserComponent.apply();
//    settings.setDictionaryFoldersPaths(pathsChooserComponent.getValues());

		final HashSet<String> disabledDictionaries = new HashSet<String>();
		final HashSet<String> bundledDisabledDictionaries = new HashSet<String>();
		for (Pair<String, Boolean> pair : allDictionaries) {
			if (!pair.second) {
				final String scriptPath = pair.first;
				bundledDisabledDictionaries.add(scriptPath);
			}

		}
		settings.setDisabledDictionariesPaths(disabledDictionaries);
		settings.setBundledDisabledDictionariesPaths(bundledDisabledDictionaries);

//    manager.update(wordsPanel.getWords(), settings);
	}

/*
  private boolean isUserDictionary(final String dictionary) {
    boolean isUserDictionary = false;
    for (String dictionaryFolder : pathsChooserComponent.getValues()) {
      if (FileUtil.toSystemIndependentName(dictionary).startsWith(dictionaryFolder)) {
        isUserDictionary = true;
        break;
      }
    }
    return isUserDictionary;

  }
*/

	public void reset() {
//    pathsChooserComponent.reset();
//    fillAllDictionaries();
		optionalChooserComponent.reset();
	}

/*
  private void fillAllDictionaries() {
	dictionariesFolders.clear();
	dictionariesFolders.addAll(settings.getDictionaryFoldersPaths());
	allDictionaries.clear();
	for (String dictionary : manager.getBundledDictionaries()) {
	  allDictionaries.add(Pair.create(dictionary, !settings.getBundledDisabledDictionariesPaths().contains(dictionary)));
	}

	// user
	//todo [shkate]: refactoring  - SpellCheckerManager contains the same code withing reloadConfiguration()
	final Set<String> disabledDictionaries = settings.getDisabledDictionariesPaths();
	for (String folder : dictionariesFolders) {
	  SPFileUtil.processFilesRecursively(folder, new Consumer<String>() {
		public void consume(final String s) {
		  allDictionaries.add(Pair.create(s, !disabledDictionaries.contains(s)));
		}
	  });
	}
  }
*/


	public void dispose() {
//		if (wordsPanel != null) {
//			wordsPanel.dispose();
//		}
	}

/*
	public static final class WordDescriber {
		private Dictionary dictionary;

		public WordDescriber(Dictionary dictionary) {
			this.dictionary = dictionary;
		}

		@NotNull
		public List<String> process() {
			if (this.dictionary == null) {
				return new ArrayList<String>();
			}
			Set<String> words = this.dictionary.getEditableWords();
			if (words == null) {
				return new ArrayList<String>();
			}
			List<String> result = new ArrayList<String>();
			for (String word : words) {
				result.add(word);
			}
			Collections.sort(result);
			return result;
		}
	}
*/

/*
  private static final class WordsPanel extends AddDeleteListPanel implements Disposable {
    private SpellCheckerManager manager;

    private WordsPanel(SpellCheckerManager manager) {
      super(null, new WordDescriber(manager.getUserDictionary()).process());
      this.manager = manager;
    }


    protected Object findItemToAdd() {
      String word = Messages.showInputDialog(com.intellij.spellchecker.util.SpellCheckerBundle.message("enter.simple.word"),
                                             SpellCheckerBundle.message("add.new.word"), null);
      if (word == null) {
        return null;
      }
      else {
        word = word.trim();
      }

      if (Strings.isMixedCase(word)) {
        Messages.showWarningDialog(SpellCheckerBundle.message("entered.word.0.is.mixed.cased.you.must.enter.simple.word", word),
                                   SpellCheckerBundle.message("add.new.word"));
        return null;
      }
      if (!manager.hasProblem(word)) {
        Messages.showWarningDialog(SpellCheckerBundle.message("entered.word.0.is.correct.you.no.need.to.add.this.in.list", word),
                                   SpellCheckerBundle.message("add.new.word"));
        return null;
      }
      return word;
    }


    public void dispose() {
      myListModel.removeAllElements();
    }

    @Nullable
    public List<String> getWords() {
      Object[] pairs = getListItems();
      if (pairs == null) {
        return null;
      }
      List<String> words = new ArrayList<String>();
      for (Object pair : pairs) {
        words.add(pair.toString());
      }
      return words;
    }

    public boolean isModified() {
      List<String> newWords = getWords();
      Set<String> words = manager.getUserDictionary().getEditableWords();
      if (words == null && newWords == null) {
        return false;
      }
      if (words == null || newWords == null || newWords.size() != words.size()) {
        return true;
      }
      return !(words.containsAll(newWords) && newWords.containsAll(words));
    }
  }
*/


	{
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
		$$$setupUI$$$();
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		root = new JPanel();
		root.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		root.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
		linkContainer = new JPanel();
		linkContainer.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		panel1.add(linkContainer, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(24, 38), null, 0, false));
		final JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		root.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final JTabbedPane tabbedPane1 = new JTabbedPane();
		panel2.add(tabbedPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(200, 200), null, 0, false));
		final JPanel panel3 = new JPanel();
		panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		tabbedPane1.addTab("Accepted words", panel3);
		panelForAcceptedWords = new JPanel();
		panelForAcceptedWords.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		panel3.add(panelForAcceptedWords, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		final JPanel panel4 = new JPanel();
		panel4.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
		tabbedPane1.addTab("Dictionaries", panel4);
		final JPanel panel5 = new JPanel();
		panel5.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
		panel4.add(panel5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		panel5.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Dictionaries"));
		final JPanel panel6 = new JPanel();
		panel6.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		panel5.add(panel6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
		final JLabel label1 = new JLabel();
		this.$$$loadLabelText$$$(label1, ResourceBundle.getBundle("com/quinity/ccollab/intellij/ui/SpellCheckerBundle").getString("dictionaries.panel.description"));
		panel6.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		panelForDictionaryChooser = new JPanel();
		panelForDictionaryChooser.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		panel5.add(panelForDictionaryChooser, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		final JPanel panel7 = new JPanel();
		panel7.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
		panel4.add(panel7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		panel7.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Custom Dictionaries Folder"));
		final JPanel panel8 = new JPanel();
		panel8.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		panel7.add(panel8, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
		final JLabel label2 = new JLabel();
		this.$$$loadLabelText$$$(label2, ResourceBundle.getBundle("com/quinity/ccollab/intellij/ui/SpellCheckerBundle").getString("add.directory.description"));
		panel8.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		panelForFolderChooser = new JPanel();
		panelForFolderChooser.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		panel7.add(panelForFolderChooser, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
	}

	/**
	 * @noinspection ALL
	 */
	private void $$$loadLabelText$$$(JLabel component, String text) {
		StringBuffer result = new StringBuffer();
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

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return root;
	}
}
