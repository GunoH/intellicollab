package nl.guno.collab.intellij;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.smartbear.CollabClientException;
import com.smartbear.beans.NullAskUser;
import com.smartbear.ccollab.datamodel.Changelist;
import com.smartbear.ccollab.datamodel.Engine;
import com.smartbear.ccollab.datamodel.Review;
import com.smartbear.ccollab.datamodel.Scm;
import com.smartbear.ccollab.datamodel.User;
import com.smartbear.scm.IScmClientConfiguration;
import com.smartbear.scm.IScmLocalCheckout;
import com.smartbear.scm.ScmChangeset;
import com.smartbear.scm.ScmConfigurationException;
import com.smartbear.scm.ScmUtils;
import com.smartbear.scm.impl.subversion.SubversionSystem;
import nl.guno.collab.intellij.ui.Notification;

class AddToReviewTask extends Task.Backgroundable {

    private static final Logger logger = Logger.getInstance(AddToReviewTask.class.getName());

    private final Project project;
    private final Review review;

    private final File[] files;

    private final User user;

    private boolean success;
    private String errorMessage;

    private final IntelliCollabApplicationComponent component =
            ApplicationManager.getApplication().getComponent(IntelliCollabApplicationComponent.class);

    public AddToReviewTask(Project project, Review review, User user, File... files) {
        super(project, MessageResources.message("task.addFilesToReview.title"), true);

        this.project = project;
        this.review = review;
        this.user = user;
        this.files = files;
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {

        try {
            progressIndicator.setText(MessageResources.message("progressIndicator.addToReview.preparing"));

            // Create the SCM ChangeSet object to upload.  You can attach
            // many types of objects here from uncontrolled files as in this
            // example to controlled files (both local and server-side-only)
            // to SCM-specific atomic changelists (e.g. with Perforce and Subversion).
            logger.debug("Creating SCM Changeset...");
            ScmChangeset changeset = new ScmChangeset();


            IScmClientConfiguration clientConfig;
            IScmLocalCheckout scmFile = null;


            if (files.length == 0) {
                success = true;
                return;
            }

            int fileCounter = 0;
            for (File file : files) {

                progressIndicator.checkCanceled();

                progressIndicator.setText2(MessageResources.message("progressIndicator.addToReview.fileUploadProgress", file.getName(),
                        ++fileCounter, files.length));
                logger.debug("Working with file: " + file.getPath());


                // Create the SCM object representing a local file under version control.
                // We assume the local SCM is already configured properly.
                logger.debug("Loading SCM File object...");
                clientConfig = ScmUtils.requireScm(file, AddControlledFileAction.scmOptions, NullAskUser.INSTANCE, 
                        new NullProgressMonitor(), SubversionSystem.INSTANCE);
                scmFile = clientConfig.getLocalCheckout(file, new NullProgressMonitor());
                if (scmFile != null) {
                    changeset.addLocalCheckout(scmFile, true, new NullProgressMonitor());
                }
            }

            if (scmFile == null) {
                errorMessage = MessageResources.message("task.addFilesToReview.noFilesCouldBeAdded.text");
                return;
            }

            progressIndicator.checkCanceled();

            progressIndicator.setText(MessageResources.message("progressIndicator.addToReview.uploading"));
            progressIndicator.setText2("");

            // Upload this changeset to Collaborator.  Another form of this
            // uploader lets us specify even more information; this form extracts it
            // automatically from the files in the changeset.
            logger.debug("Uploading SCM Changeset...");
            Engine engine = AddControlledFileAction.engine;
            Scm scm = engine.scmByLocalCheckout(scmFile);            // select the SCM system that matches the client configuration

            Scm.ChangesetParameters params = new Scm.ChangesetParameters(changeset, "Local changes uploaded from IntelliJ IDEA");

            Changelist changelist = scm.uploadChangeset(params, new NullProgressMonitor());

            progressIndicator.setText(MessageResources.message("progressIndicator.addToReview.attaching", review.getId().toString()));

            progressIndicator.checkCanceled();

            // The changelist has been uploaded but it hasn't been attached
            // to any particular review!  This two-step process not only allows for
            // a changelist to be part of more than one review, but also means that
            // if there's any error in uploading the changelist the review hasn't
            // changed at all so no one will be affected.
            review.addChangelist(changelist, user);

            success = true;
        } catch (ScmConfigurationException e) {
            logger.warn(e);
            errorMessage = MessageResources.message("task.addFilesToReview.cannotDetermineSCMSystem.text");
        } catch (CollabClientException e) {
            logger.warn(e);
            errorMessage = MessageResources.message("task.addFilesToReview.errorOccurred.text");
        } catch (IOException e) {
            logger.warn(e);
            errorMessage = MessageResources.message("task.addFilesToReview.ioErrorOccurred.text");
        }
    }

    @Override
    public void onSuccess() {
        if (success) {
            new Notification(project, MessageResources.message("task.addFilesToReview.filesHaveBeenUploaded.text",
                    files.length, review.getId().toString(), review.getTitle(), component.getServerURL()), MessageType.INFO).showBalloon().addToEventLog();
        } else if (errorMessage != null) {
            new Notification(project, errorMessage, MessageType.WARNING).showBalloon().addToEventLog();
        }
    }

    @Override
    public void onCancel() {
        if (success) {
            new Notification(project, MessageResources.message("task.addFilesToReview.filesHaveBeenUploaded.text",
                    files.length, review.getId().toString(), review.getTitle(), component.getServerURL()),
                    MessageType.INFO).showBalloon().addToEventLog();
        } else {
            new Notification(project, MessageResources.message("task.addFilesToReview.cancelled.text"),
                    MessageType.ERROR).showBalloon();
        }
    }

    @Override
    public boolean shouldStartInBackground() {
        return false;
    }
}
