package org.ftc3825.opmodes

import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.subsystem.Telemetry

@Disabled
abstract class CommandOpMode: OpMode() {

    private lateinit var allHubs: List<LynxModule>

    fun initialize() {
        GlobalHardwareMap.init(hardwareMap)
        CommandScheduler.init(hardwareMap)
        Telemetry.telemetry = telemetry!!

        allHubs = hardwareMap.getAll(LynxModule::class.java)
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
