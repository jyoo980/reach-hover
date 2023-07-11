package com.github.jyoo980.reachhover.actions

import com.github.jyoo980.reachhover.model.ReachabilityContext
import com.github.jyoo980.reachhover.ui.ReachabilityPanel
import com.intellij.codeInsight.documentation.DocumentationManager
import com.intellij.codeInsight.lookup.LookupManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.popup.ActiveIcon
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.psi.PsiElement
import com.intellij.reference.SoftReference
import com.intellij.slicer.SliceNode
import com.intellij.ui.awt.RelativePoint
import com.intellij.ui.popup.PopupUpdateProcessor
import com.intellij.util.ui.JBDimension
import icons.IconManager
import java.lang.ref.Reference
import java.lang.ref.WeakReference

class ShowReachabilityElementsAction {

    private var popupRef: Reference<JBPopup>? = null
    private val minimumPopupSize: JBDimension = JBDimension(566, 717, false)

    fun performForContext(
        context: ReachabilityContext,
        root: SliceNode,
        dataflowFromHere: Boolean,
        location: RelativePoint
    ) {
        val (editor, elementUnderAnalysis, questionText) = context
        showReachabilitySession(
            editor,
            elementUnderAnalysis,
            root,
            questionText,
            dataflowFromHere,
            location
        )
    }

    private fun showReachabilitySession(
        editor: Editor,
        elementUnderAnalysis: PsiElement,
        root: SliceNode,
        questionText: String,
        dataflowFromHere: Boolean,
        location: RelativePoint
    ) {
        var popup = SoftReference.dereference(popupRef)
        val project = editor.project ?: return
        val viewComponent =
            object :
                ReachabilityPanel(
                    elementUnderAnalysis,
                    project,
                    dataflowFromHere,
                    root,
                    false,
                ) {
                override var isAutoScroll: Boolean = false
            }
        val updateProcessor =
            object : PopupUpdateProcessor(editor.project) {
                override fun updatePopup(lookupItemObject: Any?) {
                    // TODO: implement this?
                }
            }
        val popupBuilder =
            JBPopupFactory.getInstance()
                .createComponentPopupBuilder(viewComponent, viewComponent)
                .setProject(editor.project)
                .addListener(updateProcessor)
                .addUserData(updateProcessor)
                .setCancelOnWindowDeactivation(false)
                .setCancelOnOtherWindowOpen(false)
                .setTitle(questionText)
                .setTitleIcon(IconManager.reachabilityIcon.let(::ActiveIcon))
                .setDimensionServiceKey(
                    editor.project,
                    DocumentationManager.JAVADOC_LOCATION_AND_SIZE,
                    false
                )
                .setResizable(true)
                .setMovable(true)
                .setMinSize(minimumPopupSize)
                .setRequestFocus(LookupManager.getActiveLookup(editor) != null)
        popup = popupBuilder.createPopup()
        popup.show(location)
        popupRef = WeakReference(popup)
    }
}
