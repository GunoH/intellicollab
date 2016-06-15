package nl.guno.collab.intellij.settings;

import org.jetbrains.annotations.NotNull;

import com.intellij.ide.ui.search.SearchableOptionContributor;
import com.intellij.ide.ui.search.SearchableOptionProcessor;
import nl.guno.collab.intellij.MessageResources;

public class IntelliCollabSearchableOptionContributor extends SearchableOptionContributor {
    @Override
    public void processOptions(@NotNull SearchableOptionProcessor processor) {
        final String configurableId = IntelliCollabSettingsConfigurable.class.getName();
        final String displayName = MessageResources.message("configuration.key");
        processor.addOptions(MessageResources.message("configuration.serverURL.label"), null, "IntelliCollab options",
                configurableId, displayName, true);
        processor.addOptions(MessageResources.message("configuration.username.label"), null, "IntelliCollab options",
                configurableId, displayName, true);
        processor.addOptions(MessageResources.message("configuration.password.label"), null, "IntelliCollab options",
                configurableId, displayName, true);
    }
}
