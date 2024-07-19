package org.firstinspires.ftc.teamcode.util

import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.sim.DataAnalyzer
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.Globals.State.Testing

open class TestClass {
    val hardwareMap = FakeHardwareMap

    init {
        Globals.state = Testing

        CommandScheduler.init(hardwareMap)

        CommandScheduler.update()
        CommandScheduler.update()

        FakeHardwareMap.allDeviceMappings.forEach {mapping ->
            mapping.forEach {
                it.resetDeviceConfigurationForOpMode()
            }
        }

        DataAnalyzer.loadTestData()
        DataAnalyzer.analyze()

        println("initialized")
    }
}