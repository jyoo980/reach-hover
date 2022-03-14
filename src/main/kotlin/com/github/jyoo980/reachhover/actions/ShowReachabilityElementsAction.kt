package com.github.jyoo980.reachhover.actions

import com.github.jyoo980.reachhover.model.ReachabilityContext
import com.github.jyoo980.reachhover.model.map
import com.github.jyoo980.reachhover.ui.ReachabilityViewElement
import com.intellij.openapi.diagnostic.Logger

class ShowReachabilityElementsAction {

    private val logger: Logger = Logger.getInstance(ShowReachabilityElementsAction::class.java)

    fun performForContext(context: ReachabilityContext) {
        val reachabilityViewElements =
            context.tree.map { metadata -> metadata.element?.let { ReachabilityViewElement(it) } }
        // TODO: create a view session here?
    }
}
