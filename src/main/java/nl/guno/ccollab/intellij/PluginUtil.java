package nl.guno.ccollab.intellij;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.vcs.actions.VcsContext;
import com.intellij.openapi.vcs.actions.VcsContextFactory;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class PluginUtil {

    private PluginUtil() {
    }

	@Nullable
    private static VirtualFile[] getCurrentVirtualFiles(@NotNull DataContext dataContext) {
        return DataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);
    }

    @NotNull
    public static File[] getCurrentFiles(@NotNull DataContext dataContext) {
        VirtualFile[] virtualFiles = getCurrentVirtualFiles(dataContext);

        if (virtualFiles == null) {
            return new File[0];
        }

        File[] files = new File[virtualFiles.length];
        for (int i = 0; i < virtualFiles.length; i++) {
            files[i] = VfsUtil.virtualToIoFile(virtualFiles[i]);
        }
        return files;
    }

    @NotNull
    public static File[] getSelectedFiles(@NotNull AnActionEvent actionEvent) {
        VcsContext vcsContext = VcsContextFactory.SERVICE.getInstance().createContextOn(actionEvent);
        Change[] changes = vcsContext.getSelectedChanges();
        if (changes == null) {
            return new File[0];
        }

        List<File> files = new ArrayList<File>();

        for (Change change : changes) {
            Change.Type changeType = change.getType();
            ContentRevision revision;
            if (changeType == Change.Type.DELETED) {
                // The file was deleted, so we use the revision that was active before the deletion.
                revision = change.getBeforeRevision();
            } else {
                // In all other cases, we use the new revision.
                revision = change.getAfterRevision();
            }
            if (revision != null) {
                files.add(revision.getFile().getIOFile());
            }
        }

        return files.toArray(new File[files.size()]);
    }
}