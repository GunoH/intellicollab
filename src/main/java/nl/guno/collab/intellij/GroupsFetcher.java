package nl.guno.collab.intellij;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.diagnostic.Logger;
import com.smartbear.ccollab.datamodel.DataModelException;
import com.smartbear.ccollab.datamodel.GroupDescription;
import com.smartbear.ccollab.datamodel.User;

class GroupsFetcher {

    private static final Logger logger = Logger.getInstance(GroupsFetcher.class.getName());

    @Nullable
    static List<GroupDescription> fetch(@NotNull User user) {

        List<GroupDescription> groups = null;
        
        try {
            // Retrieve all groups from the collaborator server
            groups = new ArrayList<>();
            for (GroupDescription group : user.getEngine().groupsFind()) {
                if (!group.isReportingOnly()) {
                    groups.add(group);
                }
            }

            // Sort groups.
            groups.sort(Comparator.comparing(GroupDescription::getDisplayName));

        } catch (DataModelException e) {
            logger.warn("Error when retrieving groups.", e);
        }
        return groups;
    }
}
