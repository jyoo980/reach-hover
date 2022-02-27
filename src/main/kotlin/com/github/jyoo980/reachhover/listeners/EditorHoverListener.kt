package com.github.jyoo980.reachhover.listeners

import com.github.jyoo980.reachhover.model.ReachabilityHoverContext
import com.github.jyoo980.reachhover.services.ReachabilityInfoPopupManager
import com.github.jyoo980.reachhover.util.MouseHoverUtil
import com.github.jyoo980.reachhover.util.isLocalVariableReference
import com.github.jyoo980.reachhover.util.isNonLiteralMethodArg
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseMotionListener
import com.intellij.ui.awt.RelativePoint

internal class EditorHoverListener : EditorMouseMotionListener {

    private val logger: Logger = Logger.getInstance(EditorHoverListener::class.java)
    private val reachabilityPopupManager: ReachabilityInfoPopupManager =
        ReachabilityInfoPopupManager()

    override fun mouseMoved(e: EditorMouseEvent) {
        val offset = MouseHoverUtil.targetOffset(e) ?: return
        val project = e.editor.project ?: return
        val elementUnderInspection = MouseHoverUtil.elementAtOffset(project, e.editor, offset)
        elementUnderInspection
            ?.takeIf { it.isNonLiteralMethodArg() || it.isLocalVariableReference() }
            ?.also {
                val context = ReachabilityHoverContext(it, RelativePoint(e.mouseEvent), e.editor)
                reachabilityPopupManager.showReachabilityPopupFor(context)
            }
    }
}
