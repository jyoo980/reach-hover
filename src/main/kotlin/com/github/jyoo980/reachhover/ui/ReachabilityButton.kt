package com.github.jyoo980.reachhover.ui

import com.github.jyoo980.reachhover.MyBundle
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import java.awt.Desktop
import java.net.URI
import javax.swing.JButton
import javax.swing.SwingConstants

sealed class ReachabilityButton(element: PsiElement) {

    private val elementUnderCursor = element
    // TODO: Either come up with a custom icon, or find a way to resize (QuestionDialog is 32x32, we
    // need 16x16).
    val button: JButton =
        JButton(AllIcons.General.QuestionDialog).also {
            it.horizontalAlignment = SwingConstants.LEFT
            it.isBorderPainted = false
            it.isContentAreaFilled = false
        }

    abstract fun setButtonText()

    open fun activateAction(editor: Editor) {
        // TODO: stub behaviour, link this to actual reachability action later. Should be an
        // abstract fun once we're done.
        this.button.addActionListener {
            Desktop.getDesktop().browse(URI("https://www.youtube.com/watch?v=dQw4w9WgXcQ"))
        }
    }

    protected fun optIdentifierName(): String? {
        return (this.elementUnderCursor as? PsiIdentifier)?.text?.let {
            "<span style=\"font-family:JetBrains Mono;\">$it</font></span>"
        }
    }
}

class ForwardReachabilityButton(element: PsiElement) : ReachabilityButton(element) {

    override fun setButtonText() {
        val nameOfArgumentToInspect = this.optIdentifierName() ?: "this argument"
        val textToSet = MyBundle.message("createdQuestion", nameOfArgumentToInspect)
        this.button.text = "<html>$textToSet</html>"
    }

    override fun activateAction(editor: Editor) {
        this.button.addActionListener {
            // TODO: wire this up.
        }
    }
}
