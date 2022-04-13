package com.github.jyoo980.reachhover.ui.scope

import com.intellij.analysis.AnalysisScope
import com.intellij.psi.PsiElement
import com.intellij.util.ui.EmptyIcon
import javax.swing.Icon

abstract class Scope(val name: String, val icon: Icon = EmptyIcon.ICON_0) {
    abstract fun analysisScope(element: PsiElement): AnalysisScope
}

object Project : Scope(name = "Project") {
    override fun analysisScope(element: PsiElement): AnalysisScope = AnalysisScope(element.project)
}

object Directory : Scope(name = "Directory") {
    override fun analysisScope(element: PsiElement): AnalysisScope =
        AnalysisScope(element.containingFile.containingDirectory)
}

object File : Scope(name = "File") {
    override fun analysisScope(element: PsiElement): AnalysisScope =
        AnalysisScope(element.containingFile)
}
