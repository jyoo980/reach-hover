package com.github.jyoo980.reachhover.services

import com.github.jyoo980.reachhover.model.Tree
import com.intellij.slicer.SliceNode

interface TreeBuilder {

    fun treeFrom(root: SliceNode): Tree<SliceNode> {
        val treeRoot = Tree(root)
        root.children.forEach {
            val childRoot = treeFrom(it)
            treeRoot.addChild(childRoot)
        }
        return treeRoot
    }

    fun printGraph(root: Tree<SliceNode>, level: Int = 0) {
        val indent = (1..level).fold("") { acc, _ -> "$acc " }
        println(indent + root.value)
        root.children.forEach { printGraph(it, level + 1) }
    }
}
