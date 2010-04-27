package com.quinity.ccollab.intellij;

import com.intellij.openapi.actionSystem.AnActionEvent;

import java.io.File;

public class AddControlledFileFromEditorTab extends AddControlledFileAction {

	@Override
	protected File[] getCurrentlySelectedFiles(AnActionEvent event) {
		return PluginUtil.getCurrentFiles(event.getDataContext());
	}
}