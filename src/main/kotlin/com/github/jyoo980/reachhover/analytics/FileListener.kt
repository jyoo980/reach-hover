package com.github.jyoo980.reachhover.analytics

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile

class FileListener : FileEditorManagerListener {

    private val logger: Logger = Logger.getInstance(FileListener::class.java)

    // Should be okay to collect which files are open, given we provide the project in the
    // experiment.
    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        logger.info("==== File Listener: Opened '${file.name}' ====")
    }

    override fun fileClosed(source: FileEditorManager, file: VirtualFile) {
        logger.info("==== File Listener: Closed '${file.name}' ====")
    }
}
