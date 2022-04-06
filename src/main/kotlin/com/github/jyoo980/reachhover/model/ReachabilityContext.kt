package com.github.jyoo980.reachhover.model

import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement

data class ReachabilityContext(
    val editor: Editor,
    val element: PsiElement,
    val questionText: String,
)
