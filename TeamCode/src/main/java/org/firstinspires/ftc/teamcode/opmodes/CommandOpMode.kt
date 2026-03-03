package org.firstinspires.ftc.teamcode.opmodes


import android.R.attr.value
import com.qualcomm.hardware.lynx.LynxModule.BulkCachingMode.MANUAL
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.firstinspires.ftc.teamcode.shooter.CompTargets
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.command.internal.Timer
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.component.controller.Gamepad
import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.sim.FakeTimer
import org.firstinspires.ftc.teamcode.sim.SimulatedArtifact
import org.firstinspires.ftc.teamcode.subsystem.LEDs
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.SelectorInput
import org.firstinspires.ftc.teamcode.util.log
import org.psilynx.psikit.core.Logger
import org.psilynx.psikit.core.rlog.RLOGServer
import org.psilynx.psikit.core.rlog.RLOGWriter
import org.psilynx.psikit.ftc.OpModeControls
import org.psilynx.psikit.ftc.PsiKitLinearOpMode

//@Disabled
abstract class CommandOpMode : PsiKitLinearOpMode() {

    lateinit var driver : Gamepad
    lateinit var operator : Gamepad

    var afterResetHooks = mutableListOf<CommandOpMode.() -> Unit>()
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
        //psiKitSetup()
        allHubs = this.hardwareMap.getAll(com.qualcomm.hardware.lynx.LynxModule::class.java)

        allHubs.forEach {
            it.bulkCachingMode = MANUAL
        }
        println("psikit setup")

        HardwareMap.init(hardwareMap)
        CommandScheduler.init(hardwareMap, Timer())

        afterResetHooks.forEach {
            it.invoke(this)
        }

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
                Vector3D(-CompTargets.compGoalPos().y, CompTargets.compGoalPos().x, CompTargets.compGoalPos().z) } }.schedule()

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

            updateSelector(currentSelector)

            Logger.periodicAfterUser(0.0, 0.0)
        }

        postSelector()
        if(Globals.unitTesting == true) {
            RunCommand { Thread.sleep(10) }.schedule()
        }

        while(
            !isStopRequested
            || (
                Globals.unitTesting
                && FakeTimer.time > (
                    if(
                        this::class.annotations.firstOrNull{
                            it is Autonomous
                        } != null
                    ){
                        30.0
                    } else 120.0
                )
            )
        ) {
            Logger.periodicBeforeUser()

            allHubs.forEach { it.clearBulkCache() }

            CommandScheduler.update()

            if(Globals.unitTesting){
                SimulatedArtifact.allArtifacts.map { it }.withIndex().forEach {
                    it.value.update(CommandScheduler.deltaTime)
                    log("artifacts/${it.index}") value it.value.pos
                    /*
                    log("artifact hist/${it.index}") value (
                        it.value.poseHist.toTypedArray()
                    )
                     */
                }
            }

            Logger.periodicAfterUser(0.0, 0.0)
        }
        CommandScheduler.end()
        OpModeControls.started = false
        OpModeControls.stopped = false
    }

    private fun updateSelector(currentSelector: Int) {
        var currentSelector1 = currentSelector
        val current = SelectorInput.allSelectorInputs[currentSelector1]
        this.telemetry.addData(
            current.name,
            current.get()
        )
        this.telemetry.update()

        driver.dpadLeft.onTrue(InstantCommand {
            current.moveLeft()
        })

        driver.dpadRight.onTrue(InstantCommand {
            current.moveRight()
        })

        driver.dpadUp.onTrue(InstantCommand {
            currentSelector1--
            if (currentSelector1 < 0) currentSelector1 = 0
        })

        driver.dpadDown.onTrue(InstantCommand {
            currentSelector1++
            if (
                currentSelector1
                >= SelectorInput.allSelectorInputs.size
            ) currentSelector1 = 0
        })
    }
}
