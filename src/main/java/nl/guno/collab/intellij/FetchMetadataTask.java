package nl.guno.collab.intellij;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.smartbear.ccollab.datamodel.DataModelException;
import com.smartbear.ccollab.datamodel.MetaDataDescription;
import com.smartbear.ccollab.datamodel.User;
import nl.guno.collab.intellij.ui.Notification;

public class FetchMetadataTask extends Task.Modal {

    private static final Logger logger = Logger.getInstance(FetchMetadataTask.class.getName());

    private boolean success;

    private final Project project;
    private final User user;

    private MetaDataDescription overview;
    private MetaDataDescription bugzillaInstantie;
    private MetaDataDescription bugzillaNummer;
    private MetaDataDescription fo;
    private MetaDataDescription to;
    private MetaDataDescription rnfO;
    private MetaDataDescription rntO;
    private MetaDataDescription rnMigratiePad;

    public FetchMetadataTask(Project project, User user) {
        super(project, MessageResources.message("task.createReview.title"), false);

        this.project = project;
        this.user = user;
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {

        progressIndicator.setText(MessageResources.message("progressIndicator.createReview.retrievingMetadata"));

        try {
            // Retrieve all metadata from the collaborator server
            overview = user.getEngine().metaDataDescriptionsFind(1, "AdminReviewFields", "Overview").get(0);
            bugzillaInstantie = user.getEngine().metaDataDescriptionsFind(1, "AdminReviewFields", "Bugzilla-instantie").get(0);
            bugzillaNummer = user.getEngine().metaDataDescriptionsFind(1, "AdminReviewFields", "Bugzillanummer").get(0);
            fo = user.getEngine().metaDataDescriptionsFind(1, "AdminReviewFields", "FO").get(0);
            to = user.getEngine().metaDataDescriptionsFind(1, "AdminReviewFields", "TO / Werkplan").get(0);
            rnfO = user.getEngine().metaDataDescriptionsFind(1, "AdminReviewFields", "Release notes - Functionele omschrijving").get(0);
            rntO = user.getEngine().metaDataDescriptionsFind(1, "AdminReviewFields", "Release notes - Technische omschrijving").get(0);
            rnMigratiePad = user.getEngine().metaDataDescriptionsFind(1, "AdminReviewFields", "Release notes - Migratiepad").get(0);
        } catch (DataModelException e) {
            logger.warn("Error when retrieving metadata.", e);
            return;
        }
        success = true;
    }

    @Override
    public void onSuccess() {
        if (!success) {
            new Notification(project, MessageResources.message("task.fetchMetaData.errorOccurred.text"),
                    MessageType.ERROR).showBalloon();
        }
    }

    public MetaDataDescription getOverview() {
        return overview;
    }

    public MetaDataDescription getBugzillaInstantie() {
        return bugzillaInstantie;
    }

    public MetaDataDescription getBugzillaNummer() {
        return bugzillaNummer;
    }

    public MetaDataDescription getFo() {
        return fo;
    }

    public MetaDataDescription getTo() {
        return to;
    }

    public MetaDataDescription getRnfo() {
        return rnfO;
    }

    public MetaDataDescription getRnto() {
        return rntO;
    }

    public MetaDataDescription getRnMigratiePad() {
        return rnMigratiePad;
    }
}
