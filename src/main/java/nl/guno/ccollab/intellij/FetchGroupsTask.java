package nl.guno.ccollab.intellij;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.smartbear.ccollab.datamodel.DataModelException;
import com.smartbear.ccollab.datamodel.GroupDescription;
import com.smartbear.ccollab.datamodel.User;
import nl.guno.ccollab.intellij.ui.Notification;

class FetchGroupsTask extends Task.Modal {

    private static final Logger logger = Logger.getInstance(FetchGroupsTask.class.getName());

    private boolean success;

    private final Project project;
    private final User user;

    private List<GroupDescription> groups;

    public FetchGroupsTask(Project project, User user) {
        super(project, MessageResources.message("task.createReview.title"), false);

        this.project = project;
        this.user = user;
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {

        progressIndicator.setText(MessageResources.message("progressIndicator.createReview.retrievingGroups"));

        try {
            // Retrieve all groups from the code collaborator server
            groups = new ArrayList<GroupDescription>();
            for (GroupDescription group : user.getEngine().groupsFind()) {
                if (!group.isReportingOnly()) {
                    groups.add(group);
                }
            }

            // Sort groups.
            Collections.sort(groups, new Comparator<GroupDescription>() {
                public int compare(GroupDescription o1, GroupDescription o2) {
                    return o1.getDisplayName().compareTo(o2.getDisplayName());
                }
            });

        } catch (DataModelException e) {
            logger.warn("Error when retrieving groups.", e);
            return;
        }
        success = true;
    }

    @Override
    public void onSuccess() {
        if (!success) {
            new Notification(project, MessageResources.message("task.fetchGroups.errorOccurred.text"),
                    MessageType.ERROR).showBalloon();
        }
    }

    public List<GroupDescription> getGroups() {
        return groups;
    }
}
