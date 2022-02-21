package com.github.jyoo980.reachhover.ui

import com.github.jyoo980.reachhover.MyBundle
import com.intellij.icons.AllIcons
import javax.swing.JButton
import javax.swing.SwingConstants

// TODO: May need to parameterize this with a PsiElement to properly register "Show Docs" action.
class ShowDocumentationButton {

    val ui: JButton =
        JButton(AllIcons.Toolwindows.Documentation).also {
            it.horizontalAlignment = SwingConstants.LEFT
            it.isBorderPainted = false
            it.isContentAreaFilled = false
        }

    fun setButtonText() {
        this.ui.text = MyBundle.message("showDocumentation")
    }

    fun activateAction() {
        this.ui.addActionListener {
            // TODO: wire this up.
        }
    }
}
