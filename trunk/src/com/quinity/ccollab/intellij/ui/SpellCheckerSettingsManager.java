package com.quinity.ccollab.intellij.ui;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import javax.swing.JComponent;

public class SpellCheckerSettingsManager implements Configurable {
  private SpellCheckerSettingsPane settingsPane;
  private SpellCheckerSettings settings;
  private Project project;

  public static SpellCheckerSettingsManager getInstance(Project project) {
    return ShowSettingsUtil.getInstance().findProjectConfigurable(project, SpellCheckerSettingsManager.class);
  }

  public SpellCheckerSettingsManager(Project project) {
    this.project = project;
    settings = SpellCheckerSettings.getInstance(project);
  }

  @Nls
   public String getDisplayName() {
     return SpellCheckerBundle.message("spelling");
   }

   @Nullable
   public Icon getIcon() {
     return null;
   }

   @Nullable
   @NonNls
   public String getHelpTopic() {
     return "reference.settings.ide.settings.spelling";
   }


  public JComponent createComponent() {
    if (settingsPane == null) {
      settingsPane = new SpellCheckerSettingsPane(settings,project);
    }
    return settingsPane.getPane();
  }

  public boolean isModified() {
    return settingsPane == null || settingsPane.isModified();
  }

  public void apply() throws ConfigurationException {
    if (settingsPane != null) {
      settingsPane.apply();
    }
  }

  public void reset() {
    if (settingsPane != null) {
      settingsPane.reset();
    }
  }

  public void disposeUIResources() {
    settingsPane = null;
  }
}
