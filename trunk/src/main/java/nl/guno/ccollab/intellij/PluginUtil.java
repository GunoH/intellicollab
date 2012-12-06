package nl.guno.ccollab.intellij;

import java.io.File;

import com.intellij.cvsSupport2.actions.cvsContext.CvsContextWrapper;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeList;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.changes.ui.ChangesViewContentManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiAnonymousClass;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;

public final class PluginUtil {
    private PluginUtil() {
    }

	public static VirtualFile[] getCurrentVirtualFiles(DataContext dataContext) {
        return DataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);
    }

    public static File[] getCurrentFiles(DataContext dataContext) {
        VirtualFile[] virtualFiles = getCurrentVirtualFiles(dataContext);

        File[] files = new File[virtualFiles.length];
        for (int i = 0; i < virtualFiles.length; i++) {
            files[i] = VfsUtil.virtualToIoFile(virtualFiles[i]);
        }
        return files;
    }

    public static File[] getSelectedFiles(AnActionEvent actionEvent) {
        Change[] changes = CvsContextWrapper.createInstance(actionEvent).getSelectedChanges();

        File[] files = new File[changes.length];

        for (int i = 0; i < changes.length; i++) {
            Change.Type changeType = changes[i].getType();
            ContentRevision revision;
            if (changeType == Change.Type.DELETED) {
                // The file was deleted, so we use the revision that was active before the deletion.
                revision = changes[i].getBeforeRevision();
            } else {
                // In all other cases, we use the new revision.
                revision = changes[i].getAfterRevision();
            }
            files[i] = revision.getFile().getIOFile();
        }

        return files;
    }

	public static void createBalloon(Project project, String message, MessageType type) {

        ToolWindowManager.getInstance(project).notifyByBalloon(ChangesViewContentManager.TOOLWINDOW_ID, type, message);

    }
}