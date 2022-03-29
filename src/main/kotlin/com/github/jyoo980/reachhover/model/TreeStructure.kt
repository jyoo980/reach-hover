package com.github.jyoo980.reachhover.model

import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.util.treeView.AbstractTreeStructureBase
import com.intellij.openapi.project.Project
import com.intellij.slicer.SliceNode

class TreeStructure(project: Project, private val rootSliceNode: SliceNode) :
    AbstractTreeStructureBase(project) {

    override fun getProviders(): MutableList<TreeStructureProvider>? = mutableListOf()

    override fun getRootElement(): Any = rootSliceNode

    override fun commit() {}

    override fun hasSomethingToCommit(): Boolean = false

    override fun isToBuildChildrenInBackground(element: Any): Boolean = true
}
