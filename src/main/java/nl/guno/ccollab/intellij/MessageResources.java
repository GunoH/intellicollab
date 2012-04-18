package nl.guno.ccollab.intellij;

import java.util.ResourceBundle;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import com.intellij.CommonBundle;


public final class MessageResources {
    @NonNls
    private static final String BUNDLE_NAME = "nl.guno.ccollab.intellij.MessageResources";
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private MessageResources() {
    }

    public static String message(@PropertyKey(resourceBundle = BUNDLE_NAME) String key, Object... params) {
        return CommonBundle.message(BUNDLE, key, params);
        
    }
}
