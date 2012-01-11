package nl.guno.ccollab.intellij;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import nl.guno.ccollab.intellij.ui.CreateReviewDialog;
import com.smartbear.CollabClientException;
import com.smartbear.ccollab.client.CollabClientServerConnectivityException;
import com.smartbear.ccollab.datamodel.GroupDescription;
import com.smartbear.ccollab.datamodel.IDropDownItem;
import com.smartbear.ccollab.datamodel.MetaDataDescription;
import com.smartbear.ccollab.datamodel.ReviewAccess;
import com.smartbear.ccollab.datamodel.User;
import com.smartbear.scm.ScmConfigurationException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateReviewAction extends IntelliCcollabAction {

    private static Logger logger = Logger.getInstance(CreateReviewAction.class.getName());


    @Override
    public void actionPerformed(AnActionEvent event) {

        try {
            Project project = PluginUtil.getProject(event.getDataContext());

            init(project);

            if (engine == null) {
                return;
            }

            // Retrieve the available users
            FetchUsersTask fetchUsersTask = new FetchUsersTask(project, user);
            fetchUsersTask.queue();

            User[] users = fetchUsersTask.getUsers();

            // Retrieve the available groups
            FetchGroupsTask fetchGroupsTask = new FetchGroupsTask(project, user);
            fetchGroupsTask.queue();

            List<GroupDescription> groups = fetchGroupsTask.getGroups();

            // Retrieve the available metadata
            FetchMetadataTask fetchMetadataTask = new FetchMetadataTask(project, user);
            fetchMetadataTask.queue();
            MetaDataDescription overview = fetchMetadataTask.getOverview();
            MetaDataDescription bugzillaInstantie = fetchMetadataTask.getBugzillaInstantie();
            MetaDataDescription bugzillanummer = fetchMetadataTask.getBugzillanummer();
            MetaDataDescription fo = fetchMetadataTask.getFO();
            MetaDataDescription to = fetchMetadataTask.getTO();

            IDropDownItem[] bugzillaInstanties = bugzillaInstantie.getDropDownItems(true);

            CreateReviewDialog createReviewDialog = new CreateReviewDialog(users, groups, bugzillaInstanties, user);
            createReviewDialog.pack();
            createReviewDialog.setVisible(true);

            if (!createReviewDialog.isOkPressed()) {
                logger.debug("User pressed cancel.");
                return;
            }

            GroupDescription selectedGroup = createReviewDialog.getSelectedGroup();
            String enteredTitle = createReviewDialog.getEnteredTitle();
            boolean uploadRestricted = createReviewDialog.isUploadRestricted();
            ReviewAccess reviewAccess = createReviewDialog.getReviewAccess();
            User selectedAuthor = createReviewDialog.getSelectedAuthor();
            User selectedReviewer = createReviewDialog.getSelectedReviewer();
            User selectedObserver = createReviewDialog.getSelectedObserver();

            Map<MetaDataDescription, Object> metadata = new HashMap<MetaDataDescription, Object>();
            metadata.put(overview, createReviewDialog.getEnteredOverview());
            metadata.put(bugzillaInstantie, createReviewDialog.getSelectedBugzillaInstantie());
            metadata.put(bugzillanummer, createReviewDialog.getEnteredBugzillanummer());
            metadata.put(fo, createReviewDialog.getEnteredFO());
            metadata.put(to, createReviewDialog.getEnteredTO());

            CreateReviewTask createReviewTask = new CreateReviewTask(project, user, selectedGroup, enteredTitle,
                    uploadRestricted, reviewAccess, selectedAuthor, selectedReviewer, selectedObserver, metadata);
            createReviewTask.queue();

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
            Messages.showErrorDialog(MessageResources.message("errorDialog.errorOccurred.text"),
                    MessageResources.message("errorDialog.errorOccurred.title"));
        } catch (IOException e) {
            logger.warn(e);
            Messages.showErrorDialog(MessageResources.message("errorDialog.ioErrorOccurred.text"),
                    MessageResources.message("errorDialog.ioErrorOccurred.title"));
        } finally {
            finished();
        }

    }
}
