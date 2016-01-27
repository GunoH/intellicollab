package nl.guno.collab.intellij;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;

import com.intellij.openapi.application.ApplicationManager;
import com.smartbear.beans.IGlobalOptions;
import org.jetbrains.annotations.NotNull;

public class IntelliCollabGlobalOptions implements IGlobalOptions {

    private final IGlobalOptions wrappedOptions;

    private final IntelliCollabSettings component =
            ApplicationManager.getApplication().getComponent(IntelliCollabSettings.class);

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
        return wrappedOptions.getServerProxyHost();
    }

    @Override
    public String getServerProxyPort() {
        return wrappedOptions.getServerProxyPort();
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
    public String getProfile() {
        return wrappedOptions.getProfile();
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
    public Boolean isUserProvidedAuth() {
        return wrappedOptions.isUserProvidedAuth();
    }

    @Override
    public void setUserProvidedAuth(Boolean userProvidedAuth) {
wrappedOptions.setUserProvidedAuth(userProvidedAuth);
    }

    @Override
    public Boolean isDebug() {
        return wrappedOptions.isDebug();
    }

    @Override
    public Boolean isUseJsonApi() {
        return Boolean.FALSE;
    }

    @Override
    public Boolean isUseLegacyApi() {
        return Boolean.TRUE;
    }
}