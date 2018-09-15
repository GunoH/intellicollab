package nl.guno.collab.intellij;

import com.smartbear.beans.IGlobalOptions;
import com.smartbear.ccollab.CommandLineClient;

public class IntelliCollabClient extends CommandLineClient {
    public IntelliCollabClient(IGlobalOptions options) {
        super(options);
    }

    @Override
    public String askUserPassword(String prompt) {
        // Return an empty password; the user should supply the correct password in plugin configuration.
        return "";
    }
}
