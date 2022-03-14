package com.github.jyoo980.reachhover.codeinsight

import com.intellij.codeInsight.hint.ImplementationViewElement
import com.intellij.codeInsight.hint.ImplementationViewSession
import com.intellij.codeInsight.hint.ImplementationViewSessionFactory
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.Processor

class ReachabilityElementViewSession : ImplementationViewSession {

    override val editor: Editor?
        get() = TODO("Not yet implemented")
    override val factory: ImplementationViewSessionFactory
        get() = TODO("Not yet implemented")
    override val file: VirtualFile?
        get() = TODO("Not yet implemented")
    override val implementationElements: List<ImplementationViewElement>
        get() = TODO("Not yet implemented")
    override val project: Project
        get() = TODO("Not yet implemented")
    override val text: String?
        get() = TODO("Not yet implemented")

    override fun dispose() {
        TODO("Not yet implemented")
    }

    override fun elementRequiresIncludeSelf(): Boolean {
        TODO("Not yet implemented")
    }

    override fun needUpdateInBackground(): Boolean {
        TODO("Not yet implemented")
    }

    override fun searchImplementationsInBackground(
        indicator: ProgressIndicator,
        processor: Processor<in ImplementationViewElement>
    ): List<ImplementationViewElement> {
        TODO("Not yet implemented")
    }
}
