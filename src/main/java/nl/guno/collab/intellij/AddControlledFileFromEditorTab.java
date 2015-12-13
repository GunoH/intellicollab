package nl.guno.collab.intellij;

import java.io.File;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.AnActionEvent;

public class AddControlledFileFromEditorTab extends AddControlledFileAction {

    @Override @NotNull
    protected File[] getCurrentlySelectedFiles(@NotNull AnActionEvent event) {
        return PluginUtil.getCurrentFiles(event.getDataContext());
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        // This action should always be enabled, because we know which file is selected.
    }
}
