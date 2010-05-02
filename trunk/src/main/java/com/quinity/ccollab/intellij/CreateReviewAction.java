package com.quinity.ccollab.intellij;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.quinity.ccollab.intellij.ui.CreateReviewDialog;
import com.smartbear.CollabClientException;
import com.smartbear.beans.ConfigUtils;
import com.smartbear.beans.IGlobalOptions;
import com.smartbear.beans.IScmOptions;
import com.smartbear.ccollab.CommandLineClient;
import com.smartbear.ccollab.client.CollabClientServerConnectivityException;
import com.smartbear.ccollab.client.ICollabClientInterface;
import com.smartbear.ccollab.client.LoginUtils;
import com.smartbear.ccollab.datamodel.Engine;
import com.smartbear.ccollab.datamodel.ReviewAccess;
import com.smartbear.ccollab.datamodel.User;
import com.smartbear.scm.ScmConfigurationException;

import java.io.IOException;

public class CreateReviewAction extends AnAction {

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
	
	private static Logger logger = Logger.getInstance(CreateReviewAction.class.getName());


	@Override
	public void actionPerformed(AnActionEvent event) {

		try {
			init();

			Project project = PluginUtil.getProject(event.getDataContext());

			User[] users = user.getEngine().usersPossibleReviewParticipants(null);
			
			CreateReviewDialog createReviewDialog = new CreateReviewDialog(users, user);
			createReviewDialog.pack();
			createReviewDialog.setVisible(true);

			if (!createReviewDialog.isOkPressed()) {
				logger.debug("User pressed cancel.");
				return;
			}

			String enteredTitle = createReviewDialog.getEnteredTitle();
			String enteredOverview = createReviewDialog.getEnteredOverview();
			boolean uploadRestricted = createReviewDialog.isUploadRestricted();
			ReviewAccess reviewAccess = createReviewDialog.getReviewAccess();
			User selectedAuthor = createReviewDialog.getSelectedAuthor();
			User selectedReviewer = createReviewDialog.getSelectedReviewer();
			User selectedObserver = createReviewDialog.getSelectedObserver();
			
			CreateReviewTask createReviewTask = new CreateReviewTask(project, user, enteredTitle, enteredOverview, 
					uploadRestricted, reviewAccess, selectedAuthor, selectedReviewer, selectedObserver);
			createReviewTask.queue();

		} catch (CollabClientServerConnectivityException e) {
			logger.warn(e);
			Messages.showErrorDialog(MessageResources.message("errorDialog.connectionException.text"),
					MessageResources.message("errorDialog.connectionException.title"));
		} catch (ScmConfigurationException e) {
			logger.warn(e);
			Messages.showErrorDialog(MessageResources.message("errorDialog.scmException.text"),
			MessageResources.message("errorDialog.scmException.title"));
		} catch (CollabClientException e) {
			logger.warn(e);
			Messages.showErrorDialog(MessageResources.message("errorDialog.errorOccured.text"), 
					MessageResources.message("errorDialog.errorOccured.title"));
		} catch (IOException e) {
			logger.warn(e);
			Messages.showErrorDialog(MessageResources.message("errorDialog.ioErrorOccured.text"), 
					MessageResources.message("errorDialog.ioErrorOccured.title"));
		} finally {
			finished();
		}

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
