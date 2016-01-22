package nl.guno.ccollab.intellij;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import com.smartbear.beans.IGlobalOptions;
import nl.guno.ccollab.intellij.settings.IntelliCcollabSettings;

class IntelliCcollabGlobalOptions implements IGlobalOptions {

    private final IGlobalOptions wrappedOptions;

    IntelliCcollabGlobalOptions(IGlobalOptions wrappedOptions) {
        this.wrappedOptions = wrappedOptions;

    }

    /**
     * Indicates if any of the mandatory settings are missing.
     * @return {@code true} if any of the mandatory settings are missing, {@code false} otherwise.
     */
    boolean settingsIncomplete() {
        IntelliCcollabSettings settings = IntelliCcollabSettings.getInstance();

        return StringUtils.isEmpty(settings.getServerUrl())
                || StringUtils.isEmpty(settings.getUsername())
                || StringUtils.isEmpty(settings.getPassword());
    }

    @Override @NotNull
    public URL getUrl() {
        String serverUrl = IntelliCcollabSettings.getInstance().getServerUrl();
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
        return IntelliCcollabSettings.getInstance().getUsername();
    }

    @Override
    public String getPassword() {
        return IntelliCcollabSettings.getInstance().getPassword();
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