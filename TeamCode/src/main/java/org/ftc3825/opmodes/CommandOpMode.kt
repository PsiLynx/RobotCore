package org.ftc3825.opmodes


import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.command.internal.Timer
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.util.Drawing
import org.ftc3825.util.Globals
import org.ftc3825.util.Globals.State.Running
import kotlin.math.floor

@Disabled
abstract class CommandOpMode: LinearOpMode() {

    private lateinit var allHubs: List<LynxModule>

    private fun internalInit() {
        allHubs = hardwareMap.getAll(LynxModule::class.java)

        GlobalHardwareMap.init(hardwareMap)
        CommandScheduler.init(hardwareMap, Timer())
        allHubs.forEach { it.bulkCachingMode = LynxModule.BulkCachingMode.MANUAL }

        Telemetry.reset()
        Telemetry.initialize(telemetry!!)
        Telemetry.addFunction("time") {
            floor((System.nanoTime() - lastTime) / 1e6 * 10) / 10
        }
        Telemetry.justUpdate().schedule()
    }
    private var lastTime = 0L
    abstract fun initialize()

    override fun runOpMode() {
        internalInit()
        initialize()

        lastTime = System.nanoTime()
        waitForStart()
        while (!isStopRequested) {

            allHubs.forEach { it.clearBulkCache() }
            CommandScheduler.update()
            lastTime = System.nanoTime()
            if(Globals.state == Running) Drawing.sendPacket()

        }
        CommandScheduler.end()
    }
}
