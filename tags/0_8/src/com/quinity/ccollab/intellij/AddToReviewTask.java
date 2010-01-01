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
import com.smartbear.ccollab.client.CollabClientAskIOException;
import com.smartbear.ccollab.client.CollabClientFilesNotManagedException;
import com.smartbear.ccollab.client.CollabClientInvalidInputException;
import com.smartbear.ccollab.datamodel.Changelist;
import com.smartbear.ccollab.datamodel.Engine;
import com.smartbear.ccollab.datamodel.Review;
import com.smartbear.ccollab.datamodel.Scm;
import com.smartbear.scm.IScmClientConfiguration;
import com.smartbear.scm.IScmLocalCheckout;
import com.smartbear.scm.ScmChangeset;
import com.smartbear.scm.ScmConfigurationException;
import com.smartbear.scm.ScmUtils;
import com.smartbear.scm.impl.concurrentvs.CvsSystem;

public class AddToReviewTask extends Task.Backgroundable {

	private static Logger logger = Logger.getInstance(AddToReviewTask.class.getName());

	private Review review;

	private FilePath[] files;

	private boolean wasSuccessful;
	private String errorMessage;

	public AddToReviewTask(Project project, Review review, FilePath... files) {
		super(project, MessageResources.message("task.addFilesToReview.title"), false);

		this.review = review;
		this.files = files;
	}

	@Override
	public void run(ProgressIndicator progressIndicator) {

		try {
			progressIndicator.setText(MessageResources.message("progressIndicator.addToReview.preparing"));
			
			// Create the SCM ChangeSet object to upload.  You can attach
			// many types of objects here from uncontrolled files as in this
			// example to controlled files (both local and server-side-only)
			// to SCM-specific atomic changelists (e.g. with Perforce and Subversion).
			logger.debug("Creating SCM Changeset...");
			ScmChangeset changeset = new ScmChangeset();

			
			IScmClientConfiguration clientConfig = retrieveClientConfig(files[0]);
			IScmLocalCheckout scmFile = null;


			if (files.length == 0) {
				wasSuccessful = true;
				return;
			}

			int fileCounter = 0;
			for (FilePath filePath : files) {
				progressIndicator.setText2(MessageResources.message("progressIndicator.addToReview.fileUploadProgress", filePath.getName(), 
						++fileCounter, files.length));
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
				scmFile = clientConfig.getLocalCheckout(file, new NullProgressMonitor());
				changeset.addLocalCheckout(scmFile, new NullProgressMonitor());
			}

			progressIndicator.setText(MessageResources.message("progressIndicator.addToReview.uploading"));
			progressIndicator.setText2("");
			
			// Upload this changeset to Collaborator.  Another form of this
			// uploader lets us specify even more information; this form extracts it
			// automatically from the files in the changeset.
			logger.debug("Uploading SCM Changeset...");
			Engine engine = AddControlledFileAction.client.getEngine(new NullProgressMonitor());
//		Scm scm = engine.scmByLocalCheckout(clientConfig.getScmSystem(), scmFile);			// select the SCM system that matches the client configuration

			Scm scm = engine.scmByLocalCheckout(CvsSystem.INSTANCE, scmFile);			// Uses the CVS SCM system
			Changelist changelist = scm.uploadChangeset(changeset, "Local Files", new NullProgressMonitor());

			progressIndicator.setText(MessageResources.message("progressIndicator.addToReview.attaching", review.getId()));
			
			// The changelist has been uploaded but it hasn't been attached
			// to any particular review!  This two-step process not only allows for
			// a changelist to be part of more than one review, but also means that
			// if there's any error in uploading the changelist the review hasn't
			// changed at all so no one will be affected.
			review.addChangelist(changelist);

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
	
	/**
	 * Retrieves the client configuration that is used to access the SCM server.
	 *
	 * @param filePath Filepath used to retrieve the SCM information.
	 * @return The client configuration that is used to access the SCM server.
	 */
	private IScmClientConfiguration retrieveClientConfig(FilePath filePath) throws CollabClientAskIOException,
			CollabClientFilesNotManagedException, ScmConfigurationException, CollabClientInvalidInputException,
			IntelliCcollabException {

		if (filePath.isDirectory()) {
			logger.error("error: path points to a directory instead of to a file: " + filePath.getPath());
			throw new IntelliCcollabException("error: path points to a directory instead of to a file: " + filePath.getPath());
		}

		File file = filePath.getIOFile();
		
		// If the file does not exist, we go up in the tree until we find a file that does exist in order to get vcs information.
		while (!file.exists()) {
			file = file.getParentFile();
		}
		
		return AddControlledFileAction.client.requireScm(file, new NullProgressMonitor(), ScmUtils.SCMS);
	}

	private void showConfirmDialog(Review review, FilePath... files) {
		Messages.showInfoMessage(MessageResources.message("dialog.filesHaveBeenUploaded.text", files.length , review.getId(), review.getTitle()), 
				MessageResources.message("dialog.filesHaveBeenUploaded.title"));
	}

	@Override
	public boolean shouldStartInBackground() {
		return false;
	}
}
