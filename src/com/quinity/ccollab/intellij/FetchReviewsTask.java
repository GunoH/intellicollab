package com.quinity.ccollab.intellij;

import java.util.ArrayList;
import java.util.List;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.smartbear.CollabClientException;
import com.smartbear.ccollab.client.CollabClientConnection;
import com.smartbear.ccollab.datamodel.Review;

public class FetchReviewsTask extends Task.Modal {

	private static Logger logger = Logger.getInstance(FetchReviewsTask.class.getName());

	private boolean success;
	
	private Integer selectedReviewId;

	private CollabClientConnection client;
	
	private List<String> reviewNames;
	private Review[] reviews;

	public FetchReviewsTask(Project project, CollabClientConnection client) {
		super(project, MessageResources.message("task.selectReview.title"), false);
		
		this.client = client;
	}

	@Override
	public void run(ProgressIndicator progressIndicator) {

		try {
			progressIndicator.setText(MessageResources.message("progressIndicator.addToReview.retrievingReviews"));

			// Retrieve all reviews the user can upload to.
			reviews = client.getUser().getReviewsCanUploadChangelists(null);

			reviewNames = new ArrayList<String>();
			for (Review review : reviews) {
				reviewNames.add(review.getId() + " " + review.getTitle());
			}
			
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
			
			return;
		}

		int selectedIndex = Messages.showChooseDialog(MessageResources.message("dialog.selectReview.text"),
				MessageResources.message("dialog.selectReview.title"), reviewNames.toArray(new String[reviewNames.size()]), "", 
				Messages.getQuestionIcon());

		if (selectedIndex < 0) {
			// User pressed the cancel button.
			return;
		}

		selectedReviewId = reviews[selectedIndex].getId();
	}
	
	public Integer getSelectedReviewId() {
		return selectedReviewId;
	}
}
