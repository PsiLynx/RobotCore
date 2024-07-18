package org.firstinspires.ftc.teamcode.command.internal

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.command.UpdateGlobalsCommand
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.sim.SimulatedHardwareMap
import org.firstinspires.ftc.teamcode.sim.SimulatedMotor
import org.firstinspires.ftc.teamcode.subsystem.Subsystem
import org.firstinspires.ftc.teamcode.util.Globals

object CommandScheduler {
    var startTime = 0.0
    var initialized = false

    lateinit var hardwareMap: HardwareMap

    var commands = arrayListOf<Command>()
    private var triggers = arrayListOf<Trigger>()
    private var subsystemsToUpdate = arrayListOf<Subsystem>()

    fun init(hardwareMap: HardwareMap){
        if(!initialized) {
            this.hardwareMap = hardwareMap
            schedule(UpdateGlobalsCommand())
        }
        initialized = true
    }

    fun addTrigger(trigger: Trigger) = triggers.add(trigger)

    fun schedule(command: Command) {
        command.initialize()

        for (requirement in command.requirements){
            requirement.init(hardwareMap)
            commands.filter { it.requirements.contains(requirement)}
                .forEach{
                    it.end(true)
                    commands.remove(it)
                }

            if ( !(requirement in subsystemsToUpdate) ){
                subsystemsToUpdate.add(requirement)
            }
        }

        command.readOnly.forEach {
            it.init(hardwareMap)
            if ( !(it in subsystemsToUpdate) ){
                subsystemsToUpdate.add(it)
            }
        }

        commands.add(command)
    }

    private fun updateCommands(deltaTime: Double) {
        subsystemsToUpdate.forEach {
            it.update(deltaTime)
        }
        var i = 0
        while(i < commands.size){
            val command = commands[i]


            command.execute()
            
            if(command.isFinished()){
                command.end(interrupted = false)
                commands.remove(command)
            }
            else{ i ++ }

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
        if(startTime == 0.0){ startTime = Globals.timeSinceStart }

        val deltaTime = Globals.timeSinceStart - startTime

        if(hardwareMap is FakeHardwareMap){
            (hardwareMap as FakeHardwareMap).updateDevices()
            SimulatedHardwareMap.updateDevices()
        }

        updateTriggers()
        updateCommands(deltaTime)
    }

    fun end() {
        commands.forEach { it.end(true) }
    }

}