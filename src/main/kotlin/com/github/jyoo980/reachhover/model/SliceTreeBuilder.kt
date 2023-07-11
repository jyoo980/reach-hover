package com.github.jyoo980.reachhover.model

import com.intellij.ide.util.treeView.AbstractTreeBuilder
import com.intellij.ide.util.treeView.AlphaComparator
import com.intellij.ide.util.treeView.NodeDescriptor
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.util.ProgressIndicatorBase
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Comparing
import com.intellij.slicer.SliceNode
import javax.swing.JTree
import javax.swing.tree.DefaultTreeModel

class SliceTreeBuilder(
    tree: JTree,
    project: Project,
    dataflowToThis: Boolean,
    rootNode: SliceNode,
    splitByLeafExpr: Boolean
) :
    AbstractTreeBuilder(
        tree,
        tree.model as DefaultTreeModel,
        TreeStructure(project, rootNode),
        sliceNodeComparator,
        false
    ) {

    var mySplitByLeafExpr = false
    var myDataflowToThis = false
    @Volatile var analysisInProgress = false

    init {
        myDataflowToThis = dataflowToThis
        mySplitByLeafExpr = splitByLeafExpr
        initRootNode()
    }

    fun getRootSliceNode(): SliceNode {
        return treeStructure.rootElement as SliceNode
    }

    override fun isAutoExpandNode(nodeDescriptor: NodeDescriptor<*>?): Boolean = false

    override fun createProgressIndicator(): ProgressIndicator? {
        return ProgressIndicatorBase(true)
    }

    companion object {
        val sliceNodeComparator: Comparator<NodeDescriptor<*>> = Comparator { o1, o2 ->
            if (o1 !is SliceNode || o2 !is SliceNode) {
                return@Comparator AlphaComparator.INSTANCE.compare(o1, o2)
            }

            val element1 = (o1 as? SliceNode)?.value?.element
            val element2 = (o2 as? SliceNode)?.value?.element

            val file1 = element1?.containingFile
            val file2 = element2?.containingFile

            if (file1 == null) return@Comparator if (file2 == null) 0 else 1
            if (file2 == null) return@Comparator -1

            if (file1 == file2) {
                return@Comparator element1.textOffset - element2.textOffset
            }
            return@Comparator Comparing.compare(file1.name, file2.name)
        }
    }
}
