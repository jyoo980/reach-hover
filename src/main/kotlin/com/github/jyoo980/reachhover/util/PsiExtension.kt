package com.github.jyoo980.reachhover.util

import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil

/**
 * Extension method on PsiElement.
 *
 * INVARIANT: In the event that the cursor is hovering over an anon. class or lambda expr, the
 * location of the first PsiExpressionList object must be closer than that of the first
 * PsiAnonymousClass or PsiLambdaExpression, otherwise we have PsiElements that are not method
 * arguments being reported as such.
 *
 * @return true iff the element is an instance of a non-literal method argument, e.g., foo(a, 1),
 * a.isNonLiteralMethodArg() -> true e.g., foo(a, 1), 1.isNonLiteralMethodArg() -> false
 */
fun PsiElement.isNonLiteralMethodArg(): Boolean {
    val parents = PsiTreeUtil.collectParents(this, PsiElement::class.java, false) { it is PsiFile }
    val locationOfExprList =
        parents.indexOfFirst { it is PsiExpressionList }.takeIf { it >= 0 } ?: return false
    val isPsiIdentifier = PsiTreeUtil.instanceOf(this, PsiIdentifier::class.java)
    return parents
        .indexOfFirst { it is PsiAnonymousClass || it is PsiLambdaExpression }
        .takeIf { it >= 0 }
        ?.let { it -> isPsiIdentifier && locationOfExprList < it }
        ?: isPsiIdentifier
}

/**
 * Extension method on PsiElement.
 *
 * @return true iff the element is an instance of a local variable reference.
 */
fun PsiElement.isLocalVariableReference(): Boolean {
    return (this is PsiIdentifier) && (parent is PsiLocalVariable)
}
