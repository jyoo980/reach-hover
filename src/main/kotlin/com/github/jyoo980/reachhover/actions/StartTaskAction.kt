package com.github.jyoo980.reachhover.actions

import com.github.jyoo980.reachhover.services.NotificationService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import icons.IconManager

class StartTaskAction : AnAction(IconManager.startTaskIcon) {

    private val logger: Logger = Logger.getInstance(StartTaskAction::class.java)

    override fun actionPerformed(e: AnActionEvent) {
        logger.info("==== ReachHover: Task Started ====")
        e.project?.let { NotificationService.showNotification(it, "Task Started") }
    }
}
