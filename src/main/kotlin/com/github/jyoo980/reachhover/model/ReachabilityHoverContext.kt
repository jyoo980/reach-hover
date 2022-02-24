package com.github.jyoo980.reachhover.model

import com.github.jyoo980.reachhover.util.isLocalVariableReference
import com.github.jyoo980.reachhover.util.isNonLiteralMethodArg
import com.intellij.psi.PsiElement
import com.intellij.ui.awt.RelativePoint

data class ReachabilityHoverContext(val elementToInspect: PsiElement, val location: RelativePoint) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReachabilityHoverContext

        if (elementToInspect != other.elementToInspect) return false

        return true
    }

    override fun hashCode(): Int {
        return elementToInspect.hashCode()
    }

    fun isValidElementForAnalysis(): Boolean {
        return elementToInspect.isLocalVariableReference() ||
            elementToInspect.isNonLiteralMethodArg()
    }
}
