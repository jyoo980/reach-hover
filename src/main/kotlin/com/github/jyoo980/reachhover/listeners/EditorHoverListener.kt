package com.github.jyoo980.reachhover.listeners

import com.github.jyoo980.reachhover.MyBundle
import com.github.jyoo980.reachhover.model.ReachabilityHoverContext
import com.github.jyoo980.reachhover.services.NotificationService
import com.github.jyoo980.reachhover.services.ReachabilityInfoPopupManager
import com.github.jyoo980.reachhover.services.slicer.SliceDispatchService
import com.github.jyoo980.reachhover.util.MouseHoverUtil
import com.github.jyoo980.reachhover.util.isLocalVariableReference
import com.github.jyoo980.reachhover.util.isNonLiteralMethodArg
import com.github.jyoo980.reachhover.util.presentableName
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseMotionListener
import com.intellij.openapi.editor.ex.EditorSettingsExternalizable
import com.intellij.openapi.project.DumbService
import com.intellij.ui.awt.RelativePoint
import com.intellij.util.Alarm
import kotlin.math.max
import org.jetbrains.uast.toUElement

internal class EditorHoverListener : EditorMouseMotionListener {

    private val reachabilityPopupManager: ReachabilityInfoPopupManager =
        ReachabilityInfoPopupManager()
    private val alarm: Alarm = Alarm()

    override fun mouseMoved(e: EditorMouseEvent) {
        val dumbService = e.editor.project?.let { DumbService.getInstance(it) }
        val isDumbModeActive = dumbService?.isDumb ?: true
        if (isDumbModeActive) {
            e.editor.project?.let {
                if (!NotificationService.isNotReadyWarningActive(it)) {
                    NotificationService.showNotification(it, MyBundle.message("dumbModeMessage"))
                }
            }
        } else {
            val actionStartTimeMs = System.currentTimeMillis()
            alarm.addRequest({ handleMouseEvent(e) }, actionDelayTime(actionStartTimeMs))
        }
    }

    private fun handleMouseEvent(e: EditorMouseEvent) {
        MouseHoverUtil.targetOffset(e)?.let { offset ->
            val project = e.editor.project ?: return
            val elementToAnalyze = MouseHoverUtil.elementAtOffset(project, e.editor, offset)
            val unifiedAstElement = elementToAnalyze?.toUElement()
            val isForwardAnalysis = unifiedAstElement?.isLocalVariableReference() ?: false
            val isBackwardAnalysis = unifiedAstElement?.isNonLiteralMethodArg() ?: false
            elementToAnalyze
                ?.takeIf { SliceDispatchService.isAnalyzerAvailable(it) }
                ?.takeIf { isForwardAnalysis || isBackwardAnalysis }
                ?.let {
                    val context =
                        ReachabilityHoverContext(
                            elementToInspect = it,
                            location = RelativePoint(e.mouseEvent),
                            editor = e.editor,
                            isForwardAnalysis = isForwardAnalysis,
                            elementName = unifiedAstElement?.presentableName() ?: "",
                            offsetInEditor = offset
                        )
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
