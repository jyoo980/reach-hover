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
    val ui: JButton =
        JButton(AllIcons.General.QuestionDialog).apply {
            horizontalAlignment = SwingConstants.LEFT
            isBorderPainted = false
            isContentAreaFilled = false
        }

    abstract fun setButtonText(optText: String? = null)

    open fun activateAction(editor: Editor) {
        // TODO: stub behaviour, link this to actual reachability action later. Should be an
        // abstract fun once we're done.
        ui.addActionListener {
            Desktop.getDesktop().browse(URI("https://www.youtube.com/watch?v=dQw4w9WgXcQ"))
        }
    }

    protected fun identifierName(): String? {
        return (elementUnderCursor as? PsiIdentifier)?.text?.let {
            "<span style=\"font-family:JetBrains Mono;\">$it</font></span>"
        }
    }
}

class BackwardReachabilityButton(element: PsiElement) : ReachabilityButton(element) {

    override fun setButtonText(optText: String?) {
        val textToSet =
            optText
                ?: kotlin.run {
                    val nameOfArgumentToInspect = identifierName() ?: "this argument"
                    val fullText = MyBundle.message("createdQuestion", nameOfArgumentToInspect)
                    "<html>$fullText</html"
                }
        ui.text = textToSet
    }

    override fun activateAction(editor: Editor) {
        ui.addActionListener {
            // TODO: wire this up.
        }
    }
}
class ForwardReachabilityButton(element: PsiElement) : ReachabilityButton(element) {

    override fun setButtonText(optText: String?) {
        // TODO: override this stub
    }

    override fun activateAction(editor: Editor) {
        ui.addActionListener {
            // TODO: wire this up.
        }
    }
}
