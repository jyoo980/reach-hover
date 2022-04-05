package com.github.jyoo980.reachhover.util

import com.intellij.ide.scratch.ScratchUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.slicer.SliceNode
import com.intellij.util.ui.JBUI
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.SwingConstants

object PresentationUtil {

    fun constructFileComponent(node: SliceNode): Pair<JComponent, JComponent> {
        val containingFile = node.element?.value?.file
        val rawFileName = node.value?.element?.containingFile?.name
        val fullPath =
            node.project?.let {
                getPresentablePath(it, containingFile, maxChars = 120)
                    ?.removeSuffix("/$rawFileName")
            }
        val fileNameComponent =
            JLabel((rawFileName ?: "File not found"), SwingConstants.LEFT).also {
                // Note: VirtualFile#fileType may be slow, revisit this if there's a perf.
                // bottleneck.
                it.icon = containingFile?.fileType?.icon
                it.border = JBUI.Borders.empty(10, 5, 5, 0)
            }
        val filePathComponent =
            JLabel((fullPath ?: "Path not found"), SwingConstants.RIGHT).also {
                it.border = JBUI.Borders.empty(10, 0, 5, 5)
            }
        return (fileNameComponent to filePathComponent)
    }

    private fun getPresentablePath(
        project: Project,
        virtualFile: VirtualFile?,
        maxChars: Int
    ): String? {
        return virtualFile?.let {
            val path =
                if (ScratchUtil.isScratch(it)) ScratchUtil.getRelativePath(project, it)
                else if (VfsUtilCore.isAncestor(project.baseDir, it, true))
                    VfsUtilCore.getRelativeLocation(it, project.baseDir)
                else FileUtil.getLocationRelativeToUserHome(it.path)
            return path?.let { p ->
                maxChars.takeIf { chars -> chars < 0 }?.let { path }
                    ?: StringUtil.trimMiddle(p, maxChars)
            }
        }
    }
}
