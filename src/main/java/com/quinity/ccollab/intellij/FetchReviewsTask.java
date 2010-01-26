package com.quinity.ccollab.intellij;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.smartbear.CollabClientException;
import com.smartbear.ccollab.client.CollabClientConnection;
import com.smartbear.ccollab.datamodel.Review;
import org.jetbrains.annotations.NotNull;

public class FetchReviewsTask extends Task.Modal {

	private static Logger logger = Logger.getInstance(FetchReviewsTask.class.getName());

	private boolean success;
	
	private CollabClientConnection client;
	
	private Review[] reviews;

	public FetchReviewsTask(Project project, CollabClientConnection client) {
		super(project, MessageResources.message("task.selectReview.title"), false);
		
		this.client = client;
	}

	@Override
	public void run(@NotNull ProgressIndicator progressIndicator) {

		try {
			progressIndicator.setText(MessageResources.message("progressIndicator.addToReview.retrievingReviews"));

			// Retrieve all reviews the user can upload to.
			reviews = client.getUser().getReviewsCanUploadChangelists(null);

			success = true;

		} catch (CollabClientException e) {
			logger.error(e);
		}
	}

	@Override
	public void onSuccess() {
		if (!success) {
			Messages.showErrorDialog(MessageResources.message("errorDialog.errorOccured.text"), 
					MessageResources.message("errorDialog.errorOccured.title"));
		}
	}

	public Review[] getReviews() {
		return reviews;
	}
}
