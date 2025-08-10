package org.firstinspires.ftc.teamcode.opmodes


import com.qualcomm.hardware.lynx.LynxModule
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

    private var lastTime = Globals.currentTime
    private lateinit var allHubs: List<LynxModule>

    abstract fun initialize()

    final override fun runOpMode() {
        psikitSetup()
        this.hardwareMap
        allHubs = hardwareMap.getAll(LynxModule::class.java)

        HardwareMap.init(hardwareMap)
        CommandScheduler.init(hardwareMap, Timer())
        HWManager.init(hardwareMap, Timer())

        Globals.setStart()

        println("starting server...")
        val server = RLOGServer()
        Logger.addDataReceiver(server)
        Logger.addDataReceiver(RLOGWriter("/sdcard/FIRST", "logs.rlog"))
        Logger.recordMetadata("alliance", "red")

        Logger.start() // Start logging! No more data receivers, replay sources, or metadata values may be added.
        Logger.periodicAfterUser(0.0, 0.0)

        //addConfigFields()

        Telemetry.reset()
        Telemetry.initialize(telemetry)
        Telemetry.addFunction("time (ms)") {
            floor(Globals.currentTime - lastTime) / 100
        }
        Telemetry.justUpdate().schedule()

        Globals.robotVoltage = 12.0
//            hardwareMap.get(
//                VoltageSensor::class.java,
//                "Control Hub"
//            ).voltage
        initialize()

        waitForStart()

        while(!isStopRequested) {

            Logger.periodicBeforeUser()
            processHardwareMapInput()
            lastTime = Globals.currentTime
            CommandScheduler.update()
            lastTime = Globals.currentTime
            println(Drivetrain.position)
            Logger.periodicAfterUser(0.0, 0.0)

        }

        CommandScheduler.end()
    }


}