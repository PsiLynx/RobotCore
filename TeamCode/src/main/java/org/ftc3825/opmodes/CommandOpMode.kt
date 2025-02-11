package org.ftc3825.opmodes

import com.acmerobotics.dashboard.FtcDashboard
import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.subsystem.Telemetry
import kotlin.math.floor

@Disabled
abstract class CommandOpMode: LinearOpMode() {

    private lateinit var allHubs: List<LynxModule>

    private fun _initialize() {
        allHubs = hardwareMap.getAll(LynxModule::class.java)

        GlobalHardwareMap.init(hardwareMap)
        CommandScheduler.init(hardwareMap)
        allHubs.forEach { it.bulkCachingMode = LynxModule.BulkCachingMode.MANUAL }

        Telemetry.reset()
        Telemetry.initialize(telemetry!!)
        Telemetry.justUpdate().schedule()
    }
    private var lastTime = 0L
    abstract fun initialize()

    override fun runOpMode() {
        _initialize()
        initialize()
        lastTime = System.nanoTime()
        waitForStart()
        while (!isStopRequested) {
            allHubs.forEach { it.clearBulkCache() }
            CommandScheduler.update()
            val time = (System.nanoTime() - lastTime) / 1e6
            telemetry.addData("loop time", floor(time * 10) / 10)
            lastTime = System.nanoTime()

        }
        CommandScheduler.end()
    }
}
