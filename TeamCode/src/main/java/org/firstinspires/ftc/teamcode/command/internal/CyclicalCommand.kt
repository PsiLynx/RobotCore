package org.firstinspires.ftc.teamcode.command.internal

open class CyclicalCommand(vararg var commands: Command) {
    var currentIndex = 0
        set(value){
            field = (
                value % commands.size + commands.size
            ) % commands.size
        }

    val current: Command
        get() = commands[currentIndex]

    fun nextCommand(): Command {
        return Command(
            initialize = {
                currentIndex += 1
                current.initialize()
            },
            execute = { current.execute() },
            end = { interrupted -> current.end(interrupted) },
            isFinished = { current.isFinished() },
            requirements = (
                commands.map{it.requirements}.flatten().toMutableSet()
            ).toMutableSet(),
            name = { current.name() },
            description = {
                (
                    current.description()
                    + "|"
                    + current.requirements.joinToString { "$it, " }
                )
            }
        )
    }
    fun lastCommand() = nextCommand() withInit {
        currentIndex --
        current.initialize()
    }


}