package org.firstinspires.ftc.teamcode.opmodes


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.command.internal.Timer
import org.firstinspires.ftc.teamcode.component.LynxModule
import org.firstinspires.ftc.teamcode.component.controller.Gamepad
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.Cameras
import org.firstinspires.ftc.teamcode.subsystem.LEDs
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.SDKLynxModule
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.SelectorInput
import org.firstinspires.ftc.teamcode.util.log
import org.psilynx.psikit.core.Logger
import org.psilynx.psikit.core.rlog.RLOGServer
import org.psilynx.psikit.core.rlog.RLOGWriter
import org.psilynx.psikit.ftc.OpModeControls
import org.psilynx.psikit.ftc.PsiKitOpMode
import org.psilynx.psikit.ftc.wrappers.GamepadWrapper

//@Disabled
abstract class CommandOpMode : LinearOpMode() {

    lateinit var driver : Gamepad
    lateinit var operator : Gamepad

    /**
     * preSelector should initialize any objects that delegate parameters to
     * SelectInput
     */
    open fun preSelector() {
        Globals
        Drivetrain
        Cameras.init()
    }
    lateinit var allHubs: List<SDKLynxModule>

    /**
     * postSelector can assume that anything initialized to SelectInput is ready
     */
    abstract fun postSelector()

    final override fun runOpMode() {
        //psiKitSetup()
        allHubs = hardwareMap.getAll(SDKLynxModule::class.java).toList()
        setupPsiKit = true
        //println("psikit setup")

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
        LEDs.justUpdate().schedule()

        RunCommand { Globals.apply{ log("Target Position") value
                Vector3D(-goalPose.y, goalPose.x, goalPose.z) / 39.37 } }.schedule()

        val voltageSensor = hardwareMap.get(
            VoltageSensor::class.java,
            "Control Hub"
        )

        driver = Gamepad(gamepad1!!)
        operator = Gamepad(gamepad2!!)

        preSelector()

        var currentSelector = 0
        while (!isStarted){
            //Logger.periodicBeforeUser()
            //processHardwareInputs()

            val current = SelectorInput.allSelectorInputs[currentSelector]
            this.telemetry.addData(
                current.name,
                current.get()
            )
            this.telemetry.update()

            if(gamepad1.dpad_left) {
                current.moveLeft()
            }

            if(gamepad1.dpad_right) {
                current.moveRight()
            }

            if(gamepad1.dpad_up) {
                currentSelector--
                if(currentSelector < 0) currentSelector = 0
            }

            if(gamepad1.dpad_down) {
                currentSelector++
                if(
                    currentSelector
                    >= SelectorInput.allSelectorInputs.size
                ) currentSelector = 0
            }
            //Logger.periodicAfterUser(0.0, 0.0)
        }
        postSelector()

        while(!isStopRequested) {
            //val startTime = Logger.getRealTimestamp()

            //Logger.periodicBeforeUser()

            //allHubs.forEach { it.clearBulkCache() }
            /*
            processHardwareInputs()
            Logger.processInputs(
                "/DriverStation/joystick1",
                driver.gamepad as GamepadWrapper
            )
            Logger.processInputs(
                "/DriverStation/joystick2",
                operator.gamepad as GamepadWrapper
            )
             */
            if(Globals.robotVoltage == 0.0){
                Globals.robotVoltage = voltageSensor.voltage
            }


            log("voltage sensor/name") value voltageSensor.deviceName
            log("voltage sensor/voltage") value voltageSensor.voltage

            //val periodicBeforeEndTime = Logger.getRealTimestamp()
            CommandScheduler.update()
            /*
            Logger.periodicAfterUser(
                Logger.getRealTimestamp() - periodicBeforeEndTime,
                periodicBeforeEndTime - startTime
            )
             */

        }
        CommandScheduler.end()
        OpModeControls.started = false
        OpModeControls.stopped = false
        //Logger.end()
    }
    companion object {
        var setupPsiKit = false
    }
}
