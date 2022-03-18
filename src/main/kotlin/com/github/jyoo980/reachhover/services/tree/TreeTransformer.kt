package com.github.jyoo980.reachhover.services.tree

import com.github.jyoo980.reachhover.model.Tree

interface TreeTransformer<A, B> {

    fun transform(tree: Tree<A>): Tree<B>
}
