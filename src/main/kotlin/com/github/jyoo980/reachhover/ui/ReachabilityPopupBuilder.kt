package com.github.jyoo980.reachhover.ui

import com.github.jyoo980.reachhover.model.ReachabilityHoverContext
import com.intellij.psi.PsiElement
import java.awt.GridLayout
import javax.swing.JPanel

class ReachabilityPopupBuilder {

    // This is the element that the end-user sees when hovering over an element.
    // Visually, it is a panel with two rows. The first row represents the
    // Reachability analysis available (where did a value come from/how will a value be modified?).
    // The second row will be an option to show types/documentation (the unmodified behaviour of
    // IntelliJ).

    fun constructPopupFor(reachabilityContext: ReachabilityHoverContext): JPanel {
        val (element, location, editor, isForwardAnalysis, identifierName, offset) =
            reachabilityContext
        val reachabilityButton =
            createReachabilityButton(element, isForwardAnalysis).apply {
                setButtonText(identifierName)
                activateAction(editor, location)
            }
        val showDocumentationButton =
            ShowDocumentationButton(element).apply {
                setButtonText()
                activateAction(offset, editor, location)
            }
        return JPanel(GridLayout(2, 1)).apply {
            reachabilityButton.let { this.add(it.ui) }
            add(showDocumentationButton.ui)
        }
    }

    private fun createReachabilityButton(
        element: PsiElement,
        isForwardAnalysis: Boolean
    ): ReachabilityButton {
        return if (isForwardAnalysis) {
            ForwardReachabilityButton(element)
        } else {
            BackwardReachabilityButton(element)
        }
    }
}
