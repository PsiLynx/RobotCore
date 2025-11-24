package org.firstinspires.ftc.teamcode.opmodes


import com.qualcomm.robotcore.hardware.VoltageSensor
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.Timer
import org.firstinspires.ftc.teamcode.component.controller.Gamepad
import org.firstinspires.ftc.teamcode.hardware.HWManager
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.SelectorInput
import org.firstinspires.ftc.teamcode.util.log
import org.psilynx.psikit.core.Logger
import org.psilynx.psikit.core.rlog.RLOGServer
import org.psilynx.psikit.core.rlog.RLOGWriter
import org.psilynx.psikit.ftc.PsiKitOpMode
import org.psilynx.psikit.ftc.wrappers.GamepadWrapper

//@Disabled
abstract class CommandOpMode: PsiKitOpMode() {

    lateinit var driver : Gamepad
    lateinit var operator : Gamepad

    /**
     * preSelector should initialize any objects that delegate parameters to
     * SelectInput
     */
    open fun preSelector() {
        Globals
        Drivetrain
    }

    /**
     * postSelector can assume that anything initialized to SelectInput is ready
     */
    abstract fun postSelector()

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

        driver = Gamepad(GamepadWrapper(gamepad1!!))
        operator = Gamepad(GamepadWrapper(gamepad2!!))

        postSelector()

        var currentSelector = 0
        while (!psiKitIsStarted){
            Logger.periodicBeforeUser()
            processHardwareInputs()

            val current = SelectorInput.allSelectorInputs[currentSelector]
            this.telemetry.addData(
                current.name,
                current.get()
            )
            this.telemetry.update()

            if(driver.dpadLeft.onTrue(Command()).isTriggered) {
                current.moveLeft()
            }

            if(driver.dpadLeft.onTrue(Command()).isTriggered) {
                current.moveRight()
            }

            if(driver.dpadUp.onTrue(Command()).isTriggered) {
                currentSelector++
            }

            if(driver.dpadDown.onTrue(Command()).isTriggered) {
                currentSelector--
                if(currentSelector < 0) currentSelector = 0
            }
            Logger.periodicAfterUser(0.0, 0.0)
        }
        //if(Globals.running == true) waitForStart()

        while(!psiKitIsStopRequested) {
            val startTime = Logger.getRealTimestamp()

            Logger.periodicBeforeUser()

            //allHubs.forEach { it.clearBulkCache() }
            processHardwareInputs()
            Logger.processInputs(
                "/DriverStation/joystick1",
                driver.gamepad as GamepadWrapper
            )
            Logger.processInputs(
                "/DriverStation/joystick2",
                operator.gamepad as GamepadWrapper
            )
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
