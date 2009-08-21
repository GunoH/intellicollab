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

	private Integer selectedReviewId;

	private CollabClientConnection client;
	
	private List<String> reviewNames;
	private Review[] reviews;

	public FetchReviewsTask(Project project, CollabClientConnection client) {
		super(project, "Select review", false);
		
		this.client = client;
	}

	@Override
	public void run(ProgressIndicator progressIndicator) {

		try {
			progressIndicator.setText("Retrieving list of reviews from server");

			// Retrieve all reviews the user can upload to.
			reviews = client.getUser().getReviewsCanUploadChangelists(null);

			reviewNames = new ArrayList<String>();
			for (Review review : reviews) {
				reviewNames.add(review.getId() + " " + review.getTitle());
			}

		} catch (CollabClientException e) {
			logger.error(e);
			Messages.showErrorDialog("An error occured.", "General error");
		}
	}

	@Override
	public void onSuccess() {
		int selectedIndex = Messages.showChooseDialog("Please choose the review to add this/these file(s) to", "Choose review",
				reviewNames.toArray(new String[reviewNames.size()]), "", Messages.getQuestionIcon());

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