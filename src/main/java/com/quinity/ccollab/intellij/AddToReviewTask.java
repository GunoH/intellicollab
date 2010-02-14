package com.quinity.ccollab.intellij;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.NullProgressMonitor;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.FilePath;
import com.smartbear.CollabClientException;
import com.smartbear.ccollab.datamodel.Changelist;
import com.smartbear.ccollab.datamodel.Engine;
import com.smartbear.ccollab.datamodel.Review;
import com.smartbear.ccollab.datamodel.Scm;
import com.smartbear.ccollab.datamodel.User;
import com.smartbear.scm.IScmClientConfiguration;
import com.smartbear.scm.IScmLocalCheckout;
import com.smartbear.scm.ScmChangeset;
import com.smartbear.scm.ScmConfigurationException;
import com.smartbear.scm.ScmUtils;
import com.smartbear.scm.impl.concurrentvs.CvsSystem;
import org.jetbrains.annotations.NotNull;

public class AddToReviewTask extends Task.Backgroundable {

	private static Logger logger = Logger.getInstance(AddToReviewTask.class.getName());

	private Review review;

	private FilePath[] files;

	private User user;

	private boolean wasSuccessful;
	private String errorMessage;

	public AddToReviewTask(Project project, Review review, User user, FilePath... files) {
		super(project, MessageResources.message("task.addFilesToReview.title"), false);

		this.review = review;
		this.user = user;
		this.files = files;
	}

	@Override
	public void run(@NotNull ProgressIndicator progressIndicator) {

		try {
			progressIndicator.setText(MessageResources.message("progressIndicator.addToReview.preparing"));
			
			// Create the SCM ChangeSet object to upload.  You can attach
			// many types of objects here from uncontrolled files as in this
			// example to controlled files (both local and server-side-only)
			// to SCM-specific atomic changelists (e.g. with Perforce and Subversion).
			logger.debug("Creating SCM Changeset...");
			ScmChangeset changeset = new ScmChangeset();

			
			IScmClientConfiguration clientConfig;
			IScmLocalCheckout scmFile;


			if (files.length == 0) {
				wasSuccessful = true;
				return;
			}

			int fileCounter = 0;
			for (FilePath filePath : files) {
				progressIndicator.setText2(MessageResources.message("progressIndicator.addToReview.fileUploadProgress", filePath.getName(),
						Integer.valueOf(++fileCounter), Integer.valueOf(files.length)));
				logger.debug("Working with file: " + filePath.getPath());


				if (filePath.isDirectory()) {
					logger.error("error: path points to a directory instead of to a file: " + filePath.getPath());
					throw new IntelliCcollabException("error: path points to a directory instead of to a file: " 
							+ filePath.getPath());
				}

				File file = filePath.getIOFile();
				// Create the SCM object representing a local file under version control.
				// We assume the local SCM is already configured properly.
				logger.debug("Loading SCM File object...");
				clientConfig = ScmUtils.requireScm(file, AddControlledFileAction.scmOptions, new NullProgressMonitor(), ScmUtils.SCMS);
				scmFile = clientConfig.getLocalCheckout(file, new NullProgressMonitor());
				changeset.addLocalCheckout(scmFile, true, new NullProgressMonitor());
			}

			progressIndicator.setText(MessageResources.message("progressIndicator.addToReview.uploading"));
			progressIndicator.setText2("");
			
			// Upload this changeset to Collaborator.  Another form of this
			// uploader lets us specify even more information; this form extracts it
			// automatically from the files in the changeset.
			logger.debug("Uploading SCM Changeset...");
			Engine engine = AddControlledFileAction.engine;
//			Scm scm = engine.scmByLocalCheckout(scmFile);			// select the SCM system that matches the client configuration

			Scm scm = engine.scmCreate();
			scm.setScmConfig(engine.scmConfigCreate(CvsSystem.INSTANCE));// Use the CVS SCM system

			Changelist changelist = scm.uploadChangeset(changeset, "Local Files", new NullProgressMonitor());

			progressIndicator.setText(MessageResources.message("progressIndicator.addToReview.attaching", review.getId()));
			
			// The changelist has been uploaded but it hasn't been attached
			// to any particular review!  This two-step process not only allows for
			// a changelist to be part of more than one review, but also means that
			// if there's any error in uploading the changelist the review hasn't
			// changed at all so no one will be affected.
			review.addChangelist(changelist, user);

			wasSuccessful = true;
		} catch (ScmConfigurationException e) {
			logger.error(e);
			errorMessage = MessageResources.message("errorDialog.cannotDetermineSCMSystem.text");
		} catch (CollabClientException e) {
			logger.error(e);
			errorMessage = MessageResources.message("errorDialog.errorOccured.text");
		} catch (IntelliCcollabException e) {
			logger.error(e);
			errorMessage = MessageResources.message("errorDialog.errorOccured.error.text", e.getMessage());
		} catch (IOException e) {
			logger.error(e);
			errorMessage = MessageResources.message("errorDialog.ioErrorOccured.text");
		}
		
	}

	@Override
	public void onSuccess() {
		if (wasSuccessful) {
			showConfirmDialog(review, files);
		} else {
			Messages.showErrorDialog(errorMessage, MessageResources.message("errorDialog.errorOccured.title"));
		}
	}
	
	private void showConfirmDialog(Review review, FilePath... files) {
		Messages.showInfoMessage(MessageResources.message("task.addFilesToReview.filesHaveBeenUploaded.text", 
				Integer.valueOf(files.length), review.getId(), review.getTitle()), 
				MessageResources.message("task.addFilesToReview.filesHaveBeenUploaded.title"));
	}

	@Override
	public boolean shouldStartInBackground() {
		return false;
	}
}
