package nl.guno.collab.intellij;

import java.util.ResourceBundle;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import com.intellij.CommonBundle;


public final class MessageResources {
    @NonNls
    private static final String BUNDLE_NAME = "nl.guno.collab.intellij.MessageResources";
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private MessageResources() {
    }

    @NotNull
    public static String message(@PropertyKey(resourceBundle = BUNDLE_NAME) @NotNull String key, @NotNull Object... params) {
        return CommonBundle.message(BUNDLE, key, params);
        
    }
}
