package nl.guno.collab.intellij;

import java.net.MalformedURLException;
import java.net.URL;

import nl.guno.collab.intellij.settings.IntelliCollabSettings;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import com.smartbear.beans.IGlobalOptions;

class IntelliCollabGlobalOptions implements IGlobalOptions {

    private final IGlobalOptions wrappedOptions;

    IntelliCollabGlobalOptions(IGlobalOptions wrappedOptions) {
        this.wrappedOptions = wrappedOptions;

    }

    /**
     * Indicates if any of the mandatory settings are missing.
     * @return {@code true} if any of the mandatory settings are missing, {@code false} otherwise.
     */
    boolean settingsIncomplete() {
        IntelliCollabSettings settings = IntelliCollabSettings.getInstance();

        return StringUtils.isEmpty(settings.getServerUrl())
                || StringUtils.isEmpty(settings.getUsername())
                || StringUtils.isEmpty(settings.getPassword());
    }

    @Override @NotNull
    public URL getUrl() {
        String serverUrl = IntelliCollabSettings.getInstance().getServerUrl();
        try {
            return new URL(serverUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid URL:" + serverUrl);
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
        return IntelliCollabSettings.getInstance().getUsername();
    }

    @Override
    public String getPassword() {
        return IntelliCollabSettings.getInstance().getPassword();
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