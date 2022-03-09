package com.github.jyoo980.reachhover.services.tree

import com.github.jyoo980.reachhover.model.Node
import com.github.jyoo980.reachhover.model.Tree
import com.intellij.slicer.SliceNode

object TreeBuilder {

    // A bit of an inelegant way to catch cycles. IntelliJ doesn't do much better, since it
    // Shows recursive calls in a call stack over and over in the window view.
    private const val maxDepth = 10

    fun treeFrom(root: SliceNode): Tree<SliceNode> = treeFrom(root, 0)

    private fun treeFrom(root: SliceNode, callsSoFar: Int): Tree<SliceNode> {
        val treeRoot = Node(root)
        if (callsSoFar < maxDepth) {
            root.children.forEach {
                val childRoot = treeFrom(it, callsSoFar + 1)
                treeRoot.addChild(childRoot)
            }
        }
        return treeRoot
    }
}
