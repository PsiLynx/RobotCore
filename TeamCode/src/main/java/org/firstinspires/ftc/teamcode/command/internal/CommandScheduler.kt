package org.firstinspires.ftc.teamcode.command.internal

import com.qualcomm.robotcore.hardware.HardwareMap

object CommandScheduler {
    lateinit var hardwareMap: HardwareMap

    fun init(hardwareMap: HardwareMap){ this.hardwareMap = hardwareMap }

    var commands = arrayListOf<Command>()
    var triggers = arrayListOf<Trigger>()

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

    fun update(deltaTime: Double) {
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