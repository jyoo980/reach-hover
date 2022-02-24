package com.github.jyoo980.reachhover.ui

import com.github.jyoo980.reachhover.MyBundle
import com.intellij.icons.AllIcons
import javax.swing.JButton
import javax.swing.SwingConstants

// TODO: May need to parameterize this with a PsiElement to properly register "Show Docs" action.
class ShowDocumentationButton {

    // Text of this button reads: "Show types and documentation?"
    private val defaultButtonText: String = MyBundle.message("showDocumentation")

    val ui: JButton =
        JButton(AllIcons.Toolwindows.Documentation).also {
            it.horizontalAlignment = SwingConstants.LEFT
            it.isBorderPainted = false
            it.isContentAreaFilled = false
        }

    fun setButtonText(optText: String? = null) {
        this.ui.text = optText ?: this.defaultButtonText
    }

    fun activateAction() {
        this.ui.addActionListener {
            // TODO: wire this up.
        }
    }
}
