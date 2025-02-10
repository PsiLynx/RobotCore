package org.ftc3825.command.internal

open class CommandGroup(vararg commandsInGroup: Command): Command() {
    var commands = unpack(commandsInGroup.asList())

    init {
        commands.forEach {command ->
            command.requirements.forEach { this.addRequirement(it) }
        }
    }

    private var index = 0
    private val current: Command
        get() =
            if(!isFinished()) commands[index]
            else Command() //overflow safety

    override fun initialize() {
        commands[0].initialize()
        index = 0
    }
    override fun execute() {
        current.execute()
        println(current.isFinished())
        println(index)
        if(current.isFinished()){
            current.end(false)

            index ++
            if(index < commands.size){
                current.initialize()
            }
        }
    }
    override fun isFinished() = index >= commands.size
    override fun end(interrupted: Boolean){
        current.end(interrupted)
    }

    private fun unpack(commands: List<Command>): Array<out Command> {
        val output = arrayListOf<Command>()

        commands.forEach {
            if (it is CommandGroup) {
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

    override var description = {
        var output = "{\n"
        commands.forEach {
            output += if (commands.indexOf(it) == index) {
                it.toString().uppercase() + ", "
            } else {
                "$it, "
            }
        }
        "$output}"
    }
}
