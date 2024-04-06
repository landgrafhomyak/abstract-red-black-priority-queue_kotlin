@file:JvmName("AbstractRedBlackPriorityQueueKt")
package ru.landgrafhomyk.collections

import kotlin.jvm.JvmName
import ru.landgrafhomyak.collections.AbstractRedBlackTree

@Suppress("FunctionName")
abstract class AbstractRedBlackPriorityQueue<NODE : Any> {
    protected abstract fun _getParent(node: NODE): NODE?
    protected abstract fun _setParent(node: NODE, parent: NODE?)
    protected abstract fun _getLeftChild(node: NODE): NODE?
    protected abstract fun _setLeftChild(node: NODE, child: NODE?)
    protected abstract fun _getRightChild(node: NODE): NODE?
    protected abstract fun _setRightChild(node: NODE, child: NODE?)
    protected abstract fun _getColor(node: NODE): AbstractRedBlackTree.Color
    protected abstract fun _setColor(node: NODE, color: AbstractRedBlackTree.Color)

    inner class RedBlackTreeSubst : AbstractRedBlackTree<NODE>() {
        override fun _getColor(node: NODE): Color =
            this@AbstractRedBlackPriorityQueue._getColor(node)

        override fun _getLeftChild(node: NODE): NODE? =
            this@AbstractRedBlackPriorityQueue._getLeftChild(node)

        override fun _getParent(node: NODE): NODE? =
            this@AbstractRedBlackPriorityQueue._getParent(node)

        override fun _getRightChild(node: NODE): NODE? =
            this@AbstractRedBlackPriorityQueue._getRightChild(node)

        override fun _setColor(node: NODE, color: Color) =
            this@AbstractRedBlackPriorityQueue._setColor(node, color)

        override fun _setLeftChild(node: NODE, child: NODE?) =
            this@AbstractRedBlackPriorityQueue._setLeftChild(node, child)

        override fun _setParent(node: NODE, parent: NODE?) =
            this@AbstractRedBlackPriorityQueue._setParent(node, parent)

        override fun _setRightChild(node: NODE, child: NODE?) =
            this@AbstractRedBlackPriorityQueue._setRightChild(node, child)
    }


    private val treeImpl = this.RedBlackTreeSubst()

}