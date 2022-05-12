package com.github.jyoo980.reachhover.analytics

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile

class FileListener : FileEditorManagerListener {

    // Should be okay to collect which files are open, given we provide the project in the
    // experiment, but I'm going to obfuscate the name of the file by just hashing its string rep.
    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        val hashedFileName = file.name.hashCode()
        LogWriter.write("Opened $hashedFileName", EventType.FILE_OPEN)
    }

    override fun fileClosed(source: FileEditorManager, file: VirtualFile) {
        val hashedFileName = file.name.hashCode()
        LogWriter.write("Closed '$hashedFileName'", EventType.FILE_CLOSED)
    }
}
