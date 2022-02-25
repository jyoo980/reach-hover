package com.github.jyoo980.reachhover.model

import com.intellij.psi.PsiElement
import com.intellij.ui.awt.RelativePoint

data class ReachabilityHoverContext(val elementToInspect: PsiElement, val location: RelativePoint)
