package com.quinity.ccollab.intellij;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.smartbear.ccollab.datamodel.DataModelException;
import com.smartbear.ccollab.datamodel.User;
import org.jetbrains.annotations.NotNull;

public class FetchUsersTask extends Task.Modal {

	private static Logger logger = Logger.getInstance(FetchUsersTask.class.getName());

	private boolean success;
	
	private User user;
	
	private User[] users;

	public FetchUsersTask(Project project, User user) {
		super(project, MessageResources.message("task.createReview.title"), false);
		
		this.user = user;
	}

	@Override
	public void run(@NotNull ProgressIndicator progressIndicator) {

		progressIndicator.setText(MessageResources.message("progressIndicator.createReview.retrievingUsers"));

		try {
			// Retrieve all users from the code collaborator server
			users = user.getEngine().usersPossibleReviewParticipants(null);
		} catch (DataModelException e) {
			logger.warn("Error when retrieving users.", e);
			return;
		}
		success = true;
	}

	@Override
	public void onSuccess() {
		if (!success) {
			Messages.showErrorDialog(MessageResources.message("errorDialog.errorOccured.text"), 
					MessageResources.message("errorDialog.errorOccured.title"));
		}
	}

	public User[] getUsers() {
		return users;
	}
}