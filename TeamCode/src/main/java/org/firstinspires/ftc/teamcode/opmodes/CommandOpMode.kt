package org.firstinspires.ftc.teamcode.opmodes


import com.qualcomm.robotcore.hardware.VoltageSensor
import com.qualcomm.hardware.lynx.LynxModule
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.Timer
import org.firstinspires.ftc.teamcode.component.controller.Gamepad
import org.firstinspires.ftc.teamcode.hardware.HWManager
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.Globals
import org.psilynx.psikit.ftc.PsiKitOpMode

//@Disabled
abstract class CommandOpMode: PsiKitOpMode() {

    lateinit var driver : Gamepad
    lateinit var operator : Gamepad

    abstract fun initialize()

    final override fun runOpMode() {
        psiKitSetup()
        println("psikit setup")

        HWManager.init(Timer())
        HardwareMap.init(hardwareMap)
        CommandScheduler.init(hardwareMap, Timer())

        println("starting server...")
        val server = RLOGServer()
        val writer = RLOGWriter(
            if(Globals.running) "/sdcard/FIRST" else ".",
            "logs.rlog"
        )

        Logger.addDataReceiver(server)
        Logger.addDataReceiver(writer);

        server.start()
        writer.start()

        Logger.recordMetadata("alliance", "red")

        Logger.start()
        Logger.periodicAfterUser(0.0, 0.0)

        Telemetry.reset()
        Telemetry.initialize(telemetry)
        Telemetry.justUpdate().schedule()

        Globals.robotVoltage =
            hardwareMap.get(
               VoltageSensor::class.java,
               "Control Hub"
            ).voltage

        driver = Gamepad(gamepad1!!)
        operator = Gamepad(gamepad2!!)
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
