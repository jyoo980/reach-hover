package com.github.jyoo980.reachhover.services

import com.github.jyoo980.reachhover.MyBundle
import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.notification.NotificationsManager
import com.intellij.openapi.project.Project
import icons.IconManager

object NotificationService {

    private val groupId = MyBundle.message("reachhover.notification.group")
    private val notificationGroupManager =
        NotificationGroupManager.getInstance().getNotificationGroup(groupId)

    fun showNotification(project: Project, message: String) {
        val notification =
            notificationGroupManager
                .createNotification(message, NotificationType.INFORMATION)
                .setTitle("ReachHover")
                .setIcon(IconManager.reachHoverIcon)
        notification.notify(project)
    }

    fun isNotReadyWarningActive(project: Project): Boolean {
        val notifications =
            NotificationsManager.getNotificationsManager()
                .getNotificationsOfType(Notification::class.java, project)
        return notifications.any { it.content == MyBundle.message("dumbModeMessage") }
    }
}
