package com.github.jyoo980.reachhover.analytics

import com.intellij.openapi.application.PathManager
import com.intellij.openapi.diagnostic.Logger
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.time.Instant

object LogWriter : TimeUtil {

    private val logger: Logger = Logger.getInstance(LogWriter::class.java)
    private val logFilePath = "${PathManager.getLogPath()}/reach-hover/reach-hover.log"

    init {
        val file = File(logFilePath)
        if (file.exists()) {
            file.delete()
        }
        if (file.parentFile.mkdir()) {
            try {
                file.createNewFile()
            } catch (e: Exception) {
                logger.error("Failed to create logging file for reach-hover", e)
            }
        }
    }

    fun write(message: String, type: EventType) {
        try {
            val writer = BufferedWriter(FileWriter(logFilePath, true))
            val formattedMessage = format(message, humanReadableDate(Instant.now()), type)
            writer.write(formattedMessage)
            writer.close()
        } catch (e: Exception) {
            logger.error("Failed to write message to reach-hover log: $message", e)
        }
    }

    private fun format(message: String, time: String, type: EventType): String {
        return "$time - [$type] - $message\n"
    }
}

enum class EventType {
    TASK_START,
    TASK_FINISH,
    STANDARD_DATAFLOW_INVOKED,
    REACH_HOVER_INVOKED,
    FILE_OPEN,
    FILE_CLOSED,
    MOUSE_CLICK,
    CURSOR_JUMP,
}
