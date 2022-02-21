package com.github.jyoo980.reachhover.services

import com.github.jyoo980.reachhover.model.ReachabilityHoverContext
import com.github.jyoo980.reachhover.ui.BackwardReachabilityButton
import com.github.jyoo980.reachhover.ui.ReachabilityPopupBuilder
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import java.awt.GridLayout
import java.lang.ref.WeakReference
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.SwingConstants

class ReachabilityInfoPopupManager {

    private val logger: Logger = Logger.getInstance(ReachabilityInfoPopupManager::class.java)
    private val reachabilityPopupBuilder: ReachabilityPopupBuilder = ReachabilityPopupBuilder()
    private var optCurrentContext: ReachabilityHoverContext? = null
    private var currentPopupRef: WeakReference<JBPopup>? = null

    fun showReachabilityPopupFor(latestContext: ReachabilityHoverContext) {
        // TODO: probably a much more idiomatic way to do this by using takeIf/takeUnless.
        if (this.optCurrentContext != null) {
            if (this.optCurrentContext == latestContext) {
                return
            }
        }
        this.optCurrentContext = latestContext
        this.optCurrentContext?.also {
            val popupUI =
                this.reachabilityPopupBuilder.constructPopupFor(it.elementToInspect).getUI()
            val popup =
                JBPopupFactory.getInstance()
                    .createComponentPopupBuilder(popupUI, null)
                    .setCancelOnClickOutside(true)
                    .createPopup()
            this.currentPopupRef = WeakReference(popup)
            popup.show(it.location)
        }
    }

    private fun component(): JComponent {
        val panel = JPanel(GridLayout(2, 1))
        val documentationButton =
            JButton("Show types and documentation").also {
                it.horizontalAlignment = SwingConstants.LEFT
                it.isBorderPainted = false
                it.isContentAreaFilled = false
            }
        this.optCurrentContext?.also {
            val reachabilityButton = BackwardReachabilityButton(it.elementToInspect)
            reachabilityButton.setButtonText()
            panel.add(reachabilityButton.ui)
        }
        panel.add(documentationButton)
        return panel
    }
}
