package com.github.jyoo980.reachhover.ui

import com.github.jyoo980.reachhover.util.isLocalVariableReference
import com.github.jyoo980.reachhover.util.isNonLiteralMethodArg
import com.intellij.psi.PsiElement
import java.awt.GridLayout
import javax.swing.JPanel

class ReachabilityPopupBuilder {

    // This is the element that the end-user sees when hovering over an element.
    // Visually, it is a panel with two rows. The first row represents the
    // Reachability analysis available (where did a value come from/how will a value be modified?).
    // The second row will be an option to show types/documentation (the unmodified behaviour of
    // IntelliJ).

    fun constructPopupFor(element: PsiElement): JPanel {
        // TODO: Use extension functions in PsiElement to make the proper calls here depending on
        // type.
        // TODO: Eventually register custom actions using ReachabilityButton#activateAction.
        // TODO: Eventually register custom actions using ShowDocumentationButton#activateAction.
        val reachabilityButton = createReachabilityButton(element)
        val showDocumentationButton = ShowDocumentationButton().apply { setButtonText() }
        val panel = JPanel(GridLayout(2, 1))
        reachabilityButton?.also { panel.add(it.ui) }
        panel.add(showDocumentationButton.ui)
        return panel
    }

    private fun createReachabilityButton(element: PsiElement): ReachabilityButton? {
        val reachabilityButton =
            if (element.isLocalVariableReference()) {
                ForwardReachabilityButton(element)
            } else if (element.isNonLiteralMethodArg()) {
                BackwardReachabilityButton(element)
            } else null
        return reachabilityButton?.apply { setButtonText() }
    }
}