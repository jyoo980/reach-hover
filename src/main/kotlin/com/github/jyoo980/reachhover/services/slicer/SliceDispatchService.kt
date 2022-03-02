package com.github.jyoo980.reachhover.services.slicer

import com.intellij.analysis.AnalysisScope
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.slicer.*

object SliceDispatchService {

    fun createSliceUsage(
        project: Project,
        elementAtCaret: PsiElement,
        isForwardSlice: Boolean
    ): SliceRootNode? {
        return expressionToAnalyze(elementAtCaret, isForwardSlice)?.let {
            val params = analysisParams(elementAtCaret, isForwardSlice)
            val rootUsage =
                LanguageSlicing.getProvider(elementAtCaret).createRootUsage(elementAtCaret, params)
            SliceRootNode(project, DuplicateMap(), rootUsage)
        }
    }

    private fun expressionToAnalyze(
        elementAtCaret: PsiElement,
        isForwardSlice: Boolean
    ): PsiElement? {
        return LanguageSlicing.getProvider(elementAtCaret)
            .getExpressionAtCaret(elementAtCaret, !isForwardSlice)
            ?.takeIf { it.isPhysical }
    }

    // TODO: enable wider scope of analysis, currently limited to files.
    private fun analysisParams(element: PsiElement, isForwardSlice: Boolean): SliceAnalysisParams {
        return SliceAnalysisParams().apply {
            scope = AnalysisScope(element.containingFile)
            dataFlowToThis = isForwardSlice
        }
    }
}
