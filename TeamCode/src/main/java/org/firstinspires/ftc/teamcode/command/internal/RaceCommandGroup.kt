package org.firstinspires.ftc.teamcode.command.internal

open class RaceCommandGroup(vararg commandsInGroup: Command): Command() {
    var commands = unpack(commandsInGroup.asList())

    override var requirements = commands.flatMap {
        it.requirements
    }.toMutableSet()

    override fun initialize() {
        commands.forEach { it.initialize() }
    }
    override fun execute() {
        commands.forEach { it.execute() }
    }
    override fun isFinished() =
        (commands.map { it.isFinished() }.indexOf(true) > -1)

    override fun end(interrupted: Boolean){
        commands.forEach { it.end(interrupted) }
    }

    private fun unpack(commands: List<Command>): Array<out Command> {
        val output = arrayListOf<Command>()

        commands.forEach {
            if (it is RaceCommandGroup) {
                output.addAll(
                    unpack(
                        it.commands.asList()
                    )
                )
            } else {
                output.add(it)
            }
        }

        return Array(
            size = output.size,
            init = { i -> output[i] }
        )
    }

    override var name = { "Race:" }
    override var description = {
        "{" + commands.joinToString() + "}"
    }
}
