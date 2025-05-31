package org.firstinspires.ftc.teamcode.hardware

class ListView <T>(
    val start: Int,
    val end: Int,
    val backingList: MutableList<T>
) :AbstractMutableList<T>() {
    override val size = end - start + 1

    override fun get(index: Int): T {
        require(index in 0..size-1) { "Index out of bounds" }
        return backingList[start + index]
    }

    override fun set(index: Int, element: T): T {
        require(index in 0..size-1) { "Index out of bounds for analog" }
        val old = backingList[start + index]
        backingList[start + index] = element
        return old
    }

    override fun add(index: Int, element: T) {
        throw UnsupportedOperationException("Fixed-size view")
    }

    override fun removeAt(index: Int): T {
        throw UnsupportedOperationException("Fixed-size view")
    }
}