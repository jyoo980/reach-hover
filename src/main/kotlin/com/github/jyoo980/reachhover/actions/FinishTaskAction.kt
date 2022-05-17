package com.github.jyoo980.reachhover.actions

import com.github.jyoo980.reachhover.analytics.EventType
import com.github.jyoo980.reachhover.analytics.LogWriter
import com.github.jyoo980.reachhover.services.NotificationService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import icons.IconManager

class FinishTaskAction : AnAction(IconManager.completeTaskIcon) {

    override fun actionPerformed(e: AnActionEvent) {
        LogWriter.write("Task Finished", EventType.TASK_FINISH)
        e.project?.let { NotificationService.showNotification(it, "Task Finished") }
    }
}
