package com.quinity.ccollab.intellij;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.NullProgressMonitor;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
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

	private VirtualFile[] virtualFiles;

	private boolean wasSuccessful;
	private String errorMessage;

	public AddToReviewTask(Project project, Review review, VirtualFile... virtualFiles) {
		super(project, "Add file(s) to review", false);

		this.review = review;
		this.virtualFiles = virtualFiles;
	}

	@Override
	public void run(ProgressIndicator progressIndicator) {

		try {
			progressIndicator.setText("Preparing files for addition to review");
			
			// Create the SCM ChangeSet object to upload.  You can attach
			// many types of objects here from uncontrolled files as in this
			// example to controlled files (both local and server-side-only)
			// to SCM-specific atomic changelists (e.g. with Perforce and Subversion).
			logger.debug("Creating SCM Changeset...");
			ScmChangeset changeset = new ScmChangeset();

			
			IScmClientConfiguration clientConfig = retrieveClientConfig(virtualFiles[0]);
			IScmLocalCheckout scmFile = null;


			if (virtualFiles.length == 0) {
				wasSuccessful = true;
				return;
			}

			int fileCounter = 0;
			for (VirtualFile virtualFile : virtualFiles) {
				progressIndicator.setText2(virtualFile.getName() + " (" + ++fileCounter + " of " + virtualFiles.length + ")");
				String path = virtualFile.getPath();
				logger.debug("Working with file: " + path);


				File file = new File(path);
				if (!file.exists() || (!file.isFile())) {
					logger.error("error: path not an existing file: " + file.getAbsolutePath());
					throw new IntelliCcollabException("error: path not an existing file: " + file.getAbsolutePath());
				}

				// Create the SCM object representing a local file under version control.
				// We assume the local SCM is already configured properly.
				logger.debug("Loading SCM File object...");
				scmFile = clientConfig.getLocalCheckout(file, new NullProgressMonitor());
				changeset.addLocalCheckout(scmFile, new NullProgressMonitor());
			}

			progressIndicator.setText("Uploading changeset");
			progressIndicator.setText2("");
			
			// Upload this changeset to Collaborator.  Another form of this
			// uploader lets us specify even more information; this form extracts it
			// automatically from the files in the changeset.
			logger.debug("Uploading SCM Changeset...");
			Engine engine = AddControlledFileAction.client.getEngine(new NullProgressMonitor());
//		Scm scm = engine.scmByLocalCheckout(clientConfig.getScmSystem(), scmFile);			// select the SCM system that matches the client configuration

			Scm scm = engine.scmByLocalCheckout(CvsSystem.INSTANCE, scmFile);			// Uses the CVS SCM system
			Changelist changelist = scm.uploadChangeset(changeset, "Local Files", new NullProgressMonitor());

			progressIndicator.setText("Attaching changes to review " + review.getId());
			
			// The changelist has been uploaded but it hasn't been attached
			// to any particular review!  This two-step process not only allows for
			// a changelist to be part of more than one review, but also means that
			// if there's any error in uploading the changelist the review hasn't
			// changed at all so no one will be affected.
			review.addChangelist(changelist);

			wasSuccessful = true;
		} catch (ScmConfigurationException e) {
			logger.error(e);
			errorMessage = "Something went wrong when determining which SCM system to use.";
		} catch (CollabClientException e) {
			logger.error(e);
			errorMessage = "An error occured.";
		} catch (IntelliCcollabException e) {
			logger.error(e);
			errorMessage = "An error occured: " + e.getMessage();
		} catch (IOException e) {
			logger.error(e);
			errorMessage = "An IO error occured.";
		}
		
	}

	@Override
	public void onSuccess() {
		if (wasSuccessful) {
			showConfirmDialog(review, virtualFiles);
		} else {
			Messages.showErrorDialog(errorMessage, "An error occured.");
		}
	}
	
	/**
	 * Retrieves the client configuration that is used to access the SCM server.
	 *
	 * @param virtualFile File used to retrieve the SCM information.
	 * @return The client configuration that is used to access the SCM server.
	 */
	private IScmClientConfiguration retrieveClientConfig(VirtualFile virtualFile) throws CollabClientAskIOException,
			CollabClientFilesNotManagedException, ScmConfigurationException, CollabClientInvalidInputException,
			IntelliCcollabException {

		File file = new File(virtualFile.getPath());
		if (!file.exists() || (!file.isFile())) {
			logger.error("error: path not an existing file: " + file.getAbsolutePath());
			throw new IntelliCcollabException("error: path not an existing file: " + file.getAbsolutePath());
		}

		return AddControlledFileAction.client.requireScm(file, new NullProgressMonitor(), ScmUtils.SCMS);
	}

	private void showConfirmDialog(Review review, VirtualFile... files) {
		Messages.showInfoMessage(files.length + " file(s) have been uploaded to review " + review.getId() + ": " + review.getTitle(), "Success");
	}

	@Override
	public boolean shouldStartInBackground() {
		return false;
	}
}