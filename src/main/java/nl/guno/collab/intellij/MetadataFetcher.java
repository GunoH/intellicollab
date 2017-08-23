package nl.guno.collab.intellij;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.diagnostic.Logger;
import com.smartbear.ccollab.datamodel.DataModelException;
import com.smartbear.ccollab.datamodel.User;

class MetadataFetcher {

    private static final Logger logger = Logger.getInstance(MetadataFetcher.class.getName());

    @Nullable
    static Metadata fetch(@NotNull User user) {

        
        try {
            // Retrieve all metadata from the collaborator server
            return new Metadata(
                    user.getEngine().metaDataDescriptionsFind(1, "AdminReviewFields", "Overview").get(0),
                    user.getEngine().metaDataDescriptionsFind(1, "AdminReviewFields", "Issuetracker").get(0),
                    user.getEngine().metaDataDescriptionsFind(1, "AdminReviewFields", "Issue#").get(0),
                    user.getEngine().metaDataDescriptionsFind(1, "AdminReviewFields", "FO").get(0),
                    user.getEngine().metaDataDescriptionsFind(1, "AdminReviewFields", "TO / Werkplan").get(0),
                    user.getEngine().metaDataDescriptionsFind(1, "AdminReviewFields", "Release notes - Functionele omschrijving").get(0),
                    user.getEngine().metaDataDescriptionsFind(1, "AdminReviewFields", "Release notes - Technische omschrijving").get(0),
                    user.getEngine().metaDataDescriptionsFind(1, "AdminReviewFields", "Release notes - Migratiepad").get(0));
            
        } catch (DataModelException e) {
            logger.warn("Error when retrieving metadata.", e);
            return null;
        }
    }
}
