package org.firstinspires.ftc.teamcode.component

class Optional <T> private constructor(private val value: T?) {

    val empty = (value == null)
    val exists = !empty

    infix fun or(default: T): T = (
        if (exists) value!!
        else default
    )

    override fun toString() = if(exists) value.toString() else "empty"

    override fun equals(other: Any?) = (
            (other is Optional<T> && other.value == this.value)
            || this.value == other
        )

    companion object{
        operator fun <T> invoke(value: T) = Optional(value)
        fun <T> empty() = Optional<T>(null)
    }

    override fun hashCode(): Int {
        var result = exists.hashCode()
        result = 31 * result + (value?.hashCode() ?: 0)
        return result
    }
}