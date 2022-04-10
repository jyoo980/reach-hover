package com.github.jyoo980.reachhover.actions

import com.github.jyoo980.reachhover.model.ReachabilityContext
import com.github.jyoo980.reachhover.ui.ReachabilityPanel
import com.intellij.codeInsight.documentation.DocumentationManager
import com.intellij.codeInsight.lookup.LookupManager
import com.intellij.ide.DataManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.popup.ActiveIcon
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.psi.PsiElement
import com.intellij.reference.SoftReference
import com.intellij.slicer.SliceNode
import com.intellij.ui.popup.PopupPositionManager
import com.intellij.ui.popup.PopupUpdateProcessor
import icons.IconManager
import java.lang.ref.Reference
import java.lang.ref.WeakReference

class ShowReachabilityElementsAction {

    private var popupRef: Reference<JBPopup>? = null

    fun performForContext(
        context: ReachabilityContext,
        root: SliceNode,
        dataflowFromHere: Boolean
    ) {
        val (editor, elementUnderAnalysis, questionText) = context
        showReachabilitySession(editor, elementUnderAnalysis, root, questionText, dataflowFromHere)
    }

    private fun showReachabilitySession(
        editor: Editor,
        elementUnderAnalysis: PsiElement,
        root: SliceNode,
        questionText: String,
        dataflowFromHere: Boolean
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

        viewComponent.setSize(420, 200)
        val popupBuilder =
            JBPopupFactory.getInstance()
                .createComponentPopupBuilder(viewComponent, viewComponent)
                .setProject(editor.project)
                .addListener(updateProcessor)
                .addUserData(updateProcessor)
                .setTitle(questionText)
                .setTitleIcon(IconManager.reachabilityIcon.let(::ActiveIcon))
                .setDimensionServiceKey(
                    editor.project,
                    DocumentationManager.JAVADOC_LOCATION_AND_SIZE,
                    false
                )
                .setResizable(true)
                .setMovable(true)
                .setRequestFocus(LookupManager.getActiveLookup(editor) != null)

        popup = popupBuilder.createPopup()
        PopupPositionManager.positionPopupInBestPosition(
            popup,
            editor,
            DataManager.getInstance().getDataContext()
        )
        popupRef = WeakReference(popup)
    }
}
