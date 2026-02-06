package org.firstinspires.ftc.teamcode.command.internal

import android.R.attr.value
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler.commands
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.subsystem.internal.SubsystemGroup
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.log
import kotlin.time.measureTimedValue
import org.psilynx.psikit.core.Logger
import org.psilynx.psikit.ftc.HardwareMapWrapper

object  CommandScheduler {
    lateinit var hardwareMap: HardwareMap

    var deltaTime = 0.0
    lateinit var timer: Timer

    var commands = arrayListOf<Command>()
        internal set
    private var triggers = arrayListOf<Trigger>()

    fun reset(){
        commands = arrayListOf()
        triggers = arrayListOf()
    }

    fun init(hardwareMap: HardwareMap, timer: Timer){
        this.hardwareMap = hardwareMap
        this.timer = timer
        reset()
    }

    fun addTrigger(trigger: Trigger) = triggers.add(trigger)

    fun schedule(command: Command) {
        println("scheduled $command")

        command.requirements.forEach { subsystem ->
            commands.filter { it.requirements.contains(subsystem) }
                .forEach{
                    it.end(interrupted = true)
                    commands.remove(it)
                }
        }

        command.requirements.forEach { it.enable() }
        command.initialize()
        commands.add(command)
    }

    private fun updateCommands(deltaTime: Double) {
        var i = 0
        while(i < commands.size){
            val command = commands[i]

            command.requirements.forEach { requirement ->
                requirement.components.forEach { it.update(deltaTime) }
                requirement.update(deltaTime)
                (requirement as? SubsystemGroup)?.subsystems?.forEach {
                    it.update(deltaTime)
                }
            }
            command.execute()
            
            if(command.isFinished()){
                commands.remove(command)
                command.end(interrupted = false)
            }
            else {
                i ++ 
            }
        }
    }
    private fun updateTriggers() {
        triggers.forEach {
            it.update()
            if (it.isTriggered) {
                schedule(it.command)
            }
        }
    }
    fun update() {
        deltaTime = timer.getDeltaTime()
        timer.restart()
        log("delta time") value deltaTime

        if(
            hardwareMap is FakeHardwareMap ||
            (
                hardwareMap is HardwareMapWrapper
                && (hardwareMap as HardwareMapWrapper)
                    .hardwareMap is FakeHardwareMap
            )
        ){
            FakeHardwareMap.updateDevices()
        }

        updateTriggers()
        updateCommands(deltaTime)

        commands.withIndex().forEach { (i, value) ->
            log("$i") value value
        }

        Globals.apply {
            log("time") value currentTime.toString()
            log("voltage") value robotVoltage
        }
    }

    fun end() {
        commands.forEach { it.end(true) }
        commands = arrayListOf()
    }

    fun end(command: Command){
        val toRemove = commands.firstOrNull { it == command }
        if( toRemove != null ){
            toRemove.end(true)
            commands.remove(toRemove)
            command.requirements.forEach { it.disable() }
        }
    }

    fun status(): String {
        var output = "running commands: [\n"
        commands.forEach { output += ( "\n" + it.toString() ).replace("\n", "\n\t") }
        output += "]"

        return output
    }

}
