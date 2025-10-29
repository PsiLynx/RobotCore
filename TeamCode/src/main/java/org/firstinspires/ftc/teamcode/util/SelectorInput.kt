package org.firstinspires.ftc.teamcode.util

import kotlin.reflect.KProperty

/**
 * this class represents something you can select at the start of an opmode.
 * values delegated to this can change between an opmodes `beforeSelect` and
 * `afterSelect` methods.
 * @param name display name for this value
 * @param values the list of selectable values. values[0] is the defualt
 */
open class SelectorInput<T>(
    val name: String,
    val values: List<T>
) {
    constructor(name: String, vararg values: T): this(name, values.toList())

    init {
        allSelectorInputs.add(this)
    }

    private var currentInput = 0

    operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ) = values[currentInput]

    fun get() = values[currentInput]

    fun moveLeft() {
        currentInput = (
            values.size + currentInput - 1
        ) % values.size
    }

    fun moveRight() {
        currentInput = (
            values.size + currentInput + 1
        ) % values.size
    }

    companion object {
        val allSelectorInputs = mutableListOf<SelectorInput<*>>()
    }

}