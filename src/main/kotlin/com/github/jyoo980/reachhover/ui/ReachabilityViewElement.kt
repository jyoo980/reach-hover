package com.github.jyoo980.reachhover.ui

import com.intellij.codeInsight.hint.ElementLocationUtil
import com.intellij.codeInsight.hint.ImplementationViewComponent
import com.intellij.codeInsight.hint.ImplementationViewElement
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Ref
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiBinaryFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.util.PsiTreeUtil
import javax.swing.Icon

class ReachabilityViewElement(private val element: PsiElement) : ImplementationViewElement() {

    override val project: Project = element.project

    override val isNamed: Boolean = element is PsiNamedElement

    override val name: String? = (element as? PsiNamedElement)?.name

    override val containingFile: VirtualFile? = element.containingFile?.originalFile?.virtualFile

    override val text: String? = ImplementationViewComponent.getNewText(element)

    override val presentableText: String
        get() {
            val presentation = (element as? NavigationItem)?.presentation
            val file = containingFile ?: return ""
            val presentableName = file.presentableName
            return presentation?.presentableText ?: return presentableName
        }

    override val containerPresentation: String?
        get() {
            val presentation = (element as? NavigationItem)?.presentation ?: return null
            return presentation.locationString
        }

    override val locationText: String?
        get() = ElementLocationUtil.renderElementLocation(element, Ref())

    override val locationIcon: Icon?
        get() = Ref<Icon>().also { ElementLocationUtil.renderElementLocation(element, it) }.get()

    override val containingMemberOrSelf: ImplementationViewElement
        get() {
            val parent = PsiTreeUtil.getStubOrPsiParent(element)
            if (parent == null || (parent is PsiFile && parent.virtualFile == containingFile)) {
                return this
            }
            return ReachabilityViewElement(parent)
        }

    override fun navigate(focusEditor: Boolean) {
        val navigationElement = element.navigationElement
        val file = navigationElement.containingFile?.originalFile ?: return
        val virtualFile = file.virtualFile ?: return
        val project = element.project
        val fileEditorManager = FileEditorManagerEx.getInstanceEx(project)
        val descriptor = OpenFileDescriptor(project, virtualFile, navigationElement.textOffset)
        fileEditorManager.openTextEditor(descriptor, focusEditor)
    }

    override val elementForShowUsages: PsiElement?
        get() = element.takeIf { it !is PsiBinaryFile }
}
