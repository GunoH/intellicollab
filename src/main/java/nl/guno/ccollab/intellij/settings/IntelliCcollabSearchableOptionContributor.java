package nl.guno.ccollab.intellij.settings;

import org.jetbrains.annotations.NotNull;

import com.intellij.ide.ui.search.SearchableOptionContributor;
import com.intellij.ide.ui.search.SearchableOptionProcessor;
import nl.guno.ccollab.intellij.MessageResources;

public class IntelliCcollabSearchableOptionContributor extends SearchableOptionContributor {
    @Override
    public void processOptions(@NotNull SearchableOptionProcessor processor) {
        final String configurableId = "nl.guno.ccollab.intellij.settings.IntelliCcollabSettingsConfigurable";
        final String displayName = MessageResources.message("configuration.key");
        processor.addOptions("Code Collaborator URL", null, "IntelliCcollab options",
                configurableId, displayName, true);
        processor.addOptions("Username", null, "IntelliCcollab options",
                configurableId, displayName, true);
        processor.addOptions("Password", null, "IntelliCcollab options",
                configurableId, displayName, true);
    }
}
