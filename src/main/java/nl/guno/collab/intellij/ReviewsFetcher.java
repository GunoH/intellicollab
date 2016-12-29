package nl.guno.collab.intellij;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.diagnostic.Logger;
import com.smartbear.ccollab.datamodel.DataModelException;
import com.smartbear.ccollab.datamodel.Review;
import com.smartbear.ccollab.datamodel.User;

class ReviewsFetcher {

    private static final Logger logger = Logger.getInstance(ReviewsFetcher.class.getName());

    @Nullable
    static List<Review> fetch(@NotNull User user) {
        try {
            // Retrieve all reviews the user can upload to.
            return user.getReviewsCanUploadChangelists(null);
        } catch (DataModelException e) {
            logger.warn("Error when retrieving reviews.", e);
        }
        return null;
    }
}
