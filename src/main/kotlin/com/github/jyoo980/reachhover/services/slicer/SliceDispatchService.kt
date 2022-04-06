package com.github.jyoo980.reachhover.services.slicer

import com.intellij.analysis.AnalysisScope
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.slicer.*

object SliceDispatchService {

    fun expressionContainingElement(
        elementAtHover: PsiElement,
        isForwardSlice: Boolean
    ): PsiElement? {
        val provider = LanguageSlicing.getProvider(elementAtHover.containingFile)
        return provider?.getExpressionAtCaret(elementAtHover, isForwardSlice)
    }

    fun sliceRootUsage(
        elementAtHover: PsiElement,
        project: Project,
        isForwardSlice: Boolean
    ): SliceRootNode {
        val params = defaultAnalysisParams(elementAtHover, isForwardSlice)
        return SliceRootNode(
            project,
            DuplicateMap(),
            LanguageSlicing.getProvider(elementAtHover).createRootUsage(elementAtHover, params)
        )
    }

    private fun defaultAnalysisParams(
        element: PsiElement,
        isForwardSlice: Boolean
    ): SliceAnalysisParams {
        return SliceAnalysisParams().apply {
            dataFlowToThis = !isForwardSlice
            showInstanceDereferences = true
            scope = AnalysisScope(element.project)
        }
    }
}
