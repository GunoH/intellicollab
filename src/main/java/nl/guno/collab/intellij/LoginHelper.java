package nl.guno.collab.intellij;

import java.io.IOException;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.jetbrains.annotations.NotNull;

import com.intellij.notification.NotificationListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.smartbear.ccollab.client.ICollabClientInterface;
import com.smartbear.ccollab.datamodel.User;
import nl.guno.collab.intellij.settings.IntelliCollabSettings;
import nl.guno.collab.intellij.ui.Notification;

class LoginHelper {

    private Project project;
    private User user;

    LoginHelper(Project project) {
        this.project = project;
    }

    boolean login() throws IOException {

        //load options from config files
        IntelliCollabGlobalOptions globalOptions = new IntelliCollabGlobalOptions(ConfigOptions.getInstance().getA());

        if (globalOptions.settingsIncomplete()) {
            new Notification(project, MessageResources.message("configuration.error.mandatorySettingsMissing.text"),
                    MessageType.ERROR).showBalloon(new HyperlinkListener() {
                @Override
                public void hyperlinkUpdate(HyperlinkEvent e) {
                    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        IntelliCollabSettings.openSettings(project);
                    }
                }
            }).addToEventLog(new NotificationListener() {
                @Override
                public void hyperlinkUpdate(@NotNull com.intellij.notification.Notification notification,
                                            @NotNull HyperlinkEvent hyperlinkEvent) {
                    if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        IntelliCollabSettings.openSettings(project);
                    }
                }
            });
            return false;
        }

        if (!new Environment().checkConnection()) {
            new Notification(project, MessageResources.message("action.error.serverNotAvailable.text"),
                    MessageType.ERROR).showBalloon(new HyperlinkListener() {
                @Override
                public void hyperlinkUpdate(HyperlinkEvent e) {
                    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        IntelliCollabSettings.openSettings(project);
                    }
                }
            }).addToEventLog(new NotificationListener() {
                @Override
                public void hyperlinkUpdate(@NotNull com.intellij.notification.Notification notification,
                                            @NotNull HyperlinkEvent hyperlinkEvent) {
                    if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        IntelliCollabSettings.openSettings(project);
                    }
                }
            });
            return false;
        }


        //initialize client interface
        ICollabClientInterface clientInterface = new IntelliCollabClient(globalOptions);

        //connect to server and log in (throws exception if authentication fails, can't find server, etc...)
        LoginTask loginTask = new LoginTask(project, globalOptions, clientInterface);
        loginTask.queue();

        if (!loginTask.success()) {

            if (loginTask.authenticationErrorOccured()) {
                new Notification(project, MessageResources.message("task.login.authenticationError.text"),
                        MessageType.ERROR).showBalloon(new HyperlinkListener() {
                    @Override
                    public void hyperlinkUpdate(HyperlinkEvent e) {
                        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                            IntelliCollabSettings.openSettings(project);
                        }
                    }
                }).addToEventLog(new NotificationListener() {
                    @Override
                    public void hyperlinkUpdate(@NotNull com.intellij.notification.Notification notification,
                                                @NotNull HyperlinkEvent hyperlinkEvent) {
                        IntelliCollabSettings.openSettings(project);
                    }
                });
            } else {
                new Notification(project, MessageResources.message("task.login.unknowError.text"),
                        MessageType.ERROR).showBalloon(new HyperlinkListener() {
                    @Override
                    public void hyperlinkUpdate(HyperlinkEvent e) {
                        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                            PluginUtil.openLogDirectory();
                        }
                    }
                }).addToEventLog(new NotificationListener() {
                    @Override
                    public void hyperlinkUpdate(@NotNull com.intellij.notification.Notification notification,
                                                @NotNull HyperlinkEvent hyperlinkEvent) {
                        if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                            PluginUtil.openLogDirectory();
                        }
                    }
                });
            }

            return false;
        }
        
        user = loginTask.getUser();
        return true;
    }

    public User getUser() {
        return user;
    }
}
