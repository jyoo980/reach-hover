package com.github.jyoo980.reachhover.services

import com.github.jyoo980.reachhover.analytics.EventType
import com.github.jyoo980.reachhover.analytics.LogWriter
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
                .setCancelCallback {
                    LogWriter.write("ReachHover popup closed", EventType.POPUP_EVENT)
                    true
                }
                .createPopup()
        currentPopupRef = WeakReference(popup)

        ApplicationManager.getApplication().invokeLater {
            currentContext?.editor?.let { EditorMouseHoverPopupControl.disablePopups(it) }
        }
        LogWriter.write("ReachHover popup shown", EventType.POPUP_EVENT)
        popup.show(popupContext.location)
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
            currentContext?.editor?.let { EditorMouseHoverPopupControl.enablePopups(it) }
        }
    }
}
