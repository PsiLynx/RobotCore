package org.ftc3825.command.internal

import com.qualcomm.robotcore.hardware.HardwareMap
import org.ftc3825.command.UpdateGlobalsCommand
import org.ftc3825.fakehardware.FakeHardwareMap
import org.ftc3825.sim.SimulatedHardwareMap
import org.ftc3825.util.Globals

object  CommandScheduler {
    private var lastTime = 0.0
    var deltaTime = 0.0

    lateinit var hardwareMap: HardwareMap

    var commands = arrayListOf<Command>()
        internal set
    private var triggers = arrayListOf<Trigger>()

    fun reset(){
        commands = arrayListOf(UpdateGlobalsCommand())
        triggers = arrayListOf()

        deltaTime = 0.0
        lastTime = Globals.timeSinceStart
    }

    fun init(hardwareMap: HardwareMap){
        this.hardwareMap = hardwareMap
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
            }
            command.execute()
            
            if(command.isFinished()){
                command.end(interrupted = false)
                commands.remove(command)
//                command.requirements.forEach {
//                    it.justUpdate().schedule()
//                }
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
        deltaTime = Globals.timeSinceStart - lastTime

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
        commands = arrayListOf()
    }

    fun end(command: Command){
        val toRemove = commands.firstOrNull { it == command }
        if(toRemove != null ){
            toRemove.end(true)
            commands.remove(toRemove)
        }
    }

    fun status(): String {
        var output = "running commands: [\n"
        commands.forEach { output += ( "\n" + it.toString() ).replace("\n", "\n\t") }
        output += "\n]\ntriggers: ["
        triggers.forEach { output += "$it, " }
        output += "]\ntime: ${Globals.timeSinceStart}"

        return output
    }

}
