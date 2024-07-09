package org.firstinspires.ftc.teamcode.command

import com.qualcomm.robotcore.hardware.HardwareMap

class CommandScheduler(val hardwareMap: HardwareMap) {
    var commands:ArrayList<Command> = arrayListOf()
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
        commands.map{
            it.requirements.map { req -> req.update() }
            it.readOnly.map { req -> req.update() }
            it.execute()
        }
        var i = 0
        while ( i < commands.size){
            with(commands[i]) {
                if (this.isFinished()) {
                    this.end(false)
                    commands.remove(this)
                    i --
                }
            }
            i ++
        }
    }

    fun end() {
        commands.map { it.end(false) }
    }
}