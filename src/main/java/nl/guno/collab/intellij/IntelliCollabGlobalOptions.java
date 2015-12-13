package nl.guno.collab.intellij;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;

import com.intellij.openapi.application.ApplicationManager;
import com.smartbear.beans.IGlobalOptions;
import org.jetbrains.annotations.NotNull;

public class IntelliCollabGlobalOptions implements IGlobalOptions {

    private final IGlobalOptions wrappedOptions;

    @NotNull
    private final IntelliCollabApplicationComponent component =
            ApplicationManager.getApplication().getComponent(IntelliCollabApplicationComponent.class);

    public IntelliCollabGlobalOptions(IGlobalOptions wrappedOptions) {
        this.wrappedOptions = wrappedOptions;

    }

    /**
     * Indicates if any of the mandatory settings are missing.
     * @return {@code true} if any of the mandatory settings are missing, {@code false} otherwise.
     */
    public boolean settingsIncomplete() {
        return StringUtils.isEmpty(component.getServerURL())
                || StringUtils.isEmpty(component.getUsername())
                || StringUtils.isEmpty(component.getPassword());
    }

    @Override @NotNull
    public URL getUrl() {
        try {
            return new URL(component.getServerURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid URL:" + component.getServerURL());
        }
    }

    @Override
    public String getServerProxyHost() {
        return component.getServerProxyHost();
    }

    @Override
    public String getServerProxyPort() {
        return component.getServerProxyPort();
    }

    @Override
    public String getUser() {
        return component.getUsername();
    }

    @Override
    public String getPassword() {
        return component.getPassword();
    }

    @Override
    public Boolean isNoBrowser() {
        return wrappedOptions.isNoBrowser();
    }

    @Override
    public Boolean isNonInteractive() {
        return wrappedOptions.isNonInteractive();
    }

    @Override
    public Boolean isQuiet() {
        return wrappedOptions.isQuiet();
    }

    @Override
    public String getEditor() {
        return wrappedOptions.getEditor();
    }

    @Override
    public Boolean isEditorPrompt() {
        return wrappedOptions.isEditorPrompt();
    }

    @Override
    public Boolean isPauseOnError() {
        return wrappedOptions.isPauseOnError();
    }

    @Override
    public String getBrowser() {
        return wrappedOptions.getBrowser();
    }

    @Override
    public Boolean isForceNewBrowser() {
        return wrappedOptions.isForceNewBrowser();
    }
}