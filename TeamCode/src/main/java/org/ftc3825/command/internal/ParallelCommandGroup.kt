package org.ftc3825.command.internal

class ParallelCommandGroup(private vararg var commandsInGroup: Command): Command() {
    var commands = unpack(commandsInGroup.asList())
    private var finished = BooleanArray(commands.size) { false }

    override fun initialize() {
        commands = unpack(commandsInGroup.asList())
        commands.forEach {command ->
            command.requirements.forEach { this.addRequirement(it) }
        }
        requirements = requirements.removeDuplicates()
        finished = BooleanArray(commands.size) { false }

        commands.forEach { it.initialize() }
    }
    override fun execute() {
        println(finished.joinToString(", "))
        println(finished.count { it == false })
        commands.indices.forEach { i -> if(!finished[i]) commands[i].execute() }
        commands.indices.forEach { i ->
            if(commands[i].isFinished()){
                finished[i] = true
                commands[i].end(false)
            }
        }
        println(commands.map { it.isFinished() })
        println(finished.joinToString(", "))
    }
    override fun isFinished() = finished.count { it == false } == 0
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

    override var description = { "{" + commands.joinToString() + "}" }
}
private fun <T> ArrayList<T>.removeDuplicates(): ArrayList<T>{
    val output = arrayListOf<T>()
    for (i in 0 until this.size) {
        if(this.indexOf(this[i]) == i){
            output.add(this[i])
        }
    }
    println(output)
    return output
}
