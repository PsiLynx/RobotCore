package org.firstinspires.ftc.teamcode.component

/**
 * basically an optional, stores the write to a component, and allows for a dynamic default value
 */
class Write(power: Double?) {
    private val pow: Double = power ?: 0.0
    val exists = (power != null)

    infix fun or(default: Double): Double = (
        if (exists) pow
        else default
    )

    override fun toString() = if(exists) pow.toString() else "empty"

    companion object{
        fun empty() = Write(null)
    }
}