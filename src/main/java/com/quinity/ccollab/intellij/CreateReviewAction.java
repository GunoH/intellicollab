package com.quinity.ccollab.intellij;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.quinity.ccollab.intellij.ui.CreateReviewDialog;
import com.smartbear.CollabClientException;
import com.smartbear.ccollab.client.CollabClientServerConnectivityException;
import com.smartbear.ccollab.datamodel.IDropDownItem;
import com.smartbear.ccollab.datamodel.MetaDataDescription;
import com.smartbear.ccollab.datamodel.ReviewAccess;
import com.smartbear.ccollab.datamodel.User;
import com.smartbear.scm.ScmConfigurationException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CreateReviewAction extends IntelliCcollabAction {

	private static Logger logger = Logger.getInstance(CreateReviewAction.class.getName());


	@Override
	public void actionPerformed(AnActionEvent event) {

		try {
			Project project = PluginUtil.getProject(event.getDataContext());

			init(project);

			if (engine == null) {
				return;
			}

			// Retrieve the avaliable users
			FetchUsersTask fetchUsersTask = new FetchUsersTask(project, user);
			fetchUsersTask.queue();

			User[] users = fetchUsersTask.getUsers();

			// Retrieve the avaliable metadata
			MetaDataDescription overview = user.getEngine().metaDataDescriptionsFind(1, "AdminReviewFields", "Overview")[0];
			MetaDataDescription bugzillaInstantie = user.getEngine().metaDataDescriptionsFind(1, "AdminReviewFields", "Bugzilla-instantie")[0];
			MetaDataDescription bugzillanummer = user.getEngine().metaDataDescriptionsFind(1, "AdminReviewFields", "Bugzillanummer")[0];
			
			IDropDownItem[] bugzillaInstanties = bugzillaInstantie.getDropDownItems(true);

			CreateReviewDialog createReviewDialog = new CreateReviewDialog(users, bugzillaInstanties, user);
			createReviewDialog.pack();
			createReviewDialog.setVisible(true);

			if (!createReviewDialog.isOkPressed()) {
				logger.debug("User pressed cancel.");
				return;
			}

			String enteredTitle = createReviewDialog.getEnteredTitle();
			boolean uploadRestricted = createReviewDialog.isUploadRestricted();
			ReviewAccess reviewAccess = createReviewDialog.getReviewAccess();
			User selectedAuthor = createReviewDialog.getSelectedAuthor();
			User selectedReviewer = createReviewDialog.getSelectedReviewer();
			User selectedObserver = createReviewDialog.getSelectedObserver();

			Map<MetaDataDescription, Object> metadata = new HashMap<MetaDataDescription, Object>(); 
			metadata.put(overview, createReviewDialog.getEnteredOverview());
			metadata.put(bugzillaInstantie, createReviewDialog.getSelectedBugzillaInstantie());
			metadata.put(bugzillanummer, createReviewDialog.getEnteredBugzillanummer());

			CreateReviewTask createReviewTask = new CreateReviewTask(project, user, enteredTitle, uploadRestricted, 
					reviewAccess, selectedAuthor, selectedReviewer, selectedObserver, metadata);
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
}
