package org.firstinspires.ftc.teamcode.command.internal

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.command.UpdateGlobalsCommand
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.util.Globals

object CommandScheduler {
    lateinit var hardwareMap: HardwareMap
    var initialized = false

    fun init(hardwareMap: HardwareMap){
        if(!initialized) {
            this.hardwareMap = hardwareMap
            schedule(UpdateGlobalsCommand())
        }
        initialized = true
    }

    var commands = arrayListOf<Command>()
    var triggers = arrayListOf<Trigger>()

    var startTime = 0.0

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
        }

        command.readOnly.map { it.init(hardwareMap)}

        commands.add(command)
    }

    fun update() {
        if(startTime == 0.0){
            startTime = Globals.timeSinceStart
        }
        val deltaTime = Globals.timeSinceStart - startTime

        if(hardwareMap is FakeHardwareMap){
            (hardwareMap as FakeHardwareMap).updateDevices()
        }

        triggers.map {
            it.update()
            if(it.triggered){
                schedule(it.command)
            }
        }
        commands.map{
            it.requirements.map { req -> req.update(deltaTime) }
            it.readOnly.map { req -> req.update(deltaTime) }
            it.execute()
        }
        var i = 0
        while ( i < commands.size){
            with(commands[i]) {
                if (this.isFinished()) {
                    this.end(false)
                    commands.remove(this)

                }
                else{
                    i ++
                }
            }
        }
    }

    fun end() {
        commands.map { it.end(true) }
    }
}