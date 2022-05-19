package com.github.jyoo980.reachhover.actions

import com.github.jyoo980.reachhover.analytics.EventType
import com.github.jyoo980.reachhover.analytics.LogWriter
import com.github.jyoo980.reachhover.services.NotificationService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import icons.IconManager

class StartTaskAction : AnAction(IconManager.startTaskIcon) {

    override fun actionPerformed(e: AnActionEvent) {
        LogWriter.write("Task Started", EventType.TASK_START)
        e.project?.let { NotificationService.showNotification(it, "Task Started") }
    }
}
