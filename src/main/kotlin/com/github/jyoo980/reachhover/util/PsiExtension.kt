package com.github.jyoo980.reachhover.util

import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil

/**
 * Extension method on PsiElement.
 *
 * @return true iff the element is an instance of a non-literal method argument, e.g., foo(a, 1),
 * a.isNonLiteralMethodArg() -> true e.g., foo(a, 1), 1.isNonLiteralMethodArg() -> false
 */
fun PsiElement.isNonLiteralMethodArg(): Boolean {
    PsiTreeUtil.getParentOfType(this, PsiMethodCallExpression::class.java) ?: return false
    PsiTreeUtil.getParentOfType(this, PsiExpressionList::class.java) ?: return false
    return PsiTreeUtil.instanceOf(this, PsiIdentifier::class.java)
}

/**
 * Extension method on PsiElement.
 *
 * @return true iff the element is an instance of a local variable reference.
 */
fun PsiElement.isLocalVariableReference(): Boolean {
    return (this is PsiIdentifier) && (this.parent is PsiLocalVariable)
}
