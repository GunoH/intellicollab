package nl.guno.collab.intellij;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.smartbear.ccollab.datamodel.GroupDescription;
import com.smartbear.ccollab.datamodel.User;
import nl.guno.collab.intellij.ui.Notification;

class FetchGroupsTask extends Task.Modal {

    private boolean success;

    private final Project project;
    private final User user;

    private List<GroupDescription> groups;

    FetchGroupsTask(Project project, User user) {
        super(project, MessageResources.message("task.createReview.title"), false);

        this.project = project;
        this.user = user;
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {

        progressIndicator.setText(MessageResources.message("progressIndicator.createReview.retrievingGroups"));

        groups = GroupsFetcher.fetch(user);
    
        success = true;
    }

    @Override
    public void onSuccess() {
        if (!success) {
            new Notification(project, MessageResources.message("task.fetchGroups.errorOccurred.text"),
                    MessageType.ERROR).showBalloon();
        }
    }

    List<GroupDescription> getGroups() {
        return groups;
    }
}
