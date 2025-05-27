package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.HardwareDevice

abstract class IOComponent: Comparable<IOComponent>, Component {

    abstract var priority: Double
    abstract val ioOpTime: Double

    abstract fun ioOp()

    override fun compareTo(other: IOComponent)
        = ( (this.priority - other.priority) * 1000 ).toInt()

}