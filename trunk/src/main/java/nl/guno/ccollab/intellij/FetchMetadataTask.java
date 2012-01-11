package nl.guno.ccollab.intellij;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.smartbear.ccollab.datamodel.DataModelException;
import com.smartbear.ccollab.datamodel.MetaDataDescription;
import com.smartbear.ccollab.datamodel.User;
import org.jetbrains.annotations.NotNull;

public class FetchMetadataTask extends Task.Modal {

    private static Logger logger = Logger.getInstance(FetchMetadataTask.class.getName());

    private boolean success;

    private User user;

    private MetaDataDescription overview;
    private MetaDataDescription bugzillaInstantie;
    private MetaDataDescription bugzillanummer;
    private MetaDataDescription fo;
    private MetaDataDescription to;

    public FetchMetadataTask(Project project, User user) {
        super(project, MessageResources.message("task.createReview.title"), false);

        this.user = user;
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {

        progressIndicator.setText(MessageResources.message("progressIndicator.createReview.retrievingMetadata"));

        try {
            // Retrieve all metadata from the code collaborator server
            overview = user.getEngine().metaDataDescriptionsFind(1, "AdminReviewFields", "Overview")[0];
            bugzillaInstantie = user.getEngine().metaDataDescriptionsFind(1, "AdminReviewFields", "Bugzilla-instantie")[0];
            bugzillanummer = user.getEngine().metaDataDescriptionsFind(1, "AdminReviewFields", "Bugzillanummer")[0];
            fo = user.getEngine().metaDataDescriptionsFind(1, "AdminReviewFields", "FO")[0];
            to = user.getEngine().metaDataDescriptionsFind(1, "AdminReviewFields", "TO / Werkplan")[0];
        } catch (DataModelException e) {
            logger.warn("Error when retrieving metadata.", e);
            return;
        }
        success = true;
    }

    @Override
    public void onSuccess() {
        if (!success) {
            Messages.showErrorDialog(MessageResources.message("errorDialog.errorOccurred.text"),
                    MessageResources.message("errorDialog.errorOccurred.title"));
        }
    }

    public MetaDataDescription getOverview() {
        return overview;
    }

    public MetaDataDescription getBugzillaInstantie() {
        return bugzillaInstantie;
    }

    public MetaDataDescription getBugzillanummer() {
        return bugzillanummer;
    }

    public MetaDataDescription getFO() {
        return fo;
    }

    public MetaDataDescription getTO() {
        return to;
    }
}