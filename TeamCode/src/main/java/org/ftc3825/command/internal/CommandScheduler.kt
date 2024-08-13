package org.ftc3825.command.internal

import com.qualcomm.robotcore.hardware.HardwareMap
import org.ftc3825.command.UpdateGlobalsCommand
import org.ftc3825.fakehardware.FakeHardwareMap
import org.ftc3825.sim.SimulatedHardwareMap
import org.ftc3825.subsystem.Subsystem
import org.ftc3825.util.Globals

object CommandScheduler {
    var lastTime = 0.0
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
        val deltaTime = Globals.timeSinceStart - lastTime

        if(hardwareMap is FakeHardwareMap){
            FakeHardwareMap.updateDevices()
            SimulatedHardwareMap.updateDevices()
        }

        updateTriggers()
        lastTime = Globals.timeSinceStart
        updateCommands(deltaTime)


    }

    fun end() {
        commands.forEach { it.end(true) }
    }

}