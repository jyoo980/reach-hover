package com.github.jyoo980.reachhover.ui

import com.github.jyoo980.reachhover.MyBundle
import com.github.jyoo980.reachhover.services.slicer.SliceDispatchService
import com.github.jyoo980.reachhover.services.tree.TreeBuilder
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import icons.IconManager
import javax.swing.JButton
import javax.swing.SwingConstants

sealed class ReachabilityButton(
    private val element: PsiElement,
    private val dataflowFromHere: Boolean
) {

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
                val expressionToAnalyze =
                    SliceDispatchService.expressionContainingElement(element, !dataflowFromHere)
                expressionToAnalyze?.let { expr ->
                    // TODO: open a new window here right next to the popup
                    val sliceRoot =
                        SliceDispatchService.sliceRootUsage(expr, project, dataflowFromHere)
                    val tree = TreeBuilder.treeFrom(sliceRoot)
                }
            }
        }
    }

    protected fun identifierName(): String? {
        return (element as? PsiIdentifier)?.text?.let {
            "<span style=\"font-family:JetBrains Mono;\">$it</font></span>"
        }
    }
}

class BackwardReachabilityButton(element: PsiElement) :
    ReachabilityButton(element, dataflowFromHere = false) {

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

class ForwardReachabilityButton(element: PsiElement) :
    ReachabilityButton(element, dataflowFromHere = true) {

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
