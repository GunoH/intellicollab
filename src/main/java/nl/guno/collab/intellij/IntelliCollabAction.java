package nl.guno.collab.intellij;

import java.io.IOException;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.jetbrains.annotations.NotNull;

import com.intellij.notification.NotificationListener;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.smartbear.CollabClientException;
import com.smartbear.beans.ConfigUtils;
import com.smartbear.beans.IGlobalOptions;
import com.smartbear.beans.IScmOptions;
import com.smartbear.ccollab.client.ICollabClientInterface;
import com.smartbear.ccollab.datamodel.Engine;
import com.smartbear.ccollab.datamodel.User;
import com.smartbear.collections.Pair;
import nl.guno.ccollab.intellij.settings.IntelliCcollabSettingsConfigurable;
import nl.guno.ccollab.intellij.ui.Notification;

abstract class IntelliCcollabAction extends AnAction {

    /**
     * SCM options, created by {@link #init(com.intellij.openapi.project.Project)}
     */
    static IScmOptions scmOptions;

    /**
     * Connection to Collaborator server
     * created by {@link #init(com.intellij.openapi.project.Project)}
     */
    static Engine engine;

    /**
     * Currently logged-in user
     * created by {@link #init(com.intellij.openapi.project.Project)}
     */
    static User user;

    static boolean init(final Project project) throws CollabClientException, IOException, InterruptedException {

        //load options from config files
        Pair<IGlobalOptions, IScmOptions> configOptions = ConfigUtils.loadConfigFiles();
        IntelliCollabGlobalOptions globalOptions = new IntelliCollabGlobalOptions(configOptions.getA());

        if (globalOptions.settingsIncomplete()) {
            new Notification(project, MessageResources.message("configuration.error.mandatorySettingsMissing.text"),
                    MessageType.ERROR).showBalloon(new HyperlinkListener() {
                @Override
                public void hyperlinkUpdate(HyperlinkEvent e) {
                    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        openSettings(project);
                    }
                }
            }).addToEventLog(new NotificationListener() {
                @Override
                public void hyperlinkUpdate(@NotNull com.intellij.notification.Notification notification,
                                            @NotNull HyperlinkEvent hyperlinkEvent) {
                    if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        openSettings(project);
                    }
                }
            });
            return false;
        }

        if (!new Environment().checkConnection()) {
            new Notification(project, MessageResources.message("action.error.serverNotAvailable.text"),
                    MessageType.ERROR).showBalloon().addToEventLog();
            return false;
        }

        // If we've already initialized, don't do it again.
        if ( engine != null ) {
            return true;
        }

        scmOptions = configOptions.getB();

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
                            openSettings(project);
                        }
                    }
                }).addToEventLog(new NotificationListener() {
                    @Override
                    public void hyperlinkUpdate(@NotNull com.intellij.notification.Notification notification,
                                                @NotNull HyperlinkEvent hyperlinkEvent) {
                        openSettings(project);
                    }
                });
            } else {
                new Notification(project, MessageResources.message("task.login.unknowError.text"),
                        MessageType.ERROR).showBalloon(new HyperlinkListener() {
                    @Override
                    public void hyperlinkUpdate(HyperlinkEvent e) {
                        openSettings(project);
                    }
                }).addToEventLog(new NotificationListener() {
                    @Override
                    public void hyperlinkUpdate(@NotNull com.intellij.notification.Notification notification,
                                                @NotNull HyperlinkEvent hyperlinkEvent) {
                        if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                            openSettings(project);
                        }
                    }
                });
            }

            return false;
        }
        
        user = loginTask.getUser();

        if (user != null) {
            engine = user.getEngine();
        }

	    return true;
    }

    private static void openSettings(Project project) {
        ShowSettingsUtil.getInstance().editConfigurable(project, new IntelliCcollabSettingsConfigurable());
    }

    /**
     * Called to clean up a previous call to {@link #init(Project)}.
     * <p/>
     * <b>THIS IS CRITICAL</b>.  If you do not close out your {@code CollabClientConnection}
     * object, data might not be flushed out to the server!
     */
    void finished() {
        if (engine != null) {
            engine.close(true);
        }
    }

}
