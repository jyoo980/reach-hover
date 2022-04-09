package com.github.jyoo980.reachhover.ui.scope

import com.intellij.util.ui.EmptyIcon
import javax.swing.Icon

sealed class Scope(val name: String, val icon: Icon = EmptyIcon.ICON_0)

object Project : Scope(name = "In Project")

object Module : Scope(name = "Module")

object File : Scope(name = "File")
