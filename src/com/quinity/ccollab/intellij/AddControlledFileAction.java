package com.quinity.ccollab.intellij;

import java.io.IOException;

import org.eclipse.core.runtime.NullProgressMonitor;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.smartbear.CollabClientException;
import com.smartbear.beans.ConfigUtils;
import com.smartbear.beans.GlobalOptions;
import com.smartbear.beans.ISettableGlobalOptions;
import com.smartbear.ccollab.CommandLineClient;
import com.smartbear.ccollab.client.CollabClientConnection;
import com.smartbear.ccollab.client.CollabClientServerConnectivityException;
import com.smartbear.ccollab.datamodel.Review;
import com.smartbear.scm.ScmConfigurationException;

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

			Project project = PluginUtil.getProject(event.getDataContext());

			FetchReviewsTask fetchReviewsTask = new FetchReviewsTask(project, client);
			fetchReviewsTask.queue();

			Integer selectedReviewId = fetchReviewsTask.getSelectedReviewId();
			
			if (selectedReviewId != null) {
				// Retrieve the selected review.
				Review review = client.getEngine(new NullProgressMonitor()).reviewById(selectedReviewId);

				// Retrieve the current file(s)
				VirtualFile[] files = PluginUtil.getCurrentVirtualFiles(event.getDataContext());

				// Add the current file to the selected review.
				attachControlledFiles(event, review, files);
			}
		} catch (CollabClientServerConnectivityException e) {
			logger.error(e);
			Messages.showErrorDialog("A connection error occured when trying to reach Code Collaborator server.",
					"Connection Exception");
		} catch (ScmConfigurationException e) {
			logger.error(e);
			Messages.showErrorDialog("Something went wrong when determining which SCM system to use.",
			"SCM Exception");
		} catch (CollabClientException e) {
			logger.error(e);
			Messages.showErrorDialog("An error occured.", "General error");
		} catch (IOException e) {
			logger.error(e);
			Messages.showErrorDialog("An IO error occured.", "IO Error");
		} catch (InterruptedException e) {
			logger.error(e);
			Messages.showErrorDialog("The upload process was interrupted.", "Interrupted");
		} finally {
			finished();
		}

	}

	/**
	 * Attaches local files that are under version control to the given review
	 */
	private void attachControlledFiles(AnActionEvent event, final Review review, final VirtualFile... virtualFiles) throws InterruptedException {

		Project project = PluginUtil.getProject(event.getDataContext());

		AddToReviewTask addToReviewTask = new AddToReviewTask(project, review, virtualFiles);
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
