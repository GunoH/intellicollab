package com.quinity.ccollab.intellij;

import com.intellij.psi.PsiAnonymousClass;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;

public final class PluginUtil {
   private PluginUtil() { }

   public static PsiFile getCurrentFile(DataContext dataContext) {
      return (PsiFile) dataContext.getData(DataConstants.PSI_FILE);
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

   public static SelectionModel getCurrentSelection(DataContext dataContext) {
      Editor editor = (Editor) dataContext.getData(DataConstants.EDITOR);
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
      PsiElement psiElement = (PsiElement) dataContext.getData(DataConstants.PSI_ELEMENT);
      if (psiElement != null) {
         // success
         return psiElement;
      }

      // Try through editor + PsiFile
      Editor editor = (Editor) dataContext.getData(DataConstants.EDITOR);
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