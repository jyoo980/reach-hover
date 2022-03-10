package com.github.jyoo980.reachhover.model

sealed class Tree<out A>

object Empty : Tree<Nothing>()

data class Node<A>(val value: A, val children: List<Tree<A>>) : Tree<A>()

fun <A, B> Tree<A>.map(f: (A) -> B): Tree<B> =
    when (this) {
        Empty -> Empty
        is Node<A> -> {
            val mappedChildren = children.map { it.map(f) }
            Node(f(value), mappedChildren)
        }
    }
