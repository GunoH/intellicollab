package nl.guno.collab.intellij;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageType;
import com.smartbear.CollabClientException;
import com.smartbear.ccollab.client.CollabClientServerConnectivityException;
import com.smartbear.ccollab.datamodel.GroupDescription;
import com.smartbear.ccollab.datamodel.MetaDataDescription;
import com.smartbear.ccollab.datamodel.User;
import com.smartbear.ccollab.datamodel.client.ReviewAccess;
import com.smartbear.scm.ScmConfigurationException;
import nl.guno.collab.intellij.ui.CreateReviewDialog;
import nl.guno.collab.intellij.ui.Notification;

public class CreateReviewAction extends IntelliCollabAction {

    private static final Logger logger = Logger.getInstance(CreateReviewAction.class.getName());


	@Override
	public void actionPerformed(@NotNull AnActionEvent event) {
		Project project = event.getData(CommonDataKeys.PROJECT);
		if (project == null) {
			// Can happen if no project was loaded.
			return;
		}

		invoke(project);
	}

	void invoke(Project project) {
		try {

			if (!init(project)) {
				return;
			}

			if (engine == null) {
				return;
			}

			// Retrieve the available users
			FetchUsersTask fetchUsersTask = new FetchUsersTask(project, user);
			fetchUsersTask.queue();

			List<User> users = fetchUsersTask.getUsers();

			// Retrieve the available groups
			FetchGroupsTask fetchGroupsTask = new FetchGroupsTask(project, user);
			fetchGroupsTask.queue();

			List<GroupDescription> groups = fetchGroupsTask.getGroups();

			// Retrieve the available metadata
			FetchMetadataTask fetchMetadataTask = new FetchMetadataTask(project, user);
			fetchMetadataTask.queue();

			CreateReviewDialog createReviewDialog = new CreateReviewDialog(fetchMetadataTask, users, groups, user,
					project, PluginUtil.getActiveChangesetName(project));
			createReviewDialog.pack();
			createReviewDialog.show();

			if (DialogWrapper.OK_EXIT_CODE != createReviewDialog.getExitCode()) {
				logger.debug("User pressed cancel.");
				return;
			}

			GroupDescription selectedGroup = createReviewDialog.getSelectedGroup();
			String enteredTitle = createReviewDialog.getEnteredTitle();
			boolean uploadRestricted = createReviewDialog.isUploadRestricted();
			ReviewAccess reviewAccess = createReviewDialog.getReviewAccess();
			User selectedAuthor = createReviewDialog.getSelectedAuthor();
			User selectedReviewer = createReviewDialog.getSelectedReviewer();
			User selectedObserver = createReviewDialog.getSelectedObserver();

			Map<MetaDataDescription, Object> metadata = new HashMap<MetaDataDescription, Object>();
			metadata.put(fetchMetadataTask.getOverview(), createReviewDialog.getEnteredOverview());
			metadata.put(fetchMetadataTask.getBugzillaInstantie(), createReviewDialog.getSelectedBugzillaInstantie());
			metadata.put(fetchMetadataTask.getBugzillaNummer(), createReviewDialog.getEnteredBugzillanummer());
			metadata.put(fetchMetadataTask.getFo(), createReviewDialog.getEnteredFO());
			metadata.put(fetchMetadataTask.getTo(), createReviewDialog.getEnteredTO());
			metadata.put(fetchMetadataTask.getRnfo(), createReviewDialog.getEnteredRNFO());
			metadata.put(fetchMetadataTask.getRnto(), createReviewDialog.getEnteredRNTO());
			metadata.put(fetchMetadataTask.getRnMigratiePad(), createReviewDialog.getEnteredRNMigratiePad());

			CreateReviewTask createReviewTask = new CreateReviewTask(project, user, selectedGroup, enteredTitle,
					uploadRestricted, reviewAccess, selectedAuthor, selectedReviewer, selectedObserver, metadata);
			createReviewTask.queue();

		} catch (CollabClientServerConnectivityException e) {
			logger.warn(e);
			new Notification(project, MessageResources.message("action.createReview.connectionException.text"),
					MessageType.ERROR).showBalloon(new HyperlinkListener() {
				@Override
				public void hyperlinkUpdate(HyperlinkEvent e) {
					if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
						showLog();
					}
				}
			});
		} catch (ScmConfigurationException e) {
			logger.warn(e);
			new Notification(project, MessageResources.message("action.createReview.scmException.text"),
					MessageType.ERROR).showBalloon();
		} catch (CollabClientException e) {
			logger.warn(e);
			new Notification(project, MessageResources.message("action.createReview.errorOccurred.text"),
					MessageType.ERROR).showBalloon(new HyperlinkListener() {
				@Override
				public void hyperlinkUpdate(HyperlinkEvent e) {
					if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
						showLog();
					}
				}
			});
		} catch (IOException e) {
			logger.warn(e);
			new Notification(project, MessageResources.message("action.createReview.ioErrorOccurred.text"),
					MessageType.ERROR).showBalloon(new HyperlinkListener() {
				@Override
				public void hyperlinkUpdate(HyperlinkEvent e) {
					if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
						showLog();
					}
				}
			});
		} catch (InterruptedException e) {
			new Notification(project, MessageResources.message("action.createReview.errorOccurred.text"),
					MessageType.ERROR).showBalloon(new HyperlinkListener() {
				@Override
				public void hyperlinkUpdate(HyperlinkEvent e) {
					if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
						showLog();
					}
				}
			});
		} finally {
			finished();
		}
	}
}
