package com.github.jyoo980.reachhover.model

sealed class Tree<out A>

object Empty : Tree<Nothing>() {
    override fun toString(): String {
        return "Empty"
    }
}

data class Node<A>(val value: A, val children: MutableList<Tree<A>> = mutableListOf()) : Tree<A>() {

    fun addChild(child: Tree<A>) {
        this.children.add(child)
    }

    override fun toString(): String {
        return "Node($value, children)"
    }
}

fun <A, B> Tree<A>.map(f: (A) -> B): Tree<B> =
    when (this) {
        Empty -> Empty
        is Node<A> -> {
            val transformedChildren = children.map { it.map(f) } as MutableList
            Node(f(value), transformedChildren)
        }
    }
