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

	void invoke(final Project project) {
		try {

			if (!init(project)) {
				return;
			}

			if (Context.engine == null) {
				return;
			}

			// Retrieve the available users
            FetchUsersTask fetchUsersTask = new FetchUsersTask(project, Context.user, new FetchUsersTask.Callback() {
                @Override
                public void onSuccess(List<User> users) {
                    usersFetched(users, project);
                }
            });
			fetchUsersTask.queue();

		} catch (CollabClientServerConnectivityException e) {
			logger.warn(e);
			new Notification(project, MessageResources.message("action.createReview.connectionException.text"),
					MessageType.ERROR).showBalloon(new HyperlinkListener() {
				@Override
				public void hyperlinkUpdate(HyperlinkEvent e) {
					if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        PluginUtil.openLogDirectory();
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
                        PluginUtil.openLogDirectory();
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
                        PluginUtil.openLogDirectory();
					}
				}
			});
		} catch (InterruptedException e) {
			new Notification(project, MessageResources.message("action.createReview.errorOccurred.text"),
					MessageType.ERROR).showBalloon(new HyperlinkListener() {
				@Override
				public void hyperlinkUpdate(HyperlinkEvent e) {
					if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        PluginUtil.openLogDirectory();
					}
				}
			});
		} finally {
			finished();
		}
	}

    private void usersFetched(final List<User> users, final Project project) {

        // Retrieve the available groups
        FetchGroupsTask fetchGroupsTask = new FetchGroupsTask(project, Context.user, new FetchGroupsTask.Callback() {
            @Override
            public void onSuccess(List<GroupDescription> groups) {
                groupsFetched(users, groups, project);
            }
        });
        fetchGroupsTask.queue();
    }

    private void groupsFetched(final List<User> users, final List<GroupDescription> groups, final Project project) {

        // Retrieve the available metadata
        FetchMetadataTask fetchMetadataTask = new FetchMetadataTask(project, Context.user, new FetchMetadataTask.Callback() {
            @Override
            public void onSuccess(Metadata metadata) {
                metadataFetched(metadata, users, groups, project);
            }
        });
        fetchMetadataTask.queue();
    }

    private void metadataFetched(Metadata metadata, List<User> users, List<GroupDescription> groups, Project project) {
        CreateReviewDialog createReviewDialog = new CreateReviewDialog(metadata, users, groups, Context.user,
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

        Map<MetaDataDescription, Object> metadataMap = new HashMap<>();
        metadataMap.put(metadata.getOverview(), createReviewDialog.getEnteredOverview());
        metadataMap.put(metadata.getBugzillaInstantie(), createReviewDialog.getSelectedBugzillaInstantie());
        metadataMap.put(metadata.getBugzillaNummer(), createReviewDialog.getEnteredBugzillanummer());
        metadataMap.put(metadata.getFo(), createReviewDialog.getEnteredFO());
        metadataMap.put(metadata.getTo(), createReviewDialog.getEnteredTO());
        metadataMap.put(metadata.getRnfo(), createReviewDialog.getEnteredRNFO());
        metadataMap.put(metadata.getRnto(), createReviewDialog.getEnteredRNTO());
        metadataMap.put(metadata.getRnMigratiePad(), createReviewDialog.getEnteredRNMigratiePad());

        CreateReviewTask createReviewTask = new CreateReviewTask(project, Context.user, selectedGroup, enteredTitle,
                uploadRestricted, reviewAccess, selectedAuthor, selectedReviewer, selectedObserver, metadataMap);
        createReviewTask.queue();

    }
}
