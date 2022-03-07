package com.github.jyoo980.reachhover.services.slicer

import com.github.jyoo980.reachhover.services.TreeBuilder
import com.intellij.analysis.AnalysisScope
import com.intellij.lang.LangBundle
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.slicer.*
import java.util.regex.Pattern

object SliceDispatchService : TreeBuilder {

    fun expressionContainingElement(
        elementAtHover: PsiElement,
        isForwardSlice: Boolean
    ): PsiElement? {
        val provider = LanguageSlicing.getProvider(elementAtHover.containingFile)
        return provider?.getExpressionAtCaret(elementAtHover, isForwardSlice)
    }

    fun elementDescription(elementAtHover: PsiElement, isForwardSlice: Boolean): String {
        // [directionPrefix] depends on whether a forward/backward analysis is performed.
        val key =
            if (isForwardSlice) "tab.title.analyze.dataflow.from"
            else "tab.title.analyze.dataflow.to.here"
        val directionPrefix = LangBundle.message(key)
        val dialogWindowTitle =
            SliceManager.getElementDescription(directionPrefix, elementAtHover, null)
        return Pattern.compile("(<style>.*</style>)|<[^<>]*>", Pattern.DOTALL)
            .matcher(dialogWindowTitle)
            .replaceAll("")
    }

    fun sliceRootUsage(
        elementAtHover: PsiElement,
        project: Project,
        isForwardSlice: Boolean
    ): SliceRootNode {
        val params = defaultAnalysisParams(elementAtHover.containingFile, isForwardSlice)
        return SliceRootNode(
            project,
            DuplicateMap(),
            LanguageSlicing.getProvider(elementAtHover).createRootUsage(elementAtHover, params)
        )
    }

    private fun defaultAnalysisParams(
        fileContainingElement: PsiFile,
        isForwardSlice: Boolean
    ): SliceAnalysisParams {
        return SliceAnalysisParams().apply {
            dataFlowToThis = !isForwardSlice
            showInstanceDereferences = true
            scope = AnalysisScope(fileContainingElement)
        }
    }
}
