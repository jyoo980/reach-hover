package com.github.jyoo980.reachhover.services.slicer

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
}
