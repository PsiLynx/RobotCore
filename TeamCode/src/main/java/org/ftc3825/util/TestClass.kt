package org.ftc3825.util

import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.fakehardware.FakeHardwareMap
import org.ftc3825.sim.DataAnalyzer
import org.ftc3825.util.Globals.State.Testing
import org.ftc3825.subsystem.TelemetrySubsystem

open class TestClass {
    val hardwareMap = FakeHardwareMap

    init {
        if(!initialized) {
            Globals.state = Testing

            CommandScheduler.reset()

            CommandScheduler.init(hardwareMap)

            TelemetrySubsystem.init(hardwareMap)

            CommandScheduler.update()
            CommandScheduler.update()

        }
        initialized = true

        FakeHardwareMap.allDeviceMappings.forEach {mapping ->
            mapping.forEach {
                it.resetDeviceConfigurationForOpMode()
            }
        }
    }
    companion object{
        var initialized = false
    }
}
