package org.firstinspires.ftc.teamcode.opmodes


import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.ValueProvider
import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.command.internal.Timer
import org.firstinspires.ftc.teamcode.hardware.HWQue
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.Drawing
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.Globals.State.Running
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
        HWQue.init(Timer())

        allHubs.forEach { it.bulkCachingMode = LynxModule.BulkCachingMode.MANUAL }

        addConfigFields()

        Telemetry.reset()
        Telemetry.initialize(telemetry!!)
        Telemetry.addFunction("time (ms)") {
            floor(Globals.currentTime - lastTime) / 1000
        }
        Telemetry.justUpdate().schedule()

        Globals.robotVoltage =
            HardwareMap.get(
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
        allHubs.forEach { it.clearBulkCache() }
        CommandScheduler.update()
        lastTime = Globals.currentTime
        if(Globals.state == Running) Drawing.sendPacket()

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
