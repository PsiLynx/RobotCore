package org.firstinspires.ftc.teamcode.opmodes


import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.ValueProvider
import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.Timer
import org.firstinspires.ftc.teamcode.hardware.HWManager
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.Drawing
import org.firstinspires.ftc.teamcode.util.Globals
import org.psilynx.psikit.core.Logger
import org.psilynx.psikit.core.rlog.RLOGServer
import org.psilynx.psikit.core.rlog.RLOGWriter
import org.psilynx.psikit.ftc.PsikitOpmode
import java.util.Date
import java.util.function.DoubleConsumer
import java.util.function.DoubleSupplier
import kotlin.math.floor


//@Disabled
abstract class CommandOpMode: PsikitOpmode() {

    private var lastTime = Globals.currentTime
    private lateinit var allHubs: List<LynxModule>

    abstract fun initialize()

    final override fun init() {
        allHubs = hardwareMap.getAll(LynxModule::class.java)

        HardwareMap.init(hardwareMap)
        CommandScheduler.init(hardwareMap, Timer())
        HWManager.init(hardwareMap, Timer())

        Globals.setStart()

        println("starting server...")
        val server = RLOGServer()
        Logger.addDataReceiver(server)
        Logger.addDataReceiver(RLOGWriter("/sdcard/FIRST", "logs"))
        Logger.recordMetadata("alliance", "red")

        Logger.start() // Start logging! No more data receivers, replay sources, or metadata values may be added.
        Logger.periodicAfterUser(0.0, 0.0)

        //addConfigFields()

        Telemetry.reset()
        Telemetry.initialize(telemetry)
        Telemetry.addFunction("time (ms)") {
            floor(Globals.currentTime - lastTime) / 1000
        }
        Telemetry.justUpdate().schedule()

        Globals.robotVoltage =
            hardwareMap.get(
                VoltageSensor::class.java,
                "Control Hub"
            ).voltage
        initialize()
    }

    final override fun loop() {
        lastTime = Globals.currentTime
        CommandScheduler.update()
        lastTime = Globals.currentTime
        if(Globals.running) Drawing.sendPacket()

    }

    final override fun stop() = CommandScheduler.end()

}