package com.github.jyoo980.reachhover.ui

import com.github.jyoo980.reachhover.MyBundle
import com.github.jyoo980.reachhover.services.slicer.SliceDispatchService
import com.github.jyoo980.reachhover.services.tree.TreeBuilder
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.slicer.SliceHandler
import com.intellij.slicer.SliceManager
import icons.IconManager
import javax.swing.JButton
import javax.swing.SwingConstants

sealed class ReachabilityButton(element: PsiElement) {

    protected open val dataflowFromHere = false
    private val elementUnderCursor = element
    // TODO: Either come up with a custom icon, or find a way to resize (QuestionDialog is 32x32, we
    // need 16x16).
    val ui: JButton =
        JButton(IconManager.reachabilityIcon).apply {
            horizontalAlignment = SwingConstants.LEFT
            isBorderPainted = false
            isContentAreaFilled = false
        }

    abstract fun setButtonText(text: String? = null)

    fun activateAction(editor: Editor) {
        editor.project?.also { project ->
            ui.addActionListener {
                val handler = SliceHandler.create(!dataflowFromHere)
                val expressionToAnalyze =
                    SliceDispatchService.expressionContainingElement(
                        elementUnderCursor,
                        !dataflowFromHere
                    )
                expressionToAnalyze?.let { expr ->
                    // TODO: open a new window here right next to the popup
                    val sliceRoot =
                        SliceDispatchService.sliceRootUsage(expr, project, dataflowFromHere)
                    val tree = TreeBuilder.treeFrom(sliceRoot)
                    SliceManager.getInstance(project).slice(expr, dataflowFromHere, handler)
                }
            }
        }
    }

    protected fun identifierName(): String? {
        return (elementUnderCursor as? PsiIdentifier)?.text?.let {
            "<span style=\"font-family:JetBrains Mono;\">$it</font></span>"
        }
    }
}

class BackwardReachabilityButton(element: PsiElement) : ReachabilityButton(element) {

    override val dataflowFromHere = false

    override fun setButtonText(text: String?) {
        val textToSet =
            text
                ?: run {
                    val nameOfArgumentToInspect = identifierName() ?: "this argument"
                    val fullText = MyBundle.message("createdQuestion", nameOfArgumentToInspect)
                    "<html>$fullText</html"
                }
        ui.text = textToSet
    }
}

class ForwardReachabilityButton(element: PsiElement) : ReachabilityButton(element) {

    override val dataflowFromHere = true

    override fun setButtonText(text: String?) {
        val textToSet =
            text
                ?: run {
                    val nameOfArgumentToInspect = identifierName() ?: "this value"
                    val fullText = MyBundle.message("modifiedQuestion", nameOfArgumentToInspect)
                    "<html>$fullText</html>"
                }
        ui.text = textToSet
    }
}
