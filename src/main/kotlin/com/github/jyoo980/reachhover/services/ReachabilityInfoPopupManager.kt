package com.github.jyoo980.reachhover.services

import com.github.jyoo980.reachhover.model.ReachabilityHoverContext
import com.github.jyoo980.reachhover.ui.ReachabilityPopupBuilder
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import java.lang.ref.WeakReference

class ReachabilityInfoPopupManager {

    private val logger: Logger = Logger.getInstance(ReachabilityInfoPopupManager::class.java)
    private val reachabilityPopupBuilder: ReachabilityPopupBuilder = ReachabilityPopupBuilder()
    private var currentContext: ReachabilityHoverContext? = null
    private var currentPopupRef: WeakReference<JBPopup>? = null

    fun showReachabilityPopupFor(latestContext: ReachabilityHoverContext) {
        currentContext =
            latestContext.takeUnless {
                currentContext?.elementToInspect?.isEquivalentTo(it.elementToInspect) ?: false
            }
                ?: return
        val context = currentContext!!
        val popupUI = this.reachabilityPopupBuilder.constructPopupFor(context.elementToInspect)
        val popup =
            JBPopupFactory.getInstance()
                .createComponentPopupBuilder(popupUI, null)
                .setCancelOnClickOutside(true)
                .createPopup()
        currentPopupRef = WeakReference(popup)
        popup.show(context.location)
    }
}
