package com.quinity.ccollab.intellij;

import com.intellij.CommonBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import java.util.ResourceBundle;


public final class MessageResources {
    @NonNls
    private static final String BUNDLE_NAME = "com.quinity.ccollab.intellij.MessageResources";
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private MessageResources() {
    }

    public static String message(@PropertyKey(resourceBundle = BUNDLE_NAME) String key, Object... params) {
        return CommonBundle.message(BUNDLE, key, params);
        
    }
}
