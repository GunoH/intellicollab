package nl.guno.collab.intellij;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.smartbear.ccollab.datamodel.User;
import nl.guno.collab.intellij.ui.Notification;

public class FetchMetadataTask extends Task.Modal {

    private boolean success;

    private final Project project;
    private final User user;

    private Metadata metadata;

    FetchMetadataTask(Project project, User user) {
        super(project, MessageResources.message("task.createReview.title"), false);

        this.project = project;
        this.user = user;
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {

        progressIndicator.setText(MessageResources.message("progressIndicator.createReview.retrievingMetadata"));

        metadata = MetadataFetcher.fetch(user);

        success = true;
    }

    @Override
    public void onSuccess() {
        if (!success) {
            new Notification(project, MessageResources.message("task.fetchMetaData.errorOccurred.text"),
                    MessageType.ERROR).showBalloon();
        }
    }

    public Metadata getMetadata() {
        return metadata;
    }
}
