package nl.guno.collab.intellij;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.smartbear.ccollab.datamodel.DataModelException;
import com.smartbear.ccollab.datamodel.Review;
import com.smartbear.ccollab.datamodel.User;
import nl.guno.collab.intellij.ui.Notification;

class FetchReviewsTask extends Task.Modal {

    private static final Logger logger = Logger.getInstance(FetchReviewsTask.class.getName());

    private boolean success;

    private final Project project;
    private final User user;

    private Review[] reviews;

    public FetchReviewsTask(Project project, User user) {
        super(project, MessageResources.message("task.selectReview.title"), false);

        this.project = project;
        this.user = user;
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {

        progressIndicator.setText(MessageResources.message("progressIndicator.addToReview.retrievingReviews"));

        try {
            // Retrieve all reviews the user can upload to.
            reviews = user.getReviewsCanUploadChangelists(null);
        } catch (DataModelException e) {
            logger.warn("Error when retrieving reviews.", e);
            return;
        }
        success = true;
    }

    @Override
    public void onSuccess() {
        if (!success) {
            new Notification(project, MessageResources.message("task.fetchReviews.errorOccurred.text"),
                    MessageType.ERROR).showBalloon();
        }
    }

    public Review[] getReviews() {
        return reviews;
    }
}
