package com.quinity.ccollab.intellij;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeList;
import com.intellij.openapi.vcs.changes.ChangesUtil;
import com.quinity.ccollab.intellij.ui.FileAndReviewSelector;
import com.smartbear.CollabClientException;
import com.smartbear.ccollab.client.CollabClientServerConnectivityException;
import com.smartbear.ccollab.datamodel.Review;
import com.smartbear.scm.ScmConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddControlledFileAction extends IntelliCcollabAction {

    private static Logger logger = Logger.getInstance(AddControlledFileAction.class.getName());


    @Override
    public void actionPerformed(AnActionEvent event) {

        try {
            Project project = PluginUtil.getProject(event.getDataContext());

            init(project);

            if (engine == null) {
                return;
            }

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

            if (reviews == null) {
                logger.debug("No reviews found");
                return;
            }

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
        } catch (InterruptedException e) {
            logger.warn(e);
            Messages.showErrorDialog(MessageResources.message("errorDialog.uploadInterrupted.text"),
                    MessageResources.message("errorDialog.uploadInterrupted.title"));
        } finally {
            finished();
        }

    }

    protected File[] getCurrentlySelectedFiles(AnActionEvent event) {
        return PluginUtil.getSelectedFiles(event);
    }

    /**
     * Attaches local files that are under version control to the given review
     */
    private void attachControlledFiles(AnActionEvent event, final Review review, final File... files) throws InterruptedException {

        Project project = PluginUtil.getProject(event.getDataContext());

        AddToReviewTask addToReviewTask = new AddToReviewTask(project, review, user, files);
        addToReviewTask.queue();
    }

    public void update(AnActionEvent event) {
        Project project = PluginUtil.getProject(event.getDataContext());
        Change[] changes = PluginUtil.getChanges(event.getDataContext());

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
