package org.firstinspires.ftc.teamcode.logging

import org.firstinspires.ftc.teamcode.util.Globals

interface Input {

    /**
     * must be unique to this object for logging purposes
     */
    val uniqueName: String

    /**
     * @return the value that would be returned through normal operation
     */
    fun getRealValue(): Array<Double>

    /**
     * @return while running, real value. in replay, the recorded value
     */
    fun getValue() = (
        if(Globals.logReplay) Replayer.valueForName(uniqueName)
        else getRealValue()
    )

    fun logged(): Input{
        Logger.add(this)
        return this
    }
}