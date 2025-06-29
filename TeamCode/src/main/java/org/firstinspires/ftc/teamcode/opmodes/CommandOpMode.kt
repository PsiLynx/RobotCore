package org.firstinspires.ftc.teamcode.opmodes


import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.ValueProvider
import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.psilynx.psikit.LogFileUtil
import org.psilynx.psikit.Logger
import org.psilynx.psikit.WPILOGReader
import org.psilynx.psikit.WPILOGWriter
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.Timer
import org.firstinspires.ftc.teamcode.hardware.HWManager
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.Drawing
import org.firstinspires.ftc.teamcode.util.Globals
import org.psilynx.psikit.RLOGServer
import java.util.function.DoubleConsumer
import java.util.function.DoubleSupplier
import kotlin.math.floor


//@Disabled
abstract class CommandOpMode: OpMode() {

    private var lastTime = Globals.currentTime
    private lateinit var allHubs: List<LynxModule>

    abstract fun initialize()

    final override fun init() {
        allHubs = hardwareMap.getAll(LynxModule::class.java)

        HardwareMap.init(hardwareMap)
        CommandScheduler.init(hardwareMap, Timer())
        HWManager.init(hardwareMap, Timer())

        Globals.setStart()
        val server = RLOGServer()
        server.start()
        Logger.addDataReceiver(server)
        Logger.setTimeSource { Globals.currentTime }
        if (Globals.running) {
            Logger.addDataReceiver(WPILOGWriter()) // Log to a USB stick ("/U/logs")
        } else {
            // setUseTiming(false) // Run as fast as possible
            val logPath =
                LogFileUtil.findReplayLog() // Pull the replay log from AdvantageScope (or prompt the user)
            Logger.setReplaySource(WPILOGReader(logPath)) // Read replay log
            Logger.addDataReceiver(
                WPILOGWriter(
                    LogFileUtil.addPathSuffix(
                        logPath,
                        "_sim"
                    )
                )
            ) // Save outputs to a new log
        }
        Logger.recordMetadata("alliance", "red")

        Logger.start() // Start logging! No more data receivers, replay sources, or metadata values may be added.

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

    private fun addConfigFields() {
        hardwareMap.dcMotor.entrySet().forEach { entry ->
            val motor = entry.value
            FtcDashboard.getInstance().addConfigVariable(
                " Motors",
                entry.key,
                Provider(motor::getPower, motor::setPower)
            )
        }

        hardwareMap.servo.entrySet().forEach { entry ->
            val servo = entry.value
            FtcDashboard.getInstance().addConfigVariable(
                " Servos",
                entry.key,
                Provider(servo::getPosition, servo::setPosition)
            )
        }

        hardwareMap.crservo.entrySet().forEach { entry ->
            val crservo = entry.value
            FtcDashboard.getInstance().addConfigVariable(
                " CR Servos",
                entry.key,
                Provider(crservo::getPower, crservo::setPower)
            )
        }
    }

    final override fun loop() {
        lastTime = Globals.currentTime
        CommandScheduler.update()
        lastTime = Globals.currentTime
        if(Globals.running) Drawing.sendPacket()

    }

    final override fun stop() = CommandScheduler.end()

}
class Provider (
    private val get: DoubleSupplier,
    private val set: DoubleConsumer
) : ValueProvider<Double> {
    override fun get() = get.asDouble
    override fun set(value: Double) = set.accept(value)
}
