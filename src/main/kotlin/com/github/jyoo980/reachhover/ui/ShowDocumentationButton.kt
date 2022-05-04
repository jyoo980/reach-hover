package com.github.jyoo980.reachhover.ui

import com.github.jyoo980.reachhover.MyBundle
import com.intellij.codeInsight.documentation.DocumentationManager
import com.intellij.codeInsight.lookup.LookupManager
import com.intellij.lang.documentation.ide.impl.IdeDocumentationTargetProviderImpl
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.psi.PsiElement
import com.intellij.ui.awt.RelativePoint
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBUI
import icons.IconManager
import javax.swing.JButton
import javax.swing.SwingConstants

// TODO: May need to parameterize this with a PsiElement to properly register "Show Docs" action.
class ShowDocumentationButton(private val element: PsiElement) {

    // Text of this button reads: "Show types and documentation?"
    private val defaultButtonText: String = MyBundle.message("showDocumentation")
    private val logger = Logger.getInstance(ShowDocumentationButton::class.java)

    val ui: JButton =
        JButton(IconManager.documentationIcon).apply {
            horizontalAlignment = SwingConstants.LEFT
            isBorderPainted = false
            isContentAreaFilled = false
        }

    fun setButtonText(optText: String? = null) {
        ui.text = optText ?: defaultButtonText
    }

    fun activateAction(offset: Int, editor: Editor, location: RelativePoint) {
        ui.addActionListener {
            val documentationTargetProvider = IdeDocumentationTargetProviderImpl(element.project)
            val targets =
                documentationTargetProvider.documentationTargets(
                    editor,
                    element.containingFile,
                    offset
                )
            targets.takeIf { it.isNotEmpty() }?.let {
                val targets = it.first()
                val hint = targets.computeDocumentationHint()
                val component = hint?.let(::JBLabel)
                component?.let { docComponent ->
                    docComponent.border = JBUI.Borders.empty(10)
                    val popupBuilder =
                        JBPopupFactory.getInstance()
                            .createComponentPopupBuilder(docComponent, docComponent)
                            .setProject(editor.project)
                            .setDimensionServiceKey(
                                editor.project,
                                DocumentationManager.JAVADOC_LOCATION_AND_SIZE,
                                false
                            )
                            .setResizable(true)
                            .setMovable(true)
                            .setRequestFocus(LookupManager.getActiveLookup(editor) != null)
                    val popup = popupBuilder.createPopup()
                    popup.show(location)
                }
            }
        }
    }
}
