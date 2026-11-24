package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.hardware.lynx.LynxModule.BulkCachingMode.MANUAL
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.command.internal.Timer
import org.firstinspires.ftc.teamcode.component.controller.Gamepad
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.sim.FakeTimer
import org.firstinspires.ftc.teamcode.sim.SimulatedPollen
import org.firstinspires.ftc.teamcode.subsystem.telemetry.Telemetry
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.SelectorInput
import org.firstinspires.ftc.teamcode.util.log
import org.psilynx.psikit.core.Logger
import org.psilynx.psikit.ftc.FtcLoggingSession
import org.psilynx.psikit.ftc.OpModeControls
import kotlin.jvm.java

//@Disabled
abstract class CommandOpMode: LinearOpMode() {

    lateinit var driver : Gamepad
    lateinit var operator : Gamepad

    lateinit var allHubs : List<LynxModule>
    val psiKit = FtcLoggingSession()

    var timer = Timer()

    var afterResetHooks = mutableListOf<CommandOpMode.() -> Unit>()
    var afterLoopHooks = mutableListOf<CommandOpMode.() -> Unit>()
    /**
     * preSelector should initialize any objects that delegate parameters to
     * SelectInput
     */
    open fun preSelector() {
        Globals
    }
    /**
     * postSelector can assume that anything initialized to SelectInput is ready
     */
    abstract fun postSelector()

    final override fun runOpMode() {
        HardwareMap.init(hardwareMap)

        Logger.recordMetadata("alliance", "red")
        psiKit.start(this, 5800, writeFile = false)

        afterResetHooks.forEach { it.invoke(this) }


        allHubs = this.hardwareMap.getAll(LynxModule::class.java)
        allHubs.forEach { it.bulkCachingMode = MANUAL }

        CommandScheduler.init(hardwareMap, timer)

        Telemetry.reset()
        Telemetry.initialize(telemetry)
//        LEDs.justUpdate().schedule()

        val voltageSensor = hardwareMap.get(
            VoltageSensor::class.java,
            "Control Hub"
        )
        Globals.robotVoltage = voltageSensor.voltage


        driver = Gamepad(gamepad1!!)
        operator = Gamepad(gamepad2!!)

        Logger.periodicBeforeUser()
        psiKit.logOncePerLoop(this)
        Logger.periodicAfterUser(0.0, 0.0)
        preSelector()

        var currentSelector = 0
        while (!isStarted && Globals.unitTesting == false){
            CommandScheduler.update()
            Logger.periodicBeforeUser()
            psiKit.logOncePerLoop(this)

            updateSelector(currentSelector)

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
//        if(Globals.unitTesting == true) {
//            RunCommand { Thread.sleep(10) }.withName("some sleep bs").schedule()
//        }

        while(
            !isStopRequested
            || (
                Globals.unitTesting
                && FakeTimer.time > (
                    if(
                        this::class.annotations.firstOrNull{
                            it is Autonomous
                        } != null
                    ) 30.0
                    else 120.0
                )
            )
        ) {
            Logger.periodicBeforeUser()

            psiKit.logOncePerLoop(this)
            CommandScheduler.update()
            Telemetry.update()

            afterLoopHooks.forEach {
                it.invoke(this)
            }
            Logger.periodicAfterUser(0.0, 0.0)
        }
        CommandScheduler.end()
        psiKit.end()
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
