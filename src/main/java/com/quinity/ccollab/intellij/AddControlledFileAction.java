package com.quinity.ccollab.intellij;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.eclipse.core.runtime.NullProgressMonitor;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.util.Pair;
import com.smartbear.CollabClientException;
import com.smartbear.beans.ConfigUtils;
import com.smartbear.beans.GlobalOptions;
import com.smartbear.beans.ISettableGlobalOptions;
import com.smartbear.ccollab.CommandLineClient;
import com.smartbear.ccollab.client.CollabClientConnection;
import com.smartbear.ccollab.client.CollabClientServerConnectivityException;
import com.smartbear.ccollab.datamodel.Review;
import com.smartbear.scm.ScmConfigurationException;
import com.quinity.ccollab.intellij.ui.FileAndReviewSelector;

public class AddControlledFileAction extends AnAction {

	/**
	 * Connection to the Collaborator server, created by init().
	 */
	protected static CollabClientConnection client;

	private static Logger logger = Logger.getInstance(AddControlledFileAction.class.getName());


	@Override
	public void actionPerformed(AnActionEvent event) {

		try {
			init();

			if (!checkConnection()) {
				logger.debug("Could not connect to Code Collaborator server.");
				Messages.showErrorDialog(MessageResources.message("task.addFilesToReview.noConnection.text"), 
						MessageResources.message("task.addFilesToReview.noConnection.title"));
				return;
			}
			
			// Retrieve the current file(s)
			FilePath[] files = PluginUtil.getSelectedFilePaths(event);


            List<Pair<FilePath, Boolean>> fileList = new ArrayList<Pair<FilePath, Boolean>>();
            for (FilePath filePath : files) {
                fileList.add(Pair.create(filePath, Boolean.TRUE));
            }
            
			Project project = PluginUtil.getProject(event.getDataContext());

			// Retrieve the reviews the user can upload to
			FetchReviewsTask fetchReviewsTask = new FetchReviewsTask(project, client);
			fetchReviewsTask.queue();

			Review[] reviews = fetchReviewsTask.getReviews();

            FileAndReviewSelector fileAndReviewSelector = new FileAndReviewSelector(fileList, reviews);
            fileAndReviewSelector.pack();
            fileAndReviewSelector.setVisible(true);
			
            if (!fileAndReviewSelector.isOkPressed()) {
                logger.debug("User pressed cancel.");
                return;
            }
            
            files = fileAndReviewSelector.retrieveSelectedFiles(); 
            
			if (files.length == 0) {
				logger.debug("No files selected.");
				Messages.showErrorDialog(MessageResources.message("task.addFilesToReview.noFilesSelected.text"), 
						MessageResources.message("task.addFilesToReview.noFilesSelected.title"));
				return;
			}
			
			Integer selectedReviewId = fileAndReviewSelector.getSelectedReviewId();
			
			if (selectedReviewId != null) {
				// Retrieve the selected review.
				Review review = client.getEngine(new NullProgressMonitor()).reviewById(selectedReviewId);

				// Add the current file to the selected review.
				attachControlledFiles(event, review, files);
			}
		} catch (CollabClientServerConnectivityException e) {
			logger.error(e);
			Messages.showErrorDialog(MessageResources.message("errorDialog.connectionException.text"),
					MessageResources.message("errorDialog.connectionException.title"));
		} catch (ScmConfigurationException e) {
			logger.error(e);
			Messages.showErrorDialog(MessageResources.message("errorDialog.scmException.text"),
			MessageResources.message("errorDialog.scmException.title"));
		} catch (CollabClientException e) {
			logger.error(e);
			Messages.showErrorDialog(MessageResources.message("errorDialog.errorOccured.text"), 
					MessageResources.message("errorDialog.errorOccured.title"));
		} catch (IOException e) {
			logger.error(e);
			Messages.showErrorDialog(MessageResources.message("errorDialog.ioErrorOccured.text"), 
					MessageResources.message("errorDialog.ioErrorOccured.title"));
		} catch (InterruptedException e) {
			logger.error(e);
			Messages.showErrorDialog(MessageResources.message("errorDialog.uploadInterrupted.text"), 
					MessageResources.message("errorDialog.uploadInterrupted.title"));
		} finally {
			finished();
		}

	}

	/**
	 * Checks if the Code Collaborator server is reachable.
	 * @return <code>true</code> if the server is reachable, <code>false</code> otherwise.
	 */
	private boolean checkConnection() {
		try {
			// Try to fetch the user info from the server. If this succeeds, the server is reachable.
			client.getUser();
			return true;
		} catch (CollabClientException e) {
			// Connection failed.
			return false;
		}
	}

	/**
	 * Attaches local files that are under version control to the given review
	 */
	private void attachControlledFiles(AnActionEvent event, final Review review, final FilePath... files) throws InterruptedException {

		Project project = PluginUtil.getProject(event.getDataContext());

		AddToReviewTask addToReviewTask = new AddToReviewTask(project, review, files);
		addToReviewTask.queue();
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
