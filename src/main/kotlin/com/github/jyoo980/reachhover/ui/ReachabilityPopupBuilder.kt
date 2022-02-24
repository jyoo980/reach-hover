package com.github.jyoo980.reachhover.ui

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
        val panel = JPanel(GridLayout(2, 1))
        val reachabilityButton = BackwardReachabilityButton(element).also { it.setButtonText() }
        val showDocumentationButton = ShowDocumentationButton().also { it.setButtonText() }
        return JPanel(GridLayout(2, 1)).also {
            it.add(reachabilityButton.ui)
            it.add(showDocumentationButton.ui)
        }
    }
}
