package com.github.jyoo980.reachhover.services.tree

import com.github.jyoo980.reachhover.model.Node
import com.github.jyoo980.reachhover.model.Tree
import com.intellij.slicer.SliceNode

interface TreeBuilder {

    fun treeFrom(root: SliceNode): Tree<SliceNode> {
        val treeRoot = Node(root)
        root.children.forEach {
            val childRoot = treeFrom(it)
            treeRoot.addChild(childRoot)
        }
        return treeRoot
    }
}
