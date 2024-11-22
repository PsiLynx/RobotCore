package org.ftc3825.util

import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.fakehardware.FakeHardwareMap
import org.ftc3825.util.Globals.State.Testing

open class TestClass {
    val hardwareMap = FakeHardwareMap
    init {
        GlobalHardwareMap.init(hardwareMap)
        CommandScheduler.init(FakeHardwareMap)

        Globals.state = Testing
        Globals.timeSinceStart = 0.0
        CommandScheduler.reset()

        CommandScheduler.update()
        CommandScheduler.update()

        FakeHardwareMap.allDeviceMappings.forEach {mapping ->
            mapping.forEach {
                it.resetDeviceConfigurationForOpMode()
            }
        }
    }
}
