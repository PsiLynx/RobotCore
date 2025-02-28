package org.ftc3825.opmodes


import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.command.internal.Timer
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.util.Drawing
import org.ftc3825.util.Globals
import org.ftc3825.util.Globals.State.Running
import kotlin.math.floor

//@Disabled
abstract class CommandOpMode: OpMode() {

    private var lastTime = 0L
    private lateinit var allHubs: List<LynxModule>

    abstract fun initialize()

    final override fun init() {
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

        Globals.robotVoltage =
            GlobalHardwareMap.get(
                VoltageSensor::class.java,
                "Control Hub"
            ).voltage
        initialize()
    }

    final override fun loop() {
        lastTime = System.nanoTime()
        allHubs.forEach { it.clearBulkCache() }
        CommandScheduler.update()
        lastTime = System.nanoTime()
        if(Globals.state == Running) Drawing.sendPacket()

    }

    final override fun stop() = CommandScheduler.end()

}
