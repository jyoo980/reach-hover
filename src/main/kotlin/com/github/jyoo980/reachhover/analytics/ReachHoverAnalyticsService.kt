package com.github.jyoo980.reachhover.analytics

import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ex.AnActionListener
import com.intellij.openapi.actionSystem.impl.ActionMenuItem
import com.intellij.openapi.diagnostic.Logger
import java.awt.AWTEvent
import java.awt.Toolkit
import java.awt.event.AWTEventListener
import java.awt.event.MouseEvent

class ReachHoverAnalyticsService : AWTEventListener, AnActionListener, Disposable {

    private val logger: Logger = Logger.getInstance(ReachHoverAnalyticsService::class.java)
    private val builtInBackwardAnalysisText = "Data Flow to Here..."
    private val builtInForwardAnalysisText = "Data Flow from Here..."
    private var numTimesDataflowInvoked = 0

    init {
        val eventMask =
            AWTEvent.MOUSE_EVENT_MASK or
                AWTEvent.WINDOW_EVENT_MASK or
                AWTEvent.WINDOW_STATE_EVENT_MASK
        Toolkit.getDefaultToolkit().addAWTEventListener(this, eventMask)
    }

    override fun eventDispatched(event: AWTEvent?) {
        event?.let { e ->
            logger.info("EVENT: $e")
            val mouseEvent = e as? MouseEvent
            val isMousePressed = mouseEvent?.let { it.id == MouseEvent.MOUSE_PRESSED } ?: false
            if (isMousePressed && isDataflowAnalysisEvent(e)) {
                numTimesDataflowInvoked++
                // For debugging.
                logger.info("Dataflow Analysis invoked: $numTimesDataflowInvoked time(s)")
            }
        }
    }

    override fun dispose() {
        Toolkit.getDefaultToolkit().removeAWTEventListener(this)
    }

    private fun isDataflowAnalysisEvent(event: AWTEvent): Boolean {
        return (event.source as? ActionMenuItem)?.let {
            it.text == builtInBackwardAnalysisText || it.text == builtInForwardAnalysisText
        }
            ?: false
    }
}
