package com.github.jyoo980.reachhover.actions

import com.github.jyoo980.reachhover.analytics.listeners.EditorSelectionListener
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class StartupAction : StartupActivity {

    override fun runActivity(project: Project) {
        EditorFactory.getInstance().eventMulticaster.addCaretListener(EditorSelectionListener())
    }
}
