package com.github.jyoo980.reachhover.listeners

import com.github.jyoo980.reachhover.model.ReachabilityHoverContext
import com.github.jyoo980.reachhover.services.ReachabilityInfoPopupManager
import com.github.jyoo980.reachhover.util.MouseHoverUtil
import com.github.jyoo980.reachhover.util.isLocalVariableReference
import com.github.jyoo980.reachhover.util.isNonLiteralMethodArg
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseMotionListener
import com.intellij.openapi.editor.ex.EditorSettingsExternalizable
import com.intellij.ui.awt.RelativePoint
import com.intellij.util.Alarm
import kotlin.math.max

internal class EditorHoverListener : EditorMouseMotionListener {

    private val logger: Logger = Logger.getInstance(EditorHoverListener::class.java)
    private val reachabilityPopupManager: ReachabilityInfoPopupManager =
        ReachabilityInfoPopupManager()
    private val alarm: Alarm = Alarm()

    override fun mouseMoved(e: EditorMouseEvent) {
        val actionStartTimeMs = System.currentTimeMillis()
        alarm.addRequest({ handleMouseEvent(e) }, actionDelayTime(actionStartTimeMs))
    }

    private fun handleMouseEvent(e: EditorMouseEvent) {
        MouseHoverUtil.targetOffset(e)?.let { offset ->
            val project = e.editor.project ?: return
            val elementToAnalyze = MouseHoverUtil.elementAtOffset(project, e.editor, offset)
            elementToAnalyze
                ?.takeIf { it.isNonLiteralMethodArg() || it.isLocalVariableReference() }
                ?.let {
                    val context =
                        ReachabilityHoverContext(it, RelativePoint(e.mouseEvent), e.editor)
                    reachabilityPopupManager.showReachabilityPopupFor(context)
                }
        }
            ?: reachabilityPopupManager.resetPopupState()
    }

    private fun actionDelayTime(startTimeMs: Long): Long =
        max(
            0,
            EditorSettingsExternalizable.getInstance().tooltipsDelay -
                (System.currentTimeMillis() - startTimeMs)
        )
}
