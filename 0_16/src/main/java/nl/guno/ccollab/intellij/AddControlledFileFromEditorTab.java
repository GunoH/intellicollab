package nl.guno.ccollab.intellij;

import java.io.File;

import com.intellij.openapi.actionSystem.AnActionEvent;

public class AddControlledFileFromEditorTab extends AddControlledFileAction {

    @Override
    protected File[] getCurrentlySelectedFiles(AnActionEvent event) {
        return PluginUtil.getCurrentFiles(event.getDataContext());
    }

    public void update(AnActionEvent event) {
        // This action should always be enabled, because we know which file is selected.
    }
}