package com.github.jyoo980.reachhover.ui.scope

import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JButton
import javax.swing.JToolBar

class ReachabilityScopePanel : JToolBar(), ScopePanelStyler {

    private val supportedScopes: List<Scope> = listOf(Project, Module, File)
    private val scopeSelectionButtons = supportedScopes.map(::scopeButton)

    init {
        scopeSelectionButtons.forEachIndexed { i, button -> add(button, i) }
        this.border = panelBorder()
        this.isFloatable = false
    }

    private fun scopeButton(scope: Scope): JButton {
        return JButton(scope.name).also {
            applyDefaultStyle(it)
            it.addMouseListener(
                object : MouseAdapter() {
                    override fun mouseEntered(e: MouseEvent?) {
                        it.isContentAreaFilled = true
                    }
                    override fun mouseExited(e: MouseEvent?) {
                        it.isContentAreaFilled = false
                    }

                    override fun mouseClicked(e: MouseEvent?) {
                        it.isBorderPainted = !it.isBorderPainted
                        scopeSelectionButtons.filterNot { other -> other == it }.forEach { other ->
                            applyDefaultStyle(other)
                        }
                    }
                }
            )
        }
    }
}
