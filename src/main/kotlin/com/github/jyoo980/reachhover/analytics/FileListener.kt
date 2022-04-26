package com.github.jyoo980.reachhover.analytics

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile

class FileListener : FileEditorManagerListener {

    private val logger: Logger = Logger.getInstance(FileListener::class.java)

    // Should be okay to collect which files are open, given we provide the project in the
    // experiment, but I'm going to obfuscate the name of the file by just hashing its string rep.
    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        val hashedFileName = file.name.hashCode()
        logger.info("==== File Listener: Opened '$hashedFileName' ====")
    }

    override fun fileClosed(source: FileEditorManager, file: VirtualFile) {
        val hashedFileName = file.name.hashCode()
        logger.info("==== File Listener: Closed '$hashedFileName' ====")
    }
}
