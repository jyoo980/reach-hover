package com.github.jyoo980.reachhover.ui.scope

import com.github.jyoo980.reachhover.services.slicer.SliceDispatchService
import com.github.jyoo980.reachhover.ui.ReachabilityPanel
import com.intellij.psi.PsiElement
import com.intellij.slicer.SliceAnalysisParams
import com.intellij.slicer.SliceRootNode
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JButton
import javax.swing.JToolBar

class ReachabilityScopePanel(val reachabilityPanel: ReachabilityPanel) :
    JToolBar(), ScopePanelStyler {

    private val supportedScopes: List<Scope> = listOf(File, Directory, Project)
    private val scopeSelectionButtons: List<ScopeButton> = supportedScopes.map(::scopeButton)

    init {
        scopeSelectionButtons.forEachIndexed { i, button -> add(button, i) }
        setDefaultSelections()
        border = panelBorder()
        isFloatable = false
    }

    private fun scopeButton(scope: Scope): ScopeButton {
        return ScopeButton(scope).also {
            applyDefaultStyle(it)
            attachActions(it)
        }
    }

    private fun setDefaultSelections() {
        scopeSelectionButtons.firstOrNull { it.scope == File }?.setActive(true)
    }

    private fun attachActions(button: ScopeButton) {
        button.addMouseListener(
            object : MouseAdapter() {
                override fun mouseEntered(e: MouseEvent?) {
                    button.isContentAreaFilled = true
                }
                override fun mouseExited(e: MouseEvent?) {
                    button.isContentAreaFilled = button.isSelectedByUser
                }
                override fun mouseClicked(e: MouseEvent?) {
                    button.setActive(true)
                    scopeSelectionButtons.filterNot { other -> other == button }.forEach { other ->
                        applyDefaultStyle(other)
                        other.setActive(false)
                    }
                    val updatedSliceRoot =
                        sliceForSelectedScope(reachabilityPanel.elementUnderAnalysis, button.scope)
                    reachabilityPanel.refreshTree(updatedSliceRoot, this@ReachabilityScopePanel)
                }
            }
        )
    }

    private fun sliceForSelectedScope(
        elementToAnalyze: PsiElement,
        selectedScope: Scope
    ): SliceRootNode {
        val analysisParams =
            SliceAnalysisParams().apply {
                dataFlowToThis = !reachabilityPanel.dataFlowToThis
                showInstanceDereferences = true
                scope = selectedScope.analysisScope(elementToAnalyze)
            }
        return SliceDispatchService.sliceRootUsage(
            elementToAnalyze,
            elementToAnalyze.project,
            analysisParams
        )
    }
}

class ScopeButton(val scope: Scope) : JButton(scope.name, scope.icon) {

    var isSelectedByUser: Boolean = false

    fun setActive(state: Boolean) {
        isSelectedByUser = state
        isContentAreaFilled = state
    }
}
