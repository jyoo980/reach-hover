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
    val optParentMethodCallExpr =
        PsiTreeUtil.getParentOfType(this, PsiMethodCallExpression::class.java)
    // Need to check if one of the element's parents is a method list. Otherwise, we get items like
    // a.foo() being erroneously reported.
    val optParentExprList = PsiTreeUtil.getParentOfType(this, PsiExpressionList::class.java)
    return PsiTreeUtil.instanceOf(this, PsiIdentifier::class.java) &&
        optParentMethodCallExpr != null &&
        optParentExprList != null
}

/**
 * Extension method on PsiElement.
 *
 * @return true iff the element is an instance of a local variable reference.
 */
fun PsiElement.isLocalVariableReference(): Boolean {
    val isParentLocalVar =
        this.parent?.let { PsiTreeUtil.instanceOf(it, PsiLocalVariable::class.java) } ?: false
    return isParentLocalVar && PsiTreeUtil.instanceOf(this, PsiIdentifier::class.java)
}
