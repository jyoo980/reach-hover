package com.github.jyoo980.reachhover.analytics

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile

class FileListener : FileEditorManagerListener {

    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        LogWriter.write("Opened ${file.name}", EventType.FILE_OPEN)
    }

    override fun fileClosed(source: FileEditorManager, file: VirtualFile) {
        LogWriter.write("Closed '${file.name}'", EventType.FILE_CLOSED)
    }
}
