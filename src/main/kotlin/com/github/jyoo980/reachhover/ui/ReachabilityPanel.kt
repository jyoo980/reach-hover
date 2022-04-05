package com.github.jyoo980.reachhover.ui

import com.github.jyoo980.reachhover.model.SliceTreeBuilder
import com.github.jyoo980.reachhover.util.PresentationUtil
import com.intellij.ide.DefaultTreeExpander
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.OnePixelDivider
import com.intellij.openapi.ui.Splitter
import com.intellij.openapi.util.Disposer
import com.intellij.pom.Navigatable
import com.intellij.slicer.SliceLanguageSupportProvider
import com.intellij.slicer.SliceNode
import com.intellij.slicer.SliceUsageCellRendererBase
import com.intellij.ui.*
import com.intellij.ui.treeStructure.Tree
import com.intellij.usageView.UsageInfo
import com.intellij.usages.Usage
import com.intellij.usages.UsageViewPresentation
import com.intellij.usages.UsageViewSettings
import com.intellij.usages.impl.UsagePreviewPanel
import com.intellij.util.EditSourceOnDoubleClickHandler
import com.intellij.util.ui.UIUtil
import com.intellij.util.ui.tree.TreeUtil
import java.awt.BorderLayout
import java.awt.Component
import java.awt.GridLayout
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.*
import javax.swing.event.TreeExpansionEvent
import javax.swing.event.TreeWillExpandListener
import javax.swing.tree.*

abstract class ReachabilityPanel(
    project: Project,
    dataFlowToThis: Boolean,
    rootNode: SliceNode,
    splitByLeafExpressions: Boolean,
) : JPanel(BorderLayout()), DataProvider, Disposable {
    private val myBuilder: SliceTreeBuilder
    private val myTree: JTree
    private val myAutoScrollToSourceHandler: AutoScrollToSourceHandler =
        object : AutoScrollToSourceHandler() {
            override fun isAutoScrollMode(): Boolean {
                return isAutoScroll
            }

            override fun setAutoScrollMode(state: Boolean) {
                isAutoScroll = state
            }
        }
    private var myUsagePreviewPanel: UsagePreviewPanel? = null
    private var myLabel: JPanel? = JPanel(GridLayout(1, 2))
    private val myProject: Project
    private var isDisposed = false
    private val myProvider: SliceLanguageSupportProvider?

    init {
        myProvider = rootNode.provider
        ApplicationManager.getApplication().assertIsDispatchThread()
        myProject = project
        myTree = createTree()
        myBuilder =
            SliceTreeBuilder(myTree, project, dataFlowToThis, rootNode, splitByLeafExpressions)
        myBuilder.setCanYieldUpdate(!ApplicationManager.getApplication().isUnitTestMode)
        Disposer.register(this, myBuilder)
        myBuilder.addSubtreeToUpdate(myTree.model.root as DefaultMutableTreeNode) {
            if (isDisposed || myBuilder.isDisposed || myProject.isDisposed)
                return@addSubtreeToUpdate
            val rootNode1 = myBuilder.getRootSliceNode()
            myBuilder.expand(rootNode1) {
                if (isDisposed || myBuilder.isDisposed || myProject.isDisposed) return@expand
                val children = rootNode1.cachedChildren
                if (children.isNotEmpty()) {
                    myBuilder.select(children[0]) // first there is ony one child
                }
            }
            treeSelectionChanged()
        }
        layoutPanel()
    }

    private fun layoutPanel() {
        myUsagePreviewPanel?.let { Disposer.dispose(it) }
        removeAll()
        val pane = ScrollPaneFactory.createScrollPane(myTree)
        pane.border = IdeBorderFactory.createBorder(SideBorder.LEFT or SideBorder.RIGHT)
        val splitter = Splitter(true, UsageViewSettings.instance.previewUsagesSplitterProportion)
        splitter.firstComponent = pane
        myUsagePreviewPanel = UsagePreviewPanel(myProject, UsageViewPresentation())
        myUsagePreviewPanel?.border = IdeBorderFactory.createBorder(SideBorder.LEFT)
        myUsagePreviewPanel?.let { Disposer.register(this, it) }
        splitter.secondComponent = myUsagePreviewPanel
        splitter.divider.background = OnePixelDivider.BACKGROUND
        add(splitter, BorderLayout.CENTER)
        myLabel?.let { add(it, BorderLayout.SOUTH) }
        myTree.parent.background = UIUtil.getTreeBackground()
        revalidate()
    }

    override fun dispose() {
        myUsagePreviewPanel?.let {
            UsageViewSettings.instance.previewUsagesSplitterProportion =
                (it.parent as Splitter).proportion
            myUsagePreviewPanel = null
        }
        isDisposed = true
        ToolTipManager.sharedInstance().unregisterComponent(myTree)
    }

    internal class MultiLanguageTreeCellRenderer(
        private val rootRenderer: SliceUsageCellRendererBase
    ) : TreeCellRenderer {
        private val providersToRenderers:
            MutableMap<SliceLanguageSupportProvider, SliceUsageCellRendererBase> =
            HashMap()

        init {
            rootRenderer.isOpaque = false
        }

        private fun getRenderer(value: Any): SliceUsageCellRendererBase {
            if (value !is DefaultMutableTreeNode) return rootRenderer
            val userObject = value.userObject as? SliceNode ?: return rootRenderer
            val provider = userObject.provider ?: return rootRenderer
            var renderer = providersToRenderers[provider]
            if (renderer == null) {
                renderer = provider.renderer
                renderer.isOpaque = false
                providersToRenderers[provider] = renderer
            }
            return renderer
        }

        override fun getTreeCellRendererComponent(
            tree: JTree,
            value: Any,
            selected: Boolean,
            expanded: Boolean,
            leaf: Boolean,
            row: Int,
            hasFocus: Boolean,
        ): Component {
            return getRenderer(value)
                .getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus)
        }
    }

    private fun createTree(): JTree {
        val root = DefaultMutableTreeNode()
        val tree = Tree(DefaultTreeModel(root))
        tree.isOpaque = false
        tree.toggleClickCount = -1
        tree.cellRenderer = MultiLanguageTreeCellRenderer(myProvider!!.renderer)
        tree.isRootVisible = false
        tree.showsRootHandles = true
        tree.selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
        tree.selectionPath = TreePath(root.path)
        EditSourceOnDoubleClickHandler.install(tree)
        TreeSpeedSearch(tree)
        TreeUtil.installActions(tree)
        ToolTipManager.sharedInstance().registerComponent(tree)
        myAutoScrollToSourceHandler.install(tree)
        tree.selectionModel.addTreeSelectionListener { treeSelectionChanged() }
        tree.addKeyListener(
            object : KeyAdapter() {
                override fun keyPressed(e: KeyEvent) {
                    if (KeyEvent.VK_ENTER == e.keyCode) {
                        val navigatables = navigatables
                        if (navigatables.isEmpty()) return
                        for (navigatable in navigatables) {
                            if (navigatable.canNavigateToSource()) {
                                ((navigatable as? AbstractTreeNode<*>)?.value as? Usage)
                                    ?.highlightInEditor()
                                navigatable.navigate(false)
                            }
                        }
                        e.consume()
                    }
                }
            }
        )
        tree.addTreeWillExpandListener(
            object : TreeWillExpandListener {
                override fun treeWillCollapse(event: TreeExpansionEvent) {}
                override fun treeWillExpand(event: TreeExpansionEvent) {
                    val path = event.path
                    val node = fromPath(path)
                    node?.calculateDupNode()
                }
            }
        )
        return tree
    }

    private fun treeSelectionChanged() {
        SwingUtilities.invokeLater {
            isDisposed.takeUnless { it }?.let {
                val infos = selectedUsageInfos ?: return@let
                myUsagePreviewPanel?.updateLayout(infos)
                val selectedNode = myTree.selectionPath?.let { fromPath(it) }
                selectedNode?.let {
                    val (fileName, filePath) = PresentationUtil.constructFileComponent(it)
                    myLabel?.removeAll()
                    myLabel?.add(fileName, 0)
                    myLabel?.add(filePath, 1)
                }
            }
        }
    }

    private val selectedUsageInfos: List<UsageInfo>?
        get() =
            myTree.selectionPaths?.let { it ->
                it.mapNotNull { path ->
                    val sliceNode = fromPath(path)
                    sliceNode?.value?.usageInfo
                }
            }

    override fun getData(dataId: String): Any? {
        if (CommonDataKeys.NAVIGATABLE_ARRAY.`is`(dataId)) {
            val navigatables = navigatables
            return if (navigatables.isEmpty()) null else navigatables.toTypedArray()
        }
        return if (PlatformDataKeys.TREE_EXPANDER.`is`(dataId)) {
            DefaultTreeExpander(myTree)
        } else null
    }

    private val navigatables: List<Navigatable>
        get() {
            val paths = myTree.selectionPaths ?: return emptyList()
            val navigatables = ArrayList<Navigatable>()
            for (path in paths) {
                val lastPathComponent = path.lastPathComponent
                if (lastPathComponent is DefaultMutableTreeNode) {
                    val node = lastPathComponent
                    val userObject = node.userObject
                    if (userObject is Navigatable) {
                        navigatables.add(userObject)
                    } else if (node is Navigatable) {
                        navigatables.add(node as Navigatable)
                    }
                }
            }
            return navigatables
        }

    abstract var isAutoScroll: Boolean

    companion object {
        private fun fromPath(path: TreePath): SliceNode? {
            val lastPathComponent = path.lastPathComponent
            return (lastPathComponent as? DefaultMutableTreeNode)?.let {
                val userObj = it.userObject
                return userObj as? SliceNode
            }
        }
    }
}
