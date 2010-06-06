package com.quinity.ccollab.intellij;

import com.intellij.openapi.application.ApplicationManager;
import com.smartbear.beans.IGlobalOptions;

import java.net.MalformedURLException;
import java.net.URL;

public class IntelliCcollabGlobalOptions implements IGlobalOptions {

	private IGlobalOptions wrappedOptions;

	IntelliCcollabApplicationComponent component =
			ApplicationManager.getApplication().getComponent(IntelliCcollabApplicationComponent.class);

	public IntelliCcollabGlobalOptions(IGlobalOptions wrappedOptions) {
		this.wrappedOptions = wrappedOptions;
		
	}

	public URL getUrl() {
		try {
			return new URL(component.getServerURL());
		} catch (MalformedURLException e) {
			throw new RuntimeException("Invalid URL:" + component.getServerURL());
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