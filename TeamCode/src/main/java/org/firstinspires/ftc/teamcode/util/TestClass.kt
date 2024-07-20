package org.firstinspires.ftc.teamcode.util

import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.sim.DataAnalyzer
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.Globals.State.Testing

open class TestClass {
    val hardwareMap = FakeHardwareMap

    init {
        if(!initialized) {
            Globals.state = Testing

            CommandScheduler.init(hardwareMap)

            CommandScheduler.update()
            CommandScheduler.update()

            DataAnalyzer.loadTestData()
            DataAnalyzer.analyze()
        }
        initialized = true

        FakeHardwareMap.allDeviceMappings.forEach {mapping ->
            mapping.forEach {
                it.resetDeviceConfigurationForOpMode()
            }
        }
        println("initialized")
    }
    companion object{
        var initialized = false
    }
}