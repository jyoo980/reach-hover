package com.github.jyoo980.reachhover.util

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseEventArea
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.PsiDocumentManagerImpl

object MouseHoverUtil {

    /**
     * Calculates the offset within the editor of a mouse [event].
     *
     * The offset is returned iff the mouse event is valid, i.e., hovers over unfolded text.
     *
     * @return the mouse event offset.
     */
    fun targetOffset(event: EditorMouseEvent): Int? {
        return if (this.isHoverInValidArea(event)) {
            event.offset
        } else null
    }

    /**
     * Given a [project], [editor], and [offset], attempt to locate the PsiElement at the location
     * in the file.
     *
     * @return the PsiElement if found.
     */
    fun elementAtOffset(project: Project, editor: Editor, offset: Int): PsiElement? {
        val optFile = PsiDocumentManagerImpl.getInstance(project).getPsiFile(editor.document)
        return optFile?.let { it ->
            var element = it.findElementAt(offset)
            if (element == null && offset == it.textLength) {
                element = it.findElementAt(offset - 1)
            }
            if (element is PsiWhiteSpace) {
                element = element.prevSibling
            }
            element
        }
    }

    /**
     * Checks whether [event] is a valid mouse event.
     *
     * In this case, a valid mouse event is when a mouse hovers over text in an editor, and not
     * within a collapsed fold region.
     *
     * @return the result of a validity check.
     */
    private fun isHoverInValidArea(event: EditorMouseEvent): Boolean {
        return event.editor is EditorEx &&
            event.editor.project != null &&
            event.area == EditorMouseEventArea.EDITING_AREA &&
            event.mouseEvent.modifiersEx == 0 &&
            event.isOverText &&
            event.collapsedFoldRegion == null
    }
}
