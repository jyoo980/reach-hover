package com.github.jyoo980.reachhover.services

import com.github.jyoo980.reachhover.model.ReachabilityHoverContext
import com.github.jyoo980.reachhover.ui.ReachabilityPopupBuilder
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ui.popup.JBPopupFactory

class ReachabilityInfoPopupManager {

    private val logger: Logger = Logger.getInstance(ReachabilityInfoPopupManager::class.java)
    private val reachabilityPopupBuilder: ReachabilityPopupBuilder = ReachabilityPopupBuilder()
    private var optCurrentContext: ReachabilityHoverContext? = null

    fun showReachabilityPopupFor(latestContext: ReachabilityHoverContext) {
        // TODO: probably a much more idiomatic way to do this by using takeIf/takeUnless.
        if (this.optCurrentContext != null) {
            if (this.optCurrentContext == latestContext) {
                return
            }
        }
        this.optCurrentContext = latestContext
        this.optCurrentContext?.takeIf { it.isValidElementForAnalysis() }?.also {
            val popupUI = this.reachabilityPopupBuilder.constructPopupFor(it.elementToInspect)
            val popup =
                JBPopupFactory.getInstance()
                    .createComponentPopupBuilder(popupUI, null)
                    .setCancelOnClickOutside(true)
                    .createPopup()
            popup.show(it.location)
        }
    }
}
