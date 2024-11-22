package org.ftc3825.opmodes

import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.GlobalHardwareMap

@Disabled
abstract class CommandOpMode: OpMode() {

    private lateinit var allHubs: List<LynxModule>

    fun initialize() {
        allHubs = hardwareMap.getAll(LynxModule::class.java)

        GlobalHardwareMap.init(hardwareMap)
        CommandScheduler.init(hardwareMap)
        allHubs.forEach { it.bulkCachingMode = LynxModule.BulkCachingMode.MANUAL }
    }

    override fun loop() {
        allHubs.forEach { it.clearBulkCache() }
        CommandScheduler.update()
    }

    override fun stop() {
        CommandScheduler.end()
    }
}
