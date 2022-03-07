package com.github.jyoo980.reachhover.model

data class Tree<A>(val value: A) {
    var parent: Tree<A>? = null
    // TODO: think about whether I can make this a `val` not a `var`.
    var children: MutableList<Tree<A>> = mutableListOf()

    fun addChild(child: Tree<A>) {
        children.add(child)
        child.parent = this
    }
}
