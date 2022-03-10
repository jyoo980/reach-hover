package com.github.jyoo980.reachhover.services.tree

import com.github.jyoo980.reachhover.model.SliceMetadata
import com.github.jyoo980.reachhover.model.Tree
import com.github.jyoo980.reachhover.model.map
import com.intellij.psi.PsiElement
import com.intellij.slicer.SliceNode

class SliceMetadataTransformer : TreeTransformer<SliceNode, SliceMetadata> {

    override fun transform(tree: Tree<SliceNode>): Tree<SliceMetadata> {
        return tree.map { node ->
            val elementForSlice = node.value.element
            SliceMetadata(
                element = elementForSlice,
                file = elementForSlice?.containingFile,
                lineInFile = elementForSlice?.let { lineNumberOf(it) }
            )
        }
    }

    private fun lineNumberOf(element: PsiElement): Int {
        val viewProvider = element.containingFile.viewProvider
        return viewProvider.document?.let {
            val offset = element.textOffset
            // Add 1 here, since `getLineNumber` assumes 0-indexing.
            it.getLineNumber(offset) + 1
        }
            ?: -1
    }
}
