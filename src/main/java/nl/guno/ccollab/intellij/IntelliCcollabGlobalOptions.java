package nl.guno.ccollab.intellij;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;

import com.intellij.openapi.application.ApplicationManager;
import com.smartbear.beans.IGlobalOptions;

public class IntelliCcollabGlobalOptions implements IGlobalOptions {

    private IGlobalOptions wrappedOptions;

    private IntelliCcollabApplicationComponent component =
            ApplicationManager.getApplication().getComponent(IntelliCcollabApplicationComponent.class);

    public IntelliCcollabGlobalOptions(IGlobalOptions wrappedOptions) {
        this.wrappedOptions = wrappedOptions;

    }

    /**
     * Indicates if any of the mandatory settings are missing.
     * @return <code>true</code> if any of the mandatory settings are missing, <code>false</code> otherwise.
     */
    public boolean settingsIncomplete() {
        return StringUtils.isEmpty(component.getHttpServerURL())
                || StringUtils.isEmpty(component.getRegularServerURL())
                || StringUtils.isEmpty(component.getUsername())
                || StringUtils.isEmpty(component.getPassword());
    }

    public URL getUrl() {
        try {
            return new URL(component.getHttpServerURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid URL:" + component.getHttpServerURL());
        }
    }

    public String getServerProxyHost() {
        return component.getServerProxyHost();
    }

    public String getServerProxyPort() {
        return component.getServerProxyPort();
    }

    public String getUser() {
        return component.getUsername();
    }

    public String getPassword() {
        return component.getPassword();
    }

    public Boolean isNoBrowser() {
        return wrappedOptions.isNoBrowser();
    }

    public Boolean isNonInteractive() {
        return wrappedOptions.isNonInteractive();
    }

    public Boolean isQuiet() {
        return wrappedOptions.isQuiet();
    }

    public String getEditor() {
        return wrappedOptions.getEditor();
    }

    public Boolean isEditorPrompt() {
        return wrappedOptions.isEditorPrompt();
    }

    public Boolean isPauseOnError() {
        return wrappedOptions.isPauseOnError();
    }

    public String getBrowser() {
        return wrappedOptions.getBrowser();
    }

    public Boolean isForceNewBrowser() {
        return wrappedOptions.isForceNewBrowser();
    }
}