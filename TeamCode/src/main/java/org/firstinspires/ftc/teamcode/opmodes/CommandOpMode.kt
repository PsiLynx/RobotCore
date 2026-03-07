package org.firstinspires.ftc.teamcode.opmodes


import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.hardware.lynx.LynxModule.BulkCachingMode.MANUAL
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.firstinspires.ftc.teamcode.shooter.CompTargets
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.command.internal.Timer
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.component.controller.Gamepad
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.LEDs
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.SelectorInput
import org.firstinspires.ftc.teamcode.util.log
import org.psilynx.psikit.core.Logger
import org.psilynx.psikit.core.rlog.RLOGServer
import org.psilynx.psikit.core.rlog.RLOGWriter
import org.psilynx.psikit.ftc.FtcLoggingSession
import org.psilynx.psikit.ftc.OpModeControls
import org.psilynx.psikit.ftc.PsiKitLinearOpMode

//@Disabled
abstract class CommandOpMode: LinearOpMode() {

    lateinit var driver : Gamepad
    lateinit var operator : Gamepad

    lateinit var allHubs : List<LynxModule>
    val psiKit = FtcLoggingSession()

    /**
     * preSelector should initialize any objects that delegate parameters to
     * SelectInput
     */
    open fun preSelector() {
        Globals
        TankDrivetrain.motors.forEach {
            it.setZeroPowerBehavior(Motor.ZeroPower.FLOAT)
        }
        //Cameras.init()
    }
    /**
     * postSelector can assume that anything initialized to SelectInput is ready
     */
    abstract fun postSelector()

    final override fun runOpMode() {
        HardwareMap.init(hardwareMap)
        Logger.recordMetadata("alliance", "red")
        psiKit.start(this, 5800)

        allHubs = this.hardwareMap.getAll(LynxModule::class.java)
        allHubs.forEach { it.bulkCachingMode = MANUAL }

        CommandScheduler.init(hardwareMap, Timer())

        Telemetry.reset()
        Telemetry.initialize(telemetry)
        Telemetry.justUpdate().schedule()
        LEDs.justUpdate().schedule()

        RunCommand { Globals.apply{
            log("Target Position") value (
                Vector3D(
                    -CompTargets.compGoalPos().y,
                    CompTargets.compGoalPos().x,
                    CompTargets.compGoalPos().z
                ) / 39.37
            )
        } }.schedule()

        val voltageSensor = hardwareMap.get(
            VoltageSensor::class.java,
            "Control Hub"
        )
        Globals.robotVoltage = voltageSensor.voltage


        driver = Gamepad(gamepad1!!)
        operator = Gamepad(gamepad2!!)

        preSelector()

        var currentSelector = 0
        while (!isStarted && Globals.unitTesting == false){
            CommandScheduler.update()
            Logger.periodicBeforeUser()
            psiKit.logOncePerLoop(this)

            if(Globals.robotVoltage == 0.0){
                Globals.robotVoltage = voltageSensor.voltage
            }

            val current = SelectorInput.allSelectorInputs[currentSelector]
            this.telemetry.addData(
                current.name,
                current.get()
            )
            this.telemetry.update()

            driver.dpadLeft.onTrue(InstantCommand{current.moveLeft()})

            driver.dpadRight.onTrue(InstantCommand{current.moveRight()})

            driver.dpadUp.onTrue(InstantCommand {
                currentSelector--
                if(currentSelector < 0) currentSelector = 0
            })

            driver.dpadDown.onTrue(InstantCommand {
                currentSelector++
                if(
                    currentSelector
                    >= SelectorInput.allSelectorInputs.size
                ) currentSelector = 0
            })
            Logger.periodicAfterUser(0.0, 0.0)
        }
        postSelector()
        if(Globals.unitTesting == true) {
            RunCommand { Thread.sleep(10) }.schedule()
        }

        while(!isStopRequested) {
            Logger.periodicBeforeUser()

            psiKit.logOncePerLoop(this)
            CommandScheduler.update()

            Logger.periodicAfterUser(0.0, 0.0)

        }
        CommandScheduler.end()
        psiKit.end()
    }
}
