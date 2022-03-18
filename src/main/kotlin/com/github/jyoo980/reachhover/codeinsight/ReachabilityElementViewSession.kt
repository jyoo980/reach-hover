package com.github.jyoo980.reachhover.codeinsight

import com.github.jyoo980.reachhover.model.Tree
import com.github.jyoo980.reachhover.model.flatten
import com.github.jyoo980.reachhover.ui.ReachabilityViewElement
import com.intellij.codeInsight.hint.ImplementationViewElement
import com.intellij.codeInsight.hint.ImplementationViewSession
import com.intellij.codeInsight.hint.ImplementationViewSessionFactory
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.pom.PomTargetPsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.presentation.java.SymbolPresentationUtil
import com.intellij.util.Processor

class ReachabilityElementViewSession(
    private val myEditor: Editor?,
    private val elementUnderInspection: PsiElement,
    private val reachabilityElements: Tree<ReachabilityViewElement>
) : ImplementationViewSession {

    override val editor: Editor?
        get() = myEditor

    override val factory: ImplementationViewSessionFactory
        get() =
            ImplementationViewSessionFactory.EP_NAME.findExtensionOrFail(
                ReachabilityElementViewSessionFactory::class.java
            )

    override val file: VirtualFile?
        get() = elementUnderInspection.containingFile.virtualFile

    override val implementationElements: List<ImplementationViewElement>
        get() = reachabilityElements.flatten()

    override val project: Project
        get() = editor?.project!!

    override val text: String?
        get() {
            return SymbolPresentationUtil.getSymbolPresentableText(elementUnderInspection)
                ?: (elementUnderInspection as? PsiNamedElement)?.name ?: elementUnderInspection.text
        }

    override fun dispose() {
        // Not required.
    }

    override fun elementRequiresIncludeSelf(): Boolean =
        elementUnderInspection !is PomTargetPsiElement

    override fun needUpdateInBackground(): Boolean = true

    override fun searchImplementationsInBackground(
        indicator: ProgressIndicator,
        processor: Processor<in ImplementationViewElement>
    ): List<ImplementationViewElement> = implementationElements
}
