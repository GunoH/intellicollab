package nl.guno.ccollab.intellij;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.jetbrains.annotations.NotNull;

import com.intellij.ide.BrowserUtil;
import com.intellij.notification.NotificationListener;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.smartbear.ccollab.datamodel.*;
import nl.guno.ccollab.intellij.settings.IntelliCcollabSettings;
import nl.guno.ccollab.intellij.ui.Notification;

public class CreateReviewTask extends Task.Backgroundable {

    private static final Logger logger = Logger.getInstance(CreateReviewTask.class.getName());

    private boolean success;

    private final Project project;
    private final User user;
    private final GroupDescription group;
    private final String reviewTitle;
    private final boolean uploadRestricted;
    private final ReviewAccess reviewAccess;
    private final User author;
    private final User reviewer;
    private final User observer;
    private final Map<MetaDataDescription, Object> metadata;

    private Review review;

    public CreateReviewTask(Project project, User user, GroupDescription group, String reviewTitle,
                            boolean uploadRestricted, ReviewAccess reviewAccess, User author, User reviewer,
                            User observer, Map<MetaDataDescription, Object> metadata) {
        super(project, MessageResources.message("task.createReview.title"), false);

        this.project = project;
        this.user = user;
        this.group = group;
        this.reviewTitle = reviewTitle;
        this.uploadRestricted = uploadRestricted;
        this.reviewAccess = reviewAccess;
        this.author = author;
        this.reviewer = reviewer;
        this.observer = observer;
        this.metadata = metadata;
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {

        progressIndicator.setText(MessageResources.message("progressIndicator.createReview.start"));

        createReview();

        success = true;
    }

    /**
     * Creates a new review object
     */
    private void createReview() {

        // Create the new review object with the local user as the creator
        review = user.getEngine().reviewCreate(user, reviewTitle);
        review.setGroup(group);
        review.setUploadRestricted(uploadRestricted);
        review.setReviewAccess(reviewAccess);

        String template = user.getEngine().roleStandardTemplateName();
        Role[] roles = user.getEngine().rolesFind(template, true);

        if (author != null) {
            review.addParticipants(new ReviewParticipant(author, Role.findAuthor(roles)));
        }
        if (reviewer != null) {
            review.addParticipants(new ReviewParticipant(reviewer, Role.findReviewer(roles)));
        }
        if (observer != null) {
            review.addParticipants(new ReviewParticipant(observer, Role.findObserver(roles)));
        }

        if (metadata != null) {
            for (Map.Entry<MetaDataDescription, Object> entry : metadata.entrySet()) {
                switch (entry.getKey().getType()) {
                    case CHARACTER:
                        review.getUserDefinedFields().setCharacter(entry.getKey().getTitle(), (Character) entry.getValue());
                        break;
                    case INTEGER:
                        review.getUserDefinedFields().setInteger(entry.getKey().getTitle(), (Integer) entry.getValue());
                        break;
                    case INTEGER_SET:
                        review.getUserDefinedFields().setIntegerSet(entry.getKey().getTitle(), (Set<Integer>) entry.getValue());
                        break;
                    case MULTI_SELECTION:
                        review.getUserDefinedFields().setMultiSelectItems(entry.getKey().getTitle(), (Collection<IDropDownItem>) entry.getValue());
                        break;
                    case SELECTION:
                        review.getUserDefinedFields().setSelectItem(entry.getKey().getTitle(), (MetaDataSelectItem) entry.getValue());
                        break;
                    case STRING:
                    case STRINGBIG:
                        review.getUserDefinedFields().setString(entry.getKey().getTitle(), (String) entry.getValue());
                    break;
                }
            }
        }
        review.save();
        logger.debug("New review created: " + review.getDisplayText(true));
    }

    @Override
    public void onSuccess() {
        if (success) {
            new Notification(
                    project,
                    MessageResources.message("task.createReview.reviewCreated.text", review.getId().toString(), review.getTitle(), IntelliCcollabSettings.getInstance().getServerUrl()),
                    MessageType.INFO).showBalloon(new HyperlinkListener() {
                @Override
                public void hyperlinkUpdate(HyperlinkEvent e) {
                    BrowserUtil.browse(e.getURL().toExternalForm());
                }
            }).addToEventLog(new NotificationListener() {
                @Override
                public void hyperlinkUpdate(@NotNull com.intellij.notification.Notification notification, 
                                            @NotNull HyperlinkEvent hyperlinkEvent) {
                    if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        BrowserUtil.browse(hyperlinkEvent.getURL().toExternalForm());
                    }
                }
            });
            
        } else {
            new Notification(
                    project,
                    MessageResources.message("task.createReview.errorOccurred.text"),
                    MessageType.ERROR).showBalloon();
        }
    }

    @Override
    public boolean shouldStartInBackground() {
        return false;
    }
}
