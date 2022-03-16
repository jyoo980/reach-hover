package com.github.jyoo980.reachhover.codeinsight

import com.intellij.codeInsight.hint.ImplementationViewSession
import com.intellij.codeInsight.hint.ImplementationViewSessionFactory
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

// TODO: unsure if is needed yet, come back later and check.
class ReachabilityElementViewSessionFactory: ImplementationViewSessionFactory {
    override fun createSession(
        dataContext: DataContext,
        project: Project,
        isSearchDeep: Boolean,
        alwaysIncludeSelf: Boolean
    ): ImplementationViewSession? {
        TODO("Not yet implemented")
    }

    override fun createSessionForLookupElement(
        project: Project,
        editor: Editor?,
        file: VirtualFile?,
        lookupItemObject: Any?,
        isSearchDeep: Boolean,
        alwaysIncludeSelf: Boolean
    ): ImplementationViewSession? {
        TODO("Not yet implemented")
    }
}