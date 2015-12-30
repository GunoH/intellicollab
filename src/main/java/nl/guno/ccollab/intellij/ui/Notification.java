package nl.guno.ccollab.intellij.ui;

import javax.swing.event.HyperlinkListener;

import org.jetbrains.annotations.NotNull;

import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.vcs.changes.ui.ChangesViewContentManager;
import com.intellij.openapi.wm.ToolWindowManager;

public class Notification {
    private static final NotificationGroup NOTIFICATION_GROUP = NotificationGroup.logOnlyGroup("IntelliCcollab");

    
    private final Project project;
    private final String message;
    private final MessageType type;
    
    public Notification(Project project, String message, MessageType type) {
        this.project = project;
        this.message = message;
        this.type = type;
    }

    @NotNull
    public Notification showBalloon() {
        ToolWindowManager.getInstance(project).notifyByBalloon(ChangesViewContentManager.TOOLWINDOW_ID, type, message);
        return this;
    }

    @NotNull
    public Notification showBalloon(@NotNull HyperlinkListener hyperlinkListener) {
        ToolWindowManager.getInstance(project).notifyByBalloon(ChangesViewContentManager.TOOLWINDOW_ID, type, message, 
                null, hyperlinkListener);
        return this;
    }

    @NotNull
    public Notification addToEventLog() {
        NOTIFICATION_GROUP.createNotification(message, type.toNotificationType()).notify(project);
        return this;
    }

    @NotNull
    public Notification addToEventLog(@NotNull NotificationListener notificationListener) {
        NOTIFICATION_GROUP.createNotification("", message, type.toNotificationType(), notificationListener).notify(project);
        return this;
    }
}
