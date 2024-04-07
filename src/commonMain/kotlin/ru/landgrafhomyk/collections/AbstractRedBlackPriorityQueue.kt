@file:JvmName("AbstractRedBlackPriorityQueueKt")

package ru.landgrafhomyk.collections

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmName
import ru.landgrafhomyak.collections.AbstractRedBlackTree

@Suppress("FunctionName", "PropertyName")
abstract class AbstractRedBlackPriorityQueue<NODE : Any> {
    val PARENT_INITIALIZER: NODE? get() = null
    val LEFT_CHILD_INITIALIZER: NODE? get() = null
    val RIGHT_CHILD_INITIALIZER: NODE? get() = null
    val COLOR_INITIALIZER: AbstractRedBlackTree.Color get() = AbstractRedBlackTree.Color.RED

    protected abstract fun _getParent(node: NODE): NODE?
    protected abstract fun _setParent(node: NODE, parent: NODE?)
    protected abstract fun _getLeftChild(node: NODE): NODE?
    protected abstract fun _setLeftChild(node: NODE, child: NODE?)
    protected abstract fun _getRightChild(node: NODE): NODE?
    protected abstract fun _setRightChild(node: NODE, child: NODE?)
    protected abstract fun _getColor(node: NODE): AbstractRedBlackTree.Color
    protected abstract fun _setColor(node: NODE, color: AbstractRedBlackTree.Color)
    protected abstract fun _hasHigherPriority(node: NODE, than: NODE): Boolean
    protected open fun _checkSame(leftNode: NODE, rightNode: NODE?) = leftNode === rightNode

    private inner class RedBlackTreeSubst : AbstractRedBlackTree<NODE>() {
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

        override fun _checkSame(leftNode: NODE, rightNode: NODE?): Boolean =
            this@AbstractRedBlackPriorityQueue._checkSame(leftNode, rightNode)
    }


    private val treeImpl = this.RedBlackTreeSubst()
    private var _maxPriorityNode: NODE? = null

    @Suppress("LeakingThis")
    val maxPriorityNodeOrNull by this::_maxPriorityNode

    val maxPriorityNodeOrThrow: NODE
        get() = this._maxPriorityNode ?: throw NoSuchElementException("Priority queue is empty")

    /**
     * Root of the binary search tree used to implement this priority queue.
     * Right child have higher priority, left child have same or lower priority.
     */
    val bstRoot by this.treeImpl::root

    fun link(node: NODE) {
        val root = this.treeImpl.root
        if (root == null) {
            this.treeImpl.root = node
            this.treeImpl.balanceAfterLinking(node)
            this._maxPriorityNode = node
            return
        }

        var p: NODE = root
        while (true) {
            if (this._hasHigherPriority(node, p)) {
                val newP = this._getRightChild(p)
                if (newP != null) {
                    p = newP
                    continue
                }
                this._setRightChild(p, node)
                if (this._checkSame(p, this._maxPriorityNode))
                    this._maxPriorityNode = node
            } else {
                val newP = this._getLeftChild(p)
                if (newP != null) {
                    p = newP
                    continue
                }
                this._setLeftChild(p, node)
            }
            this._setParent(node, p)
            this.treeImpl.balanceAfterLinking(node)
            break
        }
    }

    @PublishedApi
    internal fun __unlinkMaxPriorityNode(node: NODE): NODE {
        this._maxPriorityNode = this._getParent(node)
        this.treeImpl.unlink(node)
        return node
    }

    fun unlinkMaxPriorityNodeOrThrow(): NODE = this.__unlinkMaxPriorityNode(this.maxPriorityNodeOrThrow)
    fun unlinkMaxPriorityNodeOrNull(): NODE? {
        return this.__unlinkMaxPriorityNode(this._maxPriorityNode ?: return null)
    }

    /**
     * Pops node only if [predicate] returns true.
     * If queue is empty [predicate] wouldn't be called and function will return `null`.
     *
     * @param predicate function received node to be popped and returns `true` if it should
     * be popped, otherwise `false`.
     *
     * @return Popped node ***only if*** the [collection is not empty][AbstractRedBlackPriorityQueue.isNotEmpty]
     * and ***[predicate] returned `true`***, otherwise `null`.
     */
    @OptIn(ExperimentalContracts::class)
    inline fun conditionalUnlinkMaxPriorityNode(predicate: (NODE) -> Boolean): NODE? {
        contract {
            callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
        }
        val node = this.maxPriorityNodeOrNull ?: return null
        if (predicate(node))
            this.__unlinkMaxPriorityNode(node)
        return node
    }

    fun isEmpty(): Boolean = this._maxPriorityNode == null

    fun isNotEmpty(): Boolean = this._maxPriorityNode != null

    fun clear() {
        this.treeImpl.root = null
    }
}