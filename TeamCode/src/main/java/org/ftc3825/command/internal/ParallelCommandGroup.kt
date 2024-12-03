package org.ftc3825.command.internal

class ParallelCommandGroup(vararg commandsInGroup: Command): Command() {
    var commands = unpack(commandsInGroup.asList())
    private var finished = commands.map { _ -> false }.toBooleanArray()

    init {
        commands.forEach {command ->
            command.requirements.forEach { this.addRequirement(it) }
        }
        requirements = requirements.removeDuplicates()
    }

    override fun initialize() { commands.forEach { it.initialize() } }
    override fun execute() {
        commands.indices.forEach { i -> if(!finished[i]) commands[i].execute() }
        commands.indices.forEach {
            i -> if(commands[i].isFinished()){
                finished[i] = true
                commands[i].end(false)
            }
        }
    }
    override fun isFinished() = finished.all { it == true }
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
    for (i in 0..<this.size) {
        if(this.indexOf(this[i]) == i){
            output.add(this[i])
        }
    }
    println(output)
    return output
}
