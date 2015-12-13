package nl.guno.collab.intellij;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.smartbear.ccollab.datamodel.DataModelException;
import com.smartbear.ccollab.datamodel.User;
import nl.guno.collab.intellij.ui.Notification;

import java.util.List;

class FetchUsersTask extends Task.Modal {

    private static final Logger logger = Logger.getInstance(FetchUsersTask.class.getName());

    private boolean success;

    private final Project project;
    private final User user;

    private List<User> users;

    public FetchUsersTask(Project project, User user) {
        super(project, MessageResources.message("task.createReview.title"), false);

        this.project = project;
        this.user = user;
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {

        progressIndicator.setText(MessageResources.message("progressIndicator.createReview.retrievingUsers"));

        try {
            // Retrieve all users from the collaborator server
            users = user.getEngine().usersPossibleReviewParticipants(null);
        } catch (DataModelException e) {
            logger.warn("Error when retrieving users.", e);
            return;
        }
        success = true;
    }

    @Override
    public void onSuccess() {
        if (!success) {
            new Notification(project, MessageResources.message("task.fetchUsers.errorOccurred.text"),
                    MessageType.ERROR).showBalloon();
        }
    }

    public List<User> getUsers() {
        return users;
    }
}
