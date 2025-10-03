package org.firstinspires.ftc.teamcode.opmodes


import com.qualcomm.robotcore.hardware.VoltageSensor
import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.hardware.lynx.LynxModule.BulkCachingMode.MANUAL
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.Timer
import org.firstinspires.ftc.teamcode.component.controller.Gamepad
import org.firstinspires.ftc.teamcode.hardware.HWManager
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.log
import org.psilynx.psikit.core.Logger
import org.psilynx.psikit.core.rlog.RLOGServer
import org.psilynx.psikit.core.rlog.RLOGWriter
import org.psilynx.psikit.ftc.PsiKitOpMode

//@Disabled
abstract class CommandOpMode: PsiKitOpMode() {

    lateinit var driver : Gamepad
    lateinit var operator : Gamepad

    abstract fun initialize()

    final override fun runOpMode() {
        psiKitSetup()
        //allHubs.forEach { it.bulkCachingMode = MANUAL }
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

        Logger.recordMetadata("alliance", "red")

        Logger.start()
        Logger.periodicAfterUser(0.0, 0.0)

        Telemetry.reset()
        Telemetry.initialize(telemetry)
        Telemetry.justUpdate().schedule()

        val voltageSensor = hardwareMap.get(
            VoltageSensor::class.java,
            "Control Hub"
        )

        driver = Gamepad(gamepad1!!)
        operator = Gamepad(gamepad2!!)
        initialize()

        if(Globals.running == true) waitForStart()

        while(!psiKitIsStopRequested) {
            val startTime = Logger.getRealTimestamp()

            Logger.periodicBeforeUser()

            //allHubs.forEach { it.clearBulkCache() }
            processHardwareInputs()
            if(Globals.robotVoltage == 0.0){
                Globals.robotVoltage = voltageSensor.voltage
            }

            log("voltage sensor/name") value voltageSensor.deviceName
            log("voltage sensor/voltage") value voltageSensor.voltage

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
