package org.firstinspires.ftc.teamcode.opmodes


import com.qualcomm.robotcore.hardware.VoltageSensor
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.Timer
import org.firstinspires.ftc.teamcode.hardware.HWManager
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.Globals
import org.psilynx.psikit.core.Logger
import org.psilynx.psikit.core.rlog.RLOGServer
import org.psilynx.psikit.core.rlog.RLOGWriter
import org.psilynx.psikit.ftc.PsiKitOpMode
import java.time.Instant
import java.util.Date


//@Disabled
abstract class CommandOpMode: PsiKitOpMode() {

    abstract fun initialize()

    final override fun runOpMode() {
        psiKitSetup()
        println("psikit setup")

        HWManager.init(Timer())
        HardwareMap.init(hardwareMap)
        CommandScheduler.init(hardwareMap, Timer())

        println("starting server...")
        val server = RLOGServer()
        Logger.addDataReceiver(server)
        val writer = RLOGWriter(
            if(Globals.running) "/sdcard/FIRST" else "logs/",
            //Date.from(Instant.now()).toString()
            "logs.rlog"
        )
        writer.start()
        Logger.addDataReceiver(writer);
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
            val startTime = Logger.getRealTimestamp()

            Logger.periodicBeforeUser()
            processHardwareInputs()
            val periodicBeforeEndTime = Logger.getRealTimestamp()
            CommandScheduler.update()
            Logger.periodicAfterUser(
                Logger.getRealTimestamp() - periodicBeforeEndTime,
                periodicBeforeEndTime - startTime
            )

        }

        CommandScheduler.end()
        Logger.end()
    }


}