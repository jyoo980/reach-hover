package com.github.jyoo980.reachhover.services

import com.github.jyoo980.reachhover.model.ReachabilityHoverContext
import com.github.jyoo980.reachhover.ui.ReachabilityPopupBuilder
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.impl.EditorMouseHoverPopupControl
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import java.lang.ref.WeakReference

class ReachabilityInfoPopupManager {

    private val reachabilityPopupBuilder: ReachabilityPopupBuilder = ReachabilityPopupBuilder()
    private var currentContext: ReachabilityHoverContext? = null
    private var currentPopupRef: WeakReference<JBPopup>? = null

    fun showReachabilityPopupFor(latestContext: ReachabilityHoverContext) {
        currentContext =
            latestContext.takeUnless {
                currentContext?.elementToInspect?.isEquivalentTo(it.elementToInspect) ?: false
            }
                ?: return
        closePopup()
        val popupContext = currentContext!!
        val popupUI = reachabilityPopupBuilder.constructPopupFor(popupContext)
        val popup =
            JBPopupFactory.getInstance()
                .createComponentPopupBuilder(popupUI, null)
                .setCancelOnClickOutside(true)
                .createPopup()
        currentPopupRef = WeakReference(popup)

        ApplicationManager.getApplication().invokeLater {
            currentContext?.editor?.let {
                EditorMouseHoverPopupControl.disablePopups(it)
            }
        }
        popup.show(popupContext.location)

//        // WIP: Trying to show the popup at the bottom of QuickDoc hint
//        val editor = currentContext?.editor ?: return
//        val project = editor.project ?: return
//        val manager = DocumentationManager.getInstance(project)
//
//        if (EditorMouseHoverPopupControl.arePopupsDisabled(editor)) {
//            popup.show(popupContext.location)
//        } else {
//            val element = currentContext?.elementToInspect ?: return
//            val component = QuickDocUtil.getActiveDocComponent(project)
//
//            val i = 10
//            println(i)
//
//            val docHint: JBPopup = manager.docInfoHint ?: return
//            val loc = docHint.locationOnScreen
//            popup.showInScreenCoordinates(docHint.owner, Point(loc.x, loc.y + docHint.size.height))
//        }
    }

    fun resetPopupState() {
        clearPopupContext()
        closePopup()
    }

    private fun clearPopupContext() {
        currentContext = null
    }

    private fun closePopup() {
        currentPopupRef?.get()?.also { it.cancel() }
        currentPopupRef?.clear()
        ApplicationManager.getApplication().invokeLater {
            currentContext?.editor?.let {
                EditorMouseHoverPopupControl.enablePopups(it)
            }
        }
    }
}
