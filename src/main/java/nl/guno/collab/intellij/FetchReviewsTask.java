package nl.guno.collab.intellij;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.smartbear.ccollab.datamodel.Review;
import com.smartbear.ccollab.datamodel.User;
import nl.guno.collab.intellij.ui.Notification;

class FetchReviewsTask extends Task.Backgroundable {

    interface Callback {
        void onSuccess(List<Review> reviews);
    }
    
    private boolean success;
    private Callback callback;

    private final Project project;
    private final User user;

    private List<Review> reviews;

    FetchReviewsTask(Project project, User user, Callback callback) {
        super(project, MessageResources.message("task.selectReview.title"), false);

        this.project = project;
        this.user = user;
        this.callback = callback;
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {

        progressIndicator.setText(MessageResources.message("progressIndicator.addToReview.retrievingReviews"));

        reviews = ReviewsFetcher.fetch(user);
        success = true;
    }

    @Override
    public void onSuccess() {
        if (!success) {
            new Notification(project, MessageResources.message("task.fetchReviews.errorOccurred.text"),
                    MessageType.ERROR).showBalloon();
            return;
        }
        
        if (callback != null) {
            callback.onSuccess(reviews);
        }
    }

    @Override
    public boolean shouldStartInBackground() {
        return false;
    }
}
