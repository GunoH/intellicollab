package com.quinity.ccollab.intellij;

import com.intellij.psi.PsiAnonymousClass;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.project.Project;

public final class PluginUtil {
	private PluginUtil() {
	}

	public static PsiFile getCurrentFile(DataContext dataContext) {
		return DataKeys.PSI_FILE.getData(dataContext);
	}

	public static VirtualFile[] getCurrentVirtualFiles(DataContext dataContext) {
		return DataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);
	}

	public static PsiClass getCurrentClass(DataContext dataContext) {
		return findClass(getCurrentElement(dataContext));
	}

	public static PsiField getCurrentField(DataContext dataContext) {
		return findField(getCurrentElement(dataContext));
	}

	public static PsiMethod getCurrentMethod(DataContext dataContext) {
		return findMethod(getCurrentElement(dataContext));
	}
	
	public static Project getProject(DataContext dataContext) {
		return DataKeys.PROJECT.getData(dataContext);
	}

	public static SelectionModel getCurrentSelection(DataContext dataContext) {
		Editor editor = DataKeys.EDITOR.getData(dataContext);
		if (editor != null) {
			SelectionModel model = editor.getSelectionModel();
			if (model.hasSelection()) {
				return model;
			}
		}
		return null;
	}

	private static PsiElement getCurrentElement(DataContext dataContext) {
		// Try directly on dataContext

		PsiElement psiElement = DataKeys.PSI_ELEMENT.getData(dataContext);
		if (psiElement != null) {
			// success
			return psiElement;
		}

		// Try through editor + PsiFile
		Editor editor = DataKeys.EDITOR.getData(dataContext);
		PsiFile psiFile = getCurrentFile(dataContext);
		if (editor != null && psiFile != null) {
			return psiFile.findElementAt(editor.getCaretModel().getOffset());
		}
		// Unable to find currentElement
		return null;
	}

	private static PsiClass findClass(PsiElement element) {
		PsiClass psiClass = (element instanceof PsiClass) ? (PsiClass) element :
				PsiTreeUtil.getParentOfType(element, PsiClass.class);
		if (psiClass instanceof PsiAnonymousClass) {
			return findClass(psiClass.getParent());
		}
		return psiClass;
	}

	private static PsiField findField(PsiElement element) {
		PsiField psiField = (element instanceof PsiField) ? (PsiField) element :
				PsiTreeUtil.getParentOfType(element, PsiField.class);
		if (psiField != null && psiField.getContainingClass() instanceof PsiAnonymousClass) {
			return findField(psiField.getParent());
		}
		return psiField;
	}

	private static PsiMethod findMethod(PsiElement element) {
		PsiMethod method = (element instanceof PsiMethod) ? (PsiMethod) element :
				PsiTreeUtil.getParentOfType(element, PsiMethod.class);
		if (method != null && method.getContainingClass() instanceof PsiAnonymousClass) {
			return findMethod(method.getParent());
		}
		return method;
	}
}