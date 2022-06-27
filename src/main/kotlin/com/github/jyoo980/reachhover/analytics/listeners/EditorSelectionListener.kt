package com.github.jyoo980.reachhover.analytics.listeners

import com.github.jyoo980.reachhover.analytics.EventType
import com.github.jyoo980.reachhover.analytics.LogWriter
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener

class EditorSelectionListener : CaretListener {

    override fun caretPositionChanged(event: CaretEvent) {
        val editorType = event.editor.editorKind
        LogWriter.write("Jump occurred in editor of type: $editorType", EventType.CURSOR_JUMP)
    }
}
