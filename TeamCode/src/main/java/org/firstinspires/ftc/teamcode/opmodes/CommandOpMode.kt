package org.firstinspires.ftc.teamcode.opmodes


import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver
import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.Timer
import org.firstinspires.ftc.teamcode.hardware.HWManager
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.Globals
import org.psilynx.psikit.core.Logger
import org.psilynx.psikit.core.rlog.RLOGServer
import org.psilynx.psikit.core.rlog.RLOGWriter
import org.psilynx.psikit.ftc.PsiKitOpMode
import kotlin.math.floor


//@Disabled
abstract class CommandOpMode: PsiKitOpMode() {

    private lateinit var allHubs: List<LynxModule>

    abstract fun initialize()

    final override fun runOpMode() {
        allHubs = hardwareMap.getAll(LynxModule::class.java)
        psikitSetup()
        println("psikit setup")

        HWManager.init(allHubs, Timer())
        HardwareMap.init(hardwareMap)
        CommandScheduler.init(hardwareMap, Timer())

        Globals.setStart()

        println("starting server...")
        val server = RLOGServer()
        Logger.addDataReceiver(server)
        //Logger.addDataReceiver(RLOGWriter("/sdcard/FIRST", "logs.rlog"))
        Logger.recordMetadata("alliance", "red")

        Logger.start() // Start logging! No more data receivers, replay sources, or metadata values may be added.
        Logger.periodicAfterUser(0.0, 0.0)

        //addConfigFields()

        Telemetry.reset()
        Telemetry.initialize(telemetry)
        Telemetry.justUpdate().schedule()

        Globals.robotVoltage =
            hardwareMap.get(
               VoltageSensor::class.java,
               "Control Hub"
            ).voltage

        initialize()

        if(Globals.running == true) waitForStart()

        while(!isStopRequested) {
            println("loop!")

            Logger.periodicBeforeUser()
            processHardwareInputs()
            CommandScheduler.update()
            Logger.periodicAfterUser(0.0, 0.0)

        }

        CommandScheduler.end()
    }


}