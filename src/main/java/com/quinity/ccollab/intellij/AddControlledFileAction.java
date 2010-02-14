package com.quinity.ccollab.intellij;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.util.Pair;
import com.smartbear.CollabClientException;
import com.smartbear.beans.ConfigUtils;
import com.smartbear.beans.IGlobalOptions;
import com.smartbear.beans.IScmOptions;
import com.smartbear.ccollab.CommandLineClient;
import com.smartbear.ccollab.client.CollabClientServerConnectivityException;
import com.smartbear.ccollab.client.LoginUtils;
import com.smartbear.ccollab.client.ICollabClientInterface;
import com.smartbear.ccollab.datamodel.Review;
import com.smartbear.ccollab.datamodel.Engine;
import com.smartbear.ccollab.datamodel.User;
import com.smartbear.scm.ScmConfigurationException;
import com.quinity.ccollab.intellij.ui.FileAndReviewSelector;

public class AddControlledFileAction extends AnAction {

	/**
	 * Global and SCM options, created by {@link #init()}
	 */
	static private IGlobalOptions globalOptions;

	/**
	 * SCM options, created by {@link #init()}
	 */
	static IScmOptions scmOptions;

	/**
	 * Interface to user for prompting, etc...
	 * created by {@link #init()}
	 */
	static ICollabClientInterface clientInterface;
	
	/**
	 * Connection to Code Collaborator server
	 * created by {@link #init()}
	 */
	static Engine engine;
	
	/**
	 * Currently logged-in user
	 * created by {@link #init()}
	 */
	static User user;
	
	private static Logger logger = Logger.getInstance(AddControlledFileAction.class.getName());


	@Override
	public void actionPerformed(AnActionEvent event) {

		try {
			init();

			// Retrieve the current file(s)
			FilePath[] files = PluginUtil.getSelectedFilePaths(event);


            List<Pair<FilePath, Boolean>> fileList = new ArrayList<Pair<FilePath, Boolean>>();
            for (FilePath filePath : files) {
                fileList.add(Pair.create(filePath, Boolean.TRUE));
            }
            
			Project project = PluginUtil.getProject(event.getDataContext());

			// Retrieve the reviews the user can upload to
			FetchReviewsTask fetchReviewsTask = new FetchReviewsTask(project, user);
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
				Review review = engine.reviewById(selectedReviewId);

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
	 * Attaches local files that are under version control to the given review
	 */
	private void attachControlledFiles(AnActionEvent event, final Review review, final FilePath... files) throws InterruptedException {

		Project project = PluginUtil.getProject(event.getDataContext());

		AddToReviewTask addToReviewTask = new AddToReviewTask(project, review, user, files);
		addToReviewTask.queue();
	}

	private static void init() throws CollabClientException, IOException {
		// If we've already initialized, don't do it again.
		if ( engine != null ) {
			return;
		}
		
		//load options from config files
		com.smartbear.collections.Pair<IGlobalOptions, IScmOptions> configOptions = ConfigUtils.loadConfigFiles();
		globalOptions = configOptions.getA();
		scmOptions = configOptions.getB();
		
		//initialize client interface
		clientInterface = new CommandLineClient(globalOptions);
		
		//connect to server and log in (throws exception if authentication fails, can't find server, etc...)
		user = LoginUtils.login(globalOptions, clientInterface);
		engine = user.getEngine();
	}
	
	/**
	 * Called to clean up a previous call to <code>init()</code>.
	 * <p/>
	 * <b>THIS IS CRITICAL</b>.  If you do not close out your <code>CollabClientConnection</code>
	 * object, data might not be flushed out to the server!
	 */
	private void finished() {
		if (engine != null) {
			engine.close(true);
		}
	}

}
