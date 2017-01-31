package nl.guno.collab.intellij;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.actions.VcsContext;
import com.intellij.openapi.vcs.actions.VcsContextFactory;
import com.intellij.openapi.vcs.changes.*;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

final class PluginUtil {

    private static final Pattern urlPattern = Pattern.compile("https?://([^/:\\?&#]*):?.*");

    private PluginUtil() {
    }

	@Nullable
    private static VirtualFile[] getCurrentVirtualFiles(@NotNull DataContext dataContext) {
        return CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);
    }

    @NotNull
    static File[] getCurrentFiles(@NotNull DataContext dataContext) {
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
    static File[] getSelectedFiles(@NotNull AnActionEvent actionEvent) {
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

    @Nullable
    static String getSelectedChangesetName(@NotNull AnActionEvent actionEvent) {

        VcsContext vcsContext = VcsContextFactory.SERVICE.getInstance().createContextOn(actionEvent);
        ChangeList[] changeLists = vcsContext.getSelectedChangeLists();

        if (changeLists == null || changeLists.length != 1) {
            return null;
        }

        return changeLists[0].getName();
    }

    @Nullable
    static String getChangesetNameOfFirstSelectedFile(@NotNull AnActionEvent actionEvent) {

        VcsContext vcsContext = VcsContextFactory.SERVICE.getInstance().createContextOn(actionEvent);
        Change[] changes = vcsContext.getSelectedChanges();

        if (changes == null || changes.length == 0) {
            return null;
        }

        ChangeList changeList = ChangeListManager.getInstance(actionEvent.getProject()).getChangeList(changes[0]);

        if (changeList == null) {
            return null;
        }

        return changeList.getName();
    }

    @Nullable
    static String getActiveChangesetName(Project project) {

        for (ChangeList changeList : ChangeListManager.getInstance(project).getChangeLists()) {
            if (changeList instanceof LocalChangeList && ((LocalChangeList)changeList).isDefault()) {
                return changeList.getName();
            }
        }

        return null;
    }

    @Nullable
    static String extractHostFromUrl(@NotNull String url) {
        Matcher matcher = urlPattern.matcher(url);
        if (!matcher.matches()) {
            return null;
        }

        return  matcher.group(1);
    }

    static void openLogDirectory() {
        AnAction action = ActionManager.getInstance().getAction("ShowLog");
        action.actionPerformed(AnActionEvent.createFromAnAction(action, null, "", DataManager.getInstance().getDataContext()));
    }
}
