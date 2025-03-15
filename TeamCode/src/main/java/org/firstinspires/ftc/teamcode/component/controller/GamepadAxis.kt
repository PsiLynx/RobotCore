package org.firstinspires.ftc.teamcode.component.controller

import java.util.function.DoubleSupplier
import kotlin.math.pow
import kotlin.math.sign

class GamepadAxis(private val supplier: DoubleSupplier): Number() {

    override fun toShort()  = supplier.asDouble.toInt().toShort()
    override fun toByte()   = supplier.asDouble.toInt().toByte()
    override fun toFloat()  = supplier.asDouble.toFloat()
    override fun toLong()   = supplier.asDouble.toLong()
    override fun toInt()    = supplier.asDouble.toInt()
    override fun toDouble() = supplier.asDouble

    val sq: Double
        get() = supplier.asDouble.pow(2) * supplier.asDouble.sign

}