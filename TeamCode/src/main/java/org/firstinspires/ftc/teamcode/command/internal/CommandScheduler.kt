package org.firstinspires.ftc.teamcode.command.internal

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.hardware.HWManager
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.logging.Logger
import org.firstinspires.ftc.teamcode.sim.SimulatedHardwareMap

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
        Logger.init()
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
        deltaTime = timer.getDeltaTime()
        timer.restart()
        HWManager.loopStartFun()

        if(hardwareMap is FakeHardwareMap){
            FakeHardwareMap.updateDevices()
            SimulatedHardwareMap.updateDevices()
        }

        updateTriggers()
        updateCommands(deltaTime)
        HWManager.loopEndFun()
        Logger.update()
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
