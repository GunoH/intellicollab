package nl.guno.ccollab.intellij;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.jetbrains.annotations.NotNull;

import com.intellij.history.LocalHistory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
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
import nl.guno.ccollab.intellij.Environment.SVNNotAvailableException;
import nl.guno.ccollab.intellij.Environment.SVNWrongVersionException;
import nl.guno.ccollab.intellij.ui.CreateReviewDialog;
import nl.guno.ccollab.intellij.ui.FileAndReviewSelector;
import nl.guno.ccollab.intellij.ui.Notification;

public class AddControlledFileAction extends IntelliCcollabAction {

    private static final Logger logger = Logger.getInstance(AddControlledFileAction.class.getName());
    private Project project;

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {

        try {
			project = event.getData(LangDataKeys.PROJECT);

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
                new Notification(project, MessageResources.message("action.error.svnNotAvailable.text",
                        Environment.REQUIRED_SVN_VERSION),
                        MessageType.ERROR).showBalloon().addToEventLog();
                return;
            } catch (SVNWrongVersionException e) {
                new Notification(project, MessageResources.message("action.error.svnWrongVersion.text",
                        Environment.REQUIRED_SVN_VERSION),
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

            Review[] reviews = fetchReviewsTask.getReviews();

            if (reviews == null || reviews.length == 0) {
                logger.debug("No reviews found");
	            new Notification(
			            project,
			            MessageResources.message("task.addFilesToReview.noReviews.text"),
			            MessageType.WARNING)
			            .setHyperlinkListener(new HyperlinkListener() {
				            @Override
				            public void hyperlinkUpdate(@NotNull HyperlinkEvent e) {
					            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
						            // Open create review dialog
						            new CreateReviewAction().invoke(project);
					            }
				            }
			            })
			            .showBalloon();


                return;
            }

            String changesetName = PluginUtil.getSelectedChangesetName(event);
            if (changesetName == null) {
                // No changeset selected. Use changeset the first selected file is part of.
                changesetName = PluginUtil.getChangesetNameOfFirstSelectedFile(event);
            }
            if (changesetName == null) {
                // The selected file is not in a changeset. Use the active changeset.
                changesetName = PluginUtil.getActiveChangesetName(event);
            }

            FileAndReviewSelector fileAndReviewSelector = new FileAndReviewSelector(fileList, reviews, project,
                    changesetName);
            fileAndReviewSelector.pack();
            fileAndReviewSelector.show();

            if (CreateReviewDialog.OK_EXIT_CODE != fileAndReviewSelector.getExitCode()) {
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

            Integer selectedReviewId = fileAndReviewSelector.getSelectedReviewId();

            if (selectedReviewId != null) {
                // Retrieve the selected review.
                Review review = engine.reviewById(selectedReviewId);

                // Add the current file to the selected review.
                attachControlledFiles(event, review, files);
	            
	            // Add a label to the local history.
	            LocalHistory.getInstance().putSystemLabel(project, 
			            MessageResources.message("localhistory.label.filesuploaded", 
					            review.getId().toString(), 
					            review.getTitle())); 
            }
        } catch (CollabClientServerConnectivityException e) {
            logger.warn(e);
            new Notification(project, MessageResources.message("action.addControlledFile.connectionException.text"),
                    MessageType.ERROR).showBalloon();

        } catch (ScmConfigurationException e) {
            logger.warn(e);
            new Notification(project, MessageResources.message("action.addControlledFile.scmException.text"),
                    MessageType.ERROR).showBalloon();
        } catch (CollabClientException e) {
            logger.warn(e);
            new Notification(project, MessageResources.message("action.addControlledFile.errorOccurred.text"),
                    MessageType.ERROR).showBalloon();
        } catch (IOException e) {
            logger.warn(e);
            new Notification(project, MessageResources.message("action.addControlledFile.ioErrorOccurred.text"),
                    MessageType.ERROR).showBalloon();
        } catch (InterruptedException e) {
	        new Notification(project, MessageResources.message("action.addControlledFile.errorOccurred.text"),
			        MessageType.ERROR).showBalloon();
        } finally {
            finished();
        }

    }

    File[] getCurrentlySelectedFiles(AnActionEvent event) {
        return PluginUtil.getSelectedFiles(event);
    }

    /**
     * Attaches local files that are under version control to the given review
     */
    private void attachControlledFiles(AnActionEvent event, final Review review, final File... files) {

		project = event.getData(LangDataKeys.PROJECT);

        if (project == null) {
            logger.error("project is null", new Throwable());
            return;
        }

        AddToReviewTask addToReviewTask = new AddToReviewTask(project, review, user, files);
        addToReviewTask.queue();
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
		project = event.getData(LangDataKeys.PROJECT);
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
