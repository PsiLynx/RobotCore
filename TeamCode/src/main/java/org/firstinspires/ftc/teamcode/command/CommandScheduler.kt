package org.firstinspires.ftc.teamcode.command

class CommandScheduler {
    var commands:ArrayList<Command> = arrayListOf<Command>()
    fun schedule(command: Command) {
        commands.add(command)
        command.initialize()

        for (requirement in command.getRequirements()){
            commands.filter { it.getRequirements().contains(requirement)}
                .forEach{
                    it.end(true)
                    commands.remove(it)
                }
        }
    }

    fun update() {
        for (it in commands) it.execute()
        var i = 0
        while ( i < commands.size){
            with(commands[i]) {
                if (this.isFinished()) {
                    this.end(false)
                    commands.remove(this)
                }
            }
            i ++
        }
    }
}