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

    private val supportedScopes: List<Scope> = listOf(Project, Module, File)
    private val scopeSelectionButtons = supportedScopes.map(::scopeButton)

    init {
        scopeSelectionButtons.forEachIndexed { i, button -> add(button, i) }
        this.border = panelBorder()
        this.isFloatable = false
    }

    private fun scopeButton(scope: Scope): JButton {
        return ScopeButton(scope).also {
            applyDefaultStyle(it)
            attachActions(it)
        }
    }

    private fun attachActions(button: ScopeButton) {
        button.addMouseListener(
            object : MouseAdapter() {
                override fun mouseEntered(e: MouseEvent?) {
                    button.isContentAreaFilled = true
                }
                override fun mouseExited(e: MouseEvent?) {
                    button.isContentAreaFilled = false
                }

                override fun mouseClicked(e: MouseEvent?) {
                    button.isBorderPainted = !button.isBorderPainted
                    scopeSelectionButtons.filterNot { other -> other == button }.forEach { other ->
                        applyDefaultStyle(other)
                    }
                    val updatedSliceRoot = foo(reachabilityPanel.elementUnderAnalysis, button.scope)
                    reachabilityPanel.refreshTree(updatedSliceRoot, this@ReachabilityScopePanel)
                }
            }
        )
    }

    private fun foo(elementToAnalyze: PsiElement, selectedScope: Scope): SliceRootNode {
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

class ScopeButton(val scope: Scope) : JButton(scope.name, scope.icon)
