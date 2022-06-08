package com.github.jyoo980.reachhover.analytics

import com.github.jyoo980.reachhover.analytics.AnalyticsValues.builtInBackwardAnalysisText
import com.github.jyoo980.reachhover.analytics.AnalyticsValues.builtInForwardAnalysisText
import com.github.jyoo980.reachhover.analytics.AnalyticsValues.reachHoverBackwardPrefix
import com.github.jyoo980.reachhover.analytics.AnalyticsValues.reachHoverForwardPrefix
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ex.AnActionListener
import com.intellij.openapi.actionSystem.impl.ActionMenuItem
import com.intellij.ui.treeStructure.Tree
import java.awt.AWTEvent
import java.awt.Toolkit
import java.awt.event.AWTEventListener
import java.awt.event.MouseEvent
import javax.swing.JButton

class ReachHoverAnalyticsService : AWTEventListener, AnActionListener, Disposable {

    private var numTimesDataflowInvoked = 0
    private var numTimesReachHoverInvoked = 0
    private var prevActionChain: MutableList<AWTEvent> = mutableListOf()

    init {
        val eventMask =
            AWTEvent.MOUSE_EVENT_MASK or
                AWTEvent.WINDOW_EVENT_MASK or
                AWTEvent.WINDOW_STATE_EVENT_MASK
        Toolkit.getDefaultToolkit().addAWTEventListener(this, eventMask)
    }

    override fun eventDispatched(event: AWTEvent?) {
        event?.let {
            checkForDataflowAnalysisInvocation(it)
            checkForReachHoverInvocation(it)
            checkForMousePress(it)
        }
    }

    override fun dispose() {
        Toolkit.getDefaultToolkit().removeAWTEventListener(this)
    }

    private fun checkForDataflowAnalysisInvocation(e: AWTEvent) {
        val mouseEvent = e as? MouseEvent
        val isMousePressed = mouseEvent?.let { it.id == MouseEvent.MOUSE_PRESSED } ?: false
        if ((isDataflowAnalysisEvent(e) || isOkButtonClicked(e)) && isMousePressed) {
            if (isDataflowAnalysisEvent(e)) {
                prevActionChain.add(e)
            } else if (isOkButtonClicked(e)) {
                if (prevActionChain.any(::isDataflowAnalysisEvent)) {
                    numTimesDataflowInvoked++
                    LogWriter.write(
                        "Dataflow Analysis invoked: $numTimesDataflowInvoked time(s)",
                        EventType.STANDARD_DATAFLOW_INVOKED
                    )
                    prevActionChain.clear()
                }
            }
        }
    }

    private fun isDataflowAnalysisEvent(event: AWTEvent): Boolean {
        return (event.source as? ActionMenuItem)?.let {
            it.text == builtInBackwardAnalysisText || it.text == builtInForwardAnalysisText
        }
            ?: false
    }

    private fun checkForReachHoverInvocation(e: AWTEvent) {
        val mouseEvent = e as? MouseEvent
        val isMousePressed = mouseEvent?.let { it.id == MouseEvent.MOUSE_PRESSED } ?: false
        if (isReachHoverEvent(e) && isMousePressed) {
            numTimesReachHoverInvoked++
            LogWriter.write(
                "ReachHover invoked: $numTimesReachHoverInvoked time(s)",
                EventType.REACH_HOVER_INVOKED
            )
        }
    }

    private fun checkForMousePress(e: AWTEvent) {
        val mouseEvent = e as? MouseEvent
        val isMousePressed = mouseEvent?.let { it.id == MouseEvent.MOUSE_PRESSED } ?: false
        if (isMousePressed) {
            mouseEvent?.source?.let {
                val tree = it as? Tree
                val treeNodeOrEventSource = tree?.selectionPath?.lastPathComponent ?: it
                LogWriter.write(
                    "Mouse clicked at source: $treeNodeOrEventSource",
                    EventType.MOUSE_CLICK
                )
            }
        }
    }

    private fun isReachHoverEvent(event: AWTEvent): Boolean {
        val eventText = (event.source as? JButton)?.text
        return eventText?.contains(reachHoverBackwardPrefix)
            ?: false || eventText?.contains(reachHoverForwardPrefix) ?: false
    }

    private fun isOkButtonClicked(event: AWTEvent): Boolean {
        return (event.source as? JButton)?.let { it.text == "Analyze" || it.text == "OK" } ?: false
    }
}
