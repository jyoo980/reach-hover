package com.github.jyoo980.reachhover.services

import com.github.jyoo980.reachhover.MyBundle
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

object NotificationService {

    private val groupId = MyBundle.message("reachhover.notification.group")
    private val notificationManager =
        NotificationGroupManager.getInstance().getNotificationGroup(groupId)

    fun showNotification(project: Project, message: String) {
        val notification =
            notificationManager
                .createNotification(message, NotificationType.INFORMATION)
                .setTitle("ReachHover")
        notification.notify(project)
    }
}
