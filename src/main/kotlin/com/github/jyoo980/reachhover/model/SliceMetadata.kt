package com.github.jyoo980.reachhover.model

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

data class SliceMetadata(
    val element: PsiElement?,
    val file: PsiFile?,
    val lineInFile: Int?
)
