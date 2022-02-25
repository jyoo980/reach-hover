package com.github.jyoo980.reachhover.listeners

import com.github.jyoo980.reachhover.util.MouseHoverUtil
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseMotionListener

internal class EditorHoverListener : EditorMouseMotionListener {

    private val logger: Logger = Logger.getInstance(EditorHoverListener::class.java)

    override fun mouseMoved(e: EditorMouseEvent) {
        val offset = MouseHoverUtil.targetOffset(e) ?: return
        val project = e.editor.project ?: return
        val optElement = MouseHoverUtil.elementAtOffset(project, e.editor, offset)
        // TODO: 2022-02-17 Add check whether optElement is the item we're looking for
        // (variable/method arg).
    }
}
