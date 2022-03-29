package com.github.jyoo980.reachhover.actions

import com.github.jyoo980.reachhover.codeinsight.ReachabilityElementViewSession
import com.github.jyoo980.reachhover.model.ReachabilityContext
import com.github.jyoo980.reachhover.model.map
import com.github.jyoo980.reachhover.ui.ReachabilityPanel
import com.github.jyoo980.reachhover.ui.ReachabilityViewElement
import com.intellij.codeInsight.documentation.DocumentationManager
import com.intellij.codeInsight.hint.ImplementationViewComponent
import com.intellij.codeInsight.hint.ImplementationViewSession
import com.intellij.codeInsight.lookup.LookupManager
import com.intellij.ide.DataManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.reference.SoftReference
import com.intellij.slicer.SliceNode
import com.intellij.ui.popup.AbstractPopup
import com.intellij.ui.popup.PopupPositionManager
import com.intellij.ui.popup.PopupUpdateProcessor
import java.lang.ref.Reference
import java.lang.ref.WeakReference

class ShowReachabilityElementsAction {

    private val logger: Logger = Logger.getInstance(ShowReachabilityElementsAction::class.java)
    private var popupRef: Reference<JBPopup>? = null

    fun performForContext(
        context: ReachabilityContext,
        root: SliceNode,
        dataflowFromHere: Boolean
    ) {
        val (editor, exprUnderInspection, tree) = context
        val reachabilityViewElements =
            tree.map { metadata -> metadata.element?.let { ReachabilityViewElement(it) }!! }
        ReachabilityElementViewSession(editor, exprUnderInspection, reachabilityViewElements).also {
            showReachabilitySession(it, editor, root, dataflowFromHere)
        }
    }

    private fun showReachabilitySession(
        session: ImplementationViewSession,
        editor: Editor,
        root: SliceNode,
        dataflowFromHere: Boolean
    ) {
        if (session.implementationElements.isNotEmpty()) {
            // TODO: come up with an actual title based on the element/expression under analysis
            val title = "Reachability View..."
            session.implementationElements.forEach {
                logger.info("ViewElement Impl: ${it.elementForShowUsages}")
            }
            var popup = SoftReference.dereference(popupRef)
            popup?.takeIf { it.isVisible && it is AbstractPopup }?.let {
                val viewComponent = (it as AbstractPopup).component as? ImplementationViewComponent
                // Does the index need to be 0 or 1?
                viewComponent?.update(session.implementationElements, 0)
            }

            val viewComponent =
                object :
                    ReachabilityPanel(
                        session.project,
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
                    .setDimensionServiceKey(
                        editor.project,
                        DocumentationManager.JAVADOC_LOCATION_AND_SIZE,
                        false
                    )
                    .setResizable(true)
                    .setMovable(true)
                    .setRequestFocus(LookupManager.getActiveLookup(session.editor) != null)

            popup = popupBuilder.createPopup()
            PopupPositionManager.positionPopupInBestPosition(
                popup,
                session.editor,
                DataManager.getInstance().getDataContext()
            )
            popupRef = WeakReference(popup)
        }
    }
}
