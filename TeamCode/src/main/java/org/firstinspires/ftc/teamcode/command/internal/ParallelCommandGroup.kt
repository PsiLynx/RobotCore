package org.firstinspires.ftc.teamcode.command.internal

class ParallelCommandGroup(private vararg var commandsInGroup: Command): Command() {
    var commands = unpack(commandsInGroup.asList())
    private var finished = BooleanArray(commands.size) { false }

    override var requirements = commands.flatMap {
        it.requirements
    }.toMutableSet()

    override fun initialize() {
        finished = BooleanArray(commands.size) { false }

        commands.forEach { it.initialize() }
    }
    override fun execute() {
        commands.indices.forEach { i ->
            if(!finished[i]) commands[i].execute()

            if(commands[i].isFinished()){
                finished[i] = true
                commands[i].end(false)
            }
        }
    }
    override fun isFinished() = commands.all { it.isFinished() }
    override fun end(interrupted: Boolean) = commands.forEach { it.end(interrupted) }

    private fun unpack(commands: List<Command>): Array<out Command> {
        val output = arrayListOf<Command>()

        commands.forEach {
            if (it is ParallelCommandGroup) {
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

    override var name = { "Parallel command" }
    override var description = { "{" + commands.joinToString() + "}" }
}
