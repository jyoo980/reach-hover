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
        // TODO: Eventually register custom actions using ReachabilityButton#activateAction.
        // TODO: Eventually register custom actions using ShowDocumentationButton#activateAction.
        val panel = JPanel(GridLayout(2, 1))
        val optReachabilityButton = createReachabilityButton(element)
        val showDocumentationButton = ShowDocumentationButton().also { it.setButtonText() }
        optReachabilityButton?.also { panel.add(it.ui) }
        panel.add(showDocumentationButton.ui)
        return panel
    }

    private fun createReachabilityButton(element: PsiElement): ReachabilityButton? {
        val optButton =
            if (element.isNonLiteralMethodArg()) {
                ForwardReachabilityButton(element)
            } else if (element.isLocalVariableReference()) {
                BackwardReachabilityButton(element)
            } else null
        return optButton?.also {
            it.setButtonText()
            // TODO: call it.activateAction() eventually.
        }
    }
}
