package nl.guno.collab.intellij;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.diagnostic.Logger;
import com.smartbear.ccollab.datamodel.DataModelException;
import com.smartbear.ccollab.datamodel.User;

class UsersFetcher {

    private static final Logger logger = Logger.getInstance(UsersFetcher.class.getName());

    @Nullable
    static List<User> fetch(@NotNull User user) {

        try {
            // Retrieve all users from the collaborator server
            return user.getEngine().usersPossibleReviewParticipants(null);
        } catch (DataModelException e) {
            logger.warn("Error when retrieving users.", e);
        }
        return null;
    }
}
