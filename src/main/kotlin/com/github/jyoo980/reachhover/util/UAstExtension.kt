package com.github.jyoo980.reachhover.util

import org.jetbrains.uast.*

/**
 * Extension method on UElement.
 *
 * INVARIANT: In the event that the cursor is hovering over an anon. class or lambda expr, the
 * location of the first UCallExpression object must be closer than that of the first
 * UAnonymousClass or ULambdaExpression, otherwise we have UElements that are not method arguments
 * being reported as such.
 *
 * @return true iff the element is an instance of a non-literal method argument, e.g., foo(a, 1),
 * a.isNonLiteralMethodArg() -> true e.g., foo(a, 1), 1.isNonLiteralMethodArg() -> false
 */
fun UElement.isNonLiteralMethodArg(): Boolean {
    val parents = collectParents(stopWhen = { it is UFile })
    val locationOfCallExpr =
        parents.indexOfFirst { it is UCallExpression }.takeIf { it >= 0 } ?: return false
    val isIdentifier = this is UIdentifier
    return parents
        .indexOfFirst { it is UAnonymousClass || it is ULambdaExpression }
        .takeIf { it >= 0 }
        ?.let { it -> isIdentifier && locationOfCallExpr < it }
        ?: isIdentifier
}

/**
 * Extension method on PsiElement.
 *
 * @return true iff the element is an instance of a local variable reference.
 */
fun UElement.isLocalVariableReference(): Boolean {
    return (this is UIdentifier) && (this.uastParent is ULocalVariable)
}

fun UElement.presentableName(): String? {
    return (this as? UIdentifier)?.name
}

private fun UElement.collectParents(stopWhen: (UElement) -> Boolean): List<UElement> {
    val parents = mutableListOf<UElement>()
    var parent = uastParent ?: return parents.toList()
    while (true) {
        if (stopWhen(parent)) {
            return parents
        }
        parents.add(parent)
        parent = parent.uastParent ?: return parents.toList()
    }
}
