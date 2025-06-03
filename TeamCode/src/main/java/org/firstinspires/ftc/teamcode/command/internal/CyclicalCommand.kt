package org.firstinspires.ftc.teamcode.command.internal

open class CyclicalCommand(vararg var commands: Command): Command() {
    var currentIndex = 0
        private set
    val current: Command
        get() = commands[currentIndex]

    fun nextCommand(): Command {
        return Command(
            initialize = {
                currentIndex = (currentIndex + 1) % commands.size
                current.initialize()
            },
            execute = { current.execute() },
            end = { interrupted -> current.end(interrupted) },
            isFinished = { current.isFinished() },
            requirements = (
                current.requirements
                //+ CyclicalSubsystem
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
        currentIndex = ( commands.size + currentIndex - 1 ) % commands.size
        current.initialize()
    }


}