package com.quinity.ccollab.intellij;

import com.intellij.openapi.application.ApplicationManager;
import com.smartbear.beans.IGlobalOptions;

import java.net.URL;

public class IntelliCcollabGlobalOptions implements IGlobalOptions {

	private IGlobalOptions wrappedOptions;

	IntelliCcollabApplicationComponent component =
			ApplicationManager.getApplication().getComponent(IntelliCcollabApplicationComponent.class);

	public IntelliCcollabGlobalOptions(IGlobalOptions wrappedOptions) {
		this.wrappedOptions = wrappedOptions;
		
	}

	public Boolean isNoBrowser() {
		return wrappedOptions.isNoBrowser();
	}

	public String getPassword() {
		return wrappedOptions.getPassword();
	}

	public Boolean isNonInteractive() {
		return wrappedOptions.isNonInteractive();
	}

	public URL getUrl() {
		if (component.getServerURL() == null) {
			component.setServerURL(wrappedOptions.getUrl());
		}
		return component.getServerURL();
	}

	public String getUser() {
		return wrappedOptions.getUser();
	}

	public Boolean isQuiet() {
		return wrappedOptions.isQuiet();
	}

	public String getServerProxyHost() {
		return wrappedOptions.getServerProxyHost();
	}

	public String getServerProxyPort() {
		return wrappedOptions.getServerProxyPort();
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