package com.github.jyoo980.reachhover.services.tree

import com.github.jyoo980.reachhover.model.Empty
import com.github.jyoo980.reachhover.model.Node
import com.github.jyoo980.reachhover.model.Tree
import com.intellij.slicer.SliceNode

object TreeBuilder {

    fun treeFrom(root: SliceNode): Tree<SliceNode> = treeFrom(root, mutableSetOf())

    private fun treeFrom(root: SliceNode, visited: MutableSet<SliceNode>): Tree<SliceNode> {
        if (isVisitedBefore(root, visited)) {
            return Empty
        }
        visited += root
        return Node(value = root, children = root.children.map { treeFrom(it, visited) })
    }

    private fun isVisitedBefore(node: SliceNode, visited: MutableSet<SliceNode>): Boolean {
        val elementToSearchFor = node.value.element ?: return false
        return visited.any { it.value.element?.isEquivalentTo(elementToSearchFor) ?: false }
    }
}
