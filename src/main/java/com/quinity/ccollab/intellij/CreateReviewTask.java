package com.quinity.ccollab.intellij;

import java.io.IOException;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.smartbear.CollabClientException;
import com.smartbear.ccollab.datamodel.Review;
import com.smartbear.ccollab.datamodel.ReviewAccess;
import com.smartbear.ccollab.datamodel.ReviewParticipant;
import com.smartbear.ccollab.datamodel.Role;
import com.smartbear.ccollab.datamodel.User;
import org.jetbrains.annotations.NotNull;

public class CreateReviewTask extends Task.Modal {

	private static Logger logger = Logger.getInstance(CreateReviewTask.class.getName());

	private boolean success;

	private User user;
	private String reviewTitle;
	private String reviewOverview;
	private boolean uploadRestricted;
	private ReviewAccess reviewAccess;
	private User author;
	private User reviewer;
	private User observer;

	public CreateReviewTask(Project project, User user, String reviewTitle, String reviewOverview, 
							boolean uploadRestricted, ReviewAccess reviewAccess, User author, User reviewer, 
							User observer) {
		super(project, MessageResources.message("task.createReview.title"), false);

		this.user = user;
		this.reviewTitle = reviewTitle;
		this.reviewOverview = reviewOverview;
		this.uploadRestricted = uploadRestricted;
		this.reviewAccess = reviewAccess;
		this.author = author;
		this.reviewer = reviewer;
		this.observer = observer;
	}

	@Override
	public void run(@NotNull ProgressIndicator progressIndicator) {

		try {
			progressIndicator.setText(MessageResources.message("progressIndicator.createReview.start"));
			
			createReview();

			success = true;

		} catch (CollabClientException e) {
			logger.warn(e);
		} catch (IOException e) {
			logger.warn(e);
		}
	}

	/**
	 * Creates a new review object
	 */
	private void createReview() throws CollabClientException, IOException {

		// Create the new review object with the local user as the creator
		Review review = user.getEngine().reviewCreate( user, "Untitled Review" );
		review.setTitle(reviewTitle);
		review.setOverview(reviewOverview);
		review.setUploadRestricted(uploadRestricted);
		review.setReviewAccess(reviewAccess);

		String template = user.getEngine().roleStandardTemplateName();
		Role[] roles = user.getEngine().rolesFind(template, true);
		
		if (author != null) {
			review.addParticipants(new ReviewParticipant(author, Role.findAuthor(roles)));
		}
		if (reviewer != null) {
			review.addParticipants(new ReviewParticipant(reviewer, Role.findReviewer(roles)));
		}
		if (observer != null) {
			review.addParticipants(new ReviewParticipant(observer, Role.findObserver(roles)));
		}
		review.save();
		logger.debug("New review created: " + review.getDisplayText(true));
	}
	
	@Override
	public void onSuccess() {
		if (!success) {
			Messages.showErrorDialog(MessageResources.message("errorDialog.errorOccured.text"),
					MessageResources.message("errorDialog.errorOccured.title"));
		}
	}
}
