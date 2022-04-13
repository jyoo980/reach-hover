package com.github.jyoo980.reachhover.ui.scope

import com.intellij.ui.JBColor
import com.intellij.util.ui.JBUI
import java.awt.Dimension
import javax.swing.JButton
import javax.swing.border.Border

interface ScopePanelStyler {

    fun panelBorder(): Border = JBUI.Borders.customLine(JBColor.border(), 1, 0, 0, 0)

    fun applyDefaultStyle(button: JButton) {
        button.isOpaque = false
        button.isContentAreaFilled = false
        button.isBorderPainted = false
        button.border = JBUI.Borders.empty()
        button.maximumSize = Dimension(75, 25)
    }
}
