package com.quinity.ccollab.intellij;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.smartbear.CollabClientException;
import com.smartbear.beans.ConfigUtils;
import com.smartbear.beans.GlobalOptions;
import com.smartbear.beans.ISettableGlobalOptions;
import com.smartbear.ccollab.CommandLineClient;
import com.smartbear.ccollab.client.CollabClientConnection;
import com.smartbear.ccollab.client.CollabClientServerConnectivityException;
import com.smartbear.ccollab.datamodel.Changelist;
import com.smartbear.ccollab.datamodel.Engine;
import com.smartbear.ccollab.datamodel.Review;
import com.smartbear.ccollab.datamodel.Scm;
import com.smartbear.scm.IScmClientConfiguration;
import com.smartbear.scm.IScmLocalCheckout;
import com.smartbear.scm.ScmChangeset;
import com.smartbear.scm.ScmUtils;
import com.smartbear.scm.ScmConfigurationException;

public class AddControlledFileAction extends AnAction {

	/**
	 * Connection to the Collaborator server, created by init().
	 */
	protected static CollabClientConnection client;

	private static Logger logger = Logger.getInstance(AddControlledFileAction.class.getName());


	public void actionPerformed(AnActionEvent event) {

		try {
			init();

			// Show a dialog to the user where (s)he can select a review.
			Integer selectedReviewId = showChooseReviewDialog();

			if (selectedReviewId != null) {
				// Retrieve the selected review.
				Review review = client.getEngine(new NullProgressMonitor()).reviewById(selectedReviewId);

				// Retrieve the current file(s)
				VirtualFile[] files = PluginUtil.getCurrentVirtualFiles(event.getDataContext());

				// Add the current file to the selected review.
				attachControlledFiles(review, files);
			}
		} catch (ScmConfigurationException e1) {
			logger.debug(e1);
			Messages.showMessageDialog("Something went wrong when determining which SCM system to use.",
					"SCM Exception", Messages.getErrorIcon());
		} catch (CollabClientServerConnectivityException e1) {
			logger.debug(e1);
			Messages.showMessageDialog("A connection error occured when trying to reach Code Collaborator server.",
					"Connection Exception", Messages.getErrorIcon());
		} catch (CollabClientException e1) {
			logger.debug(e1);
			Messages.showMessageDialog("An error occured.",
					"General error", Messages.getErrorIcon());
		} catch (IOException e1) {
			logger.debug(e1);
			Messages.showMessageDialog("An IO error occured.",
					"IO Error", Messages.getErrorIcon());
		} finally {
			finished();
		}

	}

	private Integer showChooseReviewDialog() throws CollabClientException, IOException {
		// Retrieve all reviews the user can upload to.
		Review[] reviews = getReviewsForUser();

		// 
		List<String> reviewNames = new ArrayList<String>();
		for (Review review : reviews) {
			reviewNames.add(review.getId() + " " + review.getTitle());
		}

		int selectedIndex = Messages.showChooseDialog("Please choose the review to add this file to", "Choose review",
				reviewNames.toArray(new String[reviewNames.size()]), "", Messages.getQuestionIcon());

		if (selectedIndex < 0) {
			// User pressed the cancel button.
			return null;
		}
		return reviews[selectedIndex].getId();
	}

	/**
	 * Attaches local files that are under version control to the given review
	 */
	private void attachControlledFiles(Review review, VirtualFile... virtualFiles) throws CollabClientException, IOException {
		// Parameter validation
		if (review == null) {
			logger.error("error: no such review");
			return;
		}

		// Create the SCM ChangeSet object to upload.  You can attach
		// many types of objects here from uncontrolled files as in this
		// example to controlled files (both local and server-side-only)
		// to SCM-specific atomic changelists (e.g. with Perforce and Subversion).
		logger.debug("Creating SCM Changeset...");
		ScmChangeset changeset = new ScmChangeset();

		IScmClientConfiguration clientConfig = null;
		IScmLocalCheckout scmFile = null;
		for (VirtualFile virtualFile : virtualFiles) {
			String path = virtualFile.getPath();
			logger.debug("Working with file: " + path);


			File file = new File(path);
			if (!file.exists() || (!file.isFile() /*&& !file.isDirectory()*/)) {
				logger.error("error: path not an existing file or dir: " + file.getAbsolutePath());
				return;
			}

			// Create the SCM object representing a local file under version control.
			// We assume the local SCM is already configured properly.
			logger.debug("Loading SCM File object...");
			clientConfig = client.requireScm(file, new NullProgressMonitor(), ScmUtils.SCMS);
			scmFile = clientConfig.getLocalCheckout(file, new NullProgressMonitor());
			changeset.addLocalCheckout(scmFile, new NullProgressMonitor());
		}

		// Upload this changeset to Collaborator.  Another form of this
		// uploader lets us specify even more information; this form extracts it
		// automatically from the files in the changeset.
		logger.debug("Uploading SCM Changeset...");
		Engine engine = client.getEngine(new NullProgressMonitor());
		Scm scm = engine.scmByLocalCheckout(clientConfig.getScmSystem(), scmFile);			// select the SCM system that matches the client configuration
		Changelist changelist = scm.uploadChangeset(changeset, "Local Files", new NullProgressMonitor());

		// The changelist has been uploaded but it hasn't been attached
		// to any particular review!  This two-step process not only allows for
		// a changelist to be part of more than one review, but also means that
		// if there's any error in uploading the changelist the review hasn't
		// changed at all so no one will be affected.
		review.addChangelist(changelist);
	}

	private Review[] getReviewsForUser() throws CollabClientException, IOException {
		return client.getUser().getReviewsCanUploadChangelists(null);
	}

	private static void init() throws CollabClientException, IOException {
		// If we've already initialized, don't do it again.
		if (client != null) {
			return;
		}

		//load options from config files
		ISettableGlobalOptions options = GlobalOptions.copy(ConfigUtils.loadConfigFiles());

		//initialize interface to client api
		client = new CollabClientConnection(new CommandLineClient(options), options);
	}

	/**
	 * Called to clean up a previous call to <code>init()</code>.
	 * <p/>
	 * <b>THIS IS CRITICAL</b>.  If you do not close out your <code>CollabClientConnection</code>
	 * object, data might not be flushed out to the server!
	 */
	private void finished() {
		if (client != null) {
			client.finished(true, new NullProgressMonitor());
		}
	}

}