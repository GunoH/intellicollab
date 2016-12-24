package nl.guno.collab.intellij;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.jetbrains.annotations.NotNull;

import com.intellij.history.LocalHistory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeList;
import com.intellij.openapi.vcs.changes.ChangesUtil;
import com.smartbear.CollabClientException;
import com.smartbear.ccollab.client.CollabClientServerConnectivityException;
import com.smartbear.ccollab.datamodel.Review;
import com.smartbear.scm.ScmConfigurationException;
import nl.guno.collab.intellij.Environment.SVNNotAvailableException;
import nl.guno.collab.intellij.ui.FileAndReviewSelector;
import nl.guno.collab.intellij.ui.Notification;

public class AddControlledFileAction extends IntelliCollabAction {

    private static final Logger logger = Logger.getInstance(AddControlledFileAction.class.getName());
    private Project project;

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {

        try {
			project = event.getData(CommonDataKeys.PROJECT);

            if (project == null) {
                logger.error("project is null", new Throwable());
                return;
            }

            if (!init(project)) {
	            return;
            }

            try {
                new Environment().checkSVNExecutable();
            } catch (SVNNotAvailableException e) {
                new Notification(project, MessageResources.message("action.error.svnNotAvailable.text"),
                        MessageType.ERROR).showBalloon().addToEventLog();
                return;
            }

	        if (engine == null) {
                return;
            }

            // Save all changes to disk.
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                    FileDocumentManager.getInstance().saveAllDocuments();
                }
            });

            // Retrieve the current file(s)
            File[] files = getCurrentlySelectedFiles(event);


            List<Pair<File, Boolean>> fileList = new ArrayList<Pair<File, Boolean>>();
            for (File file : files) {
                fileList.add(Pair.create(file, Boolean.TRUE));
            }


            // Retrieve the reviews the user can upload to
            FetchReviewsTask fetchReviewsTask = new FetchReviewsTask(project, user);
            fetchReviewsTask.queue();

            List<Review> reviews = fetchReviewsTask.getReviews();

            if (reviews == null || reviews.isEmpty()) {
                logger.debug("No reviews found");
	            new Notification(
			            project,
			            MessageResources.message("task.addFilesToReview.noReviews.text"),
			            MessageType.WARNING)
			            .showBalloon(new HyperlinkListener() {
                            @Override
                            public void hyperlinkUpdate(HyperlinkEvent e) {
                                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                                    // Open create review dialog
                                    new CreateReviewAction().invoke(project);
                                }
                            }
                        });


                return;
            }

            String changesetName = PluginUtil.getSelectedChangesetName(event);
            if (changesetName == null) {
                // No changeset selected. Use changeset the first selected file is part of.
                changesetName = PluginUtil.getChangesetNameOfFirstSelectedFile(event);
            }
            if (changesetName == null) {
                // The selected file is not in a changeset. Use the active changeset.
                changesetName = PluginUtil.getActiveChangesetName(project);
            }

            FileAndReviewSelector fileAndReviewSelector = new FileAndReviewSelector(fileList, reviews, project,
                    changesetName);
            fileAndReviewSelector.pack();
            fileAndReviewSelector.show();

            if (DialogWrapper.OK_EXIT_CODE != fileAndReviewSelector.getExitCode()) {
                logger.debug("User pressed cancel.");
                return;
            }

            files = fileAndReviewSelector.retrieveSelectedFiles();

            if (files.length == 0) {
                logger.debug("No files selected.");
                new Notification(
                        project,
                        MessageResources.message("task.addFilesToReview.noFilesSelected.text"),
                        MessageType.ERROR).showBalloon();
                return;
            }

            Review selectedReview = fileAndReviewSelector.getSelectedReview();
            if (selectedReview != null) {
                // Add the current file to the selected review.
                attachControlledFiles(event, selectedReview, files);

	            // Add a label to the local history.
                LocalHistory.getInstance().putSystemLabel(project,
                        MessageResources.message("localhistory.label.filesuploaded",
                                selectedReview.getId().toString(),
                                selectedReview.getTitle()));
            }
        } catch (CollabClientServerConnectivityException e) {
            logger.warn(e);
            new Notification(project, MessageResources.message("action.addControlledFile.connectionException.text"),
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
            new Notification(project, MessageResources.message("action.addControlledFile.scmException.text"),
                    MessageType.ERROR).showBalloon();
        } catch (CollabClientException e) {
            logger.warn(e);
            new Notification(project, MessageResources.message("action.addControlledFile.errorOccurred.text"),
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
            new Notification(project, MessageResources.message("action.addControlledFile.ioErrorOccurred.text"),
                    MessageType.ERROR).showBalloon(new HyperlinkListener() {
                @Override
                public void hyperlinkUpdate(HyperlinkEvent e) {
                    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        PluginUtil.openLogDirectory();
                    }
                }
            });
        } catch (InterruptedException e) {
	        new Notification(project, MessageResources.message("action.addControlledFile.errorOccurred.text"),
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

    @NotNull
    File[] getCurrentlySelectedFiles(@NotNull AnActionEvent event) {
        return PluginUtil.getSelectedFiles(event);
    }

    /**
     * Attaches local files that are under version control to the given review
     */
    private void attachControlledFiles(@NotNull AnActionEvent event, final Review review, final File... files) {

		project = event.getData(CommonDataKeys.PROJECT);

        if (project == null) {
            logger.error("project is null", new Throwable());
            return;
        }

        AddToReviewTask addToReviewTask = new AddToReviewTask(project, review, user, files);
        addToReviewTask.queue();
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
		project = event.getData(CommonDataKeys.PROJECT);
        Change[] changes = event.getData(VcsDataKeys.CHANGES);

        boolean enabled = false;
        if (project != null) {
            ChangeList changelist = ChangesUtil.getChangeListIfOnlyOne(project, changes);
            if (changes != null && changelist != null) {
                for (Change change : changes) {
                    AbstractVcs abstractvcs = ChangesUtil.getVcsForChange(change, project);
                    if (abstractvcs != null && abstractvcs.getCheckinEnvironment() != null) {
                        enabled = true;
                    }
                }
            }
        }
        event.getPresentation().setEnabled(enabled);
    }
}
