package nl.guno.collab.intellij.ui;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.jetbrains.annotations.NotNull;

import com.intellij.ide.BrowserUtil;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.vcs.changes.ui.ChangesViewContentManager;
import com.intellij.openapi.wm.ToolWindowManager;

public class Notification {
    private static final NotificationGroup NOTIFICATION_GROUP = NotificationGroup.logOnlyGroup("IntelliCollab");

    
    private final Project project;
    private final String message;
    private final MessageType type;
    
    private HyperlinkListener hyperlinkListener = new HyperlinkListener() {
        @Override
        public void hyperlinkUpdate(HyperlinkEvent e) {
            openHyperlink(e);
        }
    };

    private NotificationListener notificationListener = new NotificationListener() {
        @Override
        public void hyperlinkUpdate(@NotNull com.intellij.notification.Notification notification, 
                                    @NotNull HyperlinkEvent hyperlinkEvent) {
            openHyperlink(hyperlinkEvent);
        }
    };
    
    public Notification(Project project, String message, MessageType type) {
        this.project = project;
        this.message = message;
        this.type = type;
    }

    @NotNull
    public Notification showBalloon() {
        ToolWindowManager.getInstance(project).notifyByBalloon(ChangesViewContentManager.TOOLWINDOW_ID, type, message, 
                null, hyperlinkListener);
        return this;
    }

    @NotNull
    public Notification addToEventLog() {
        NOTIFICATION_GROUP.createNotification("", message, type.toNotificationType(), notificationListener).notify(project);
        return this;
    }

    private void openHyperlink(@NotNull HyperlinkEvent hyperlinkEvent) {
        if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            BrowserUtil.launchBrowser(hyperlinkEvent.getURL().toExternalForm());
        }
    }

    public Notification setHyperlinkListener(HyperlinkListener hyperlinkListener) {
        this.hyperlinkListener = hyperlinkListener;
        return this;
    }
}
