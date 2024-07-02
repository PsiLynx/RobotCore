package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.subsystem.Subsystem

open class Command {
    private var requirements:ArrayList<Subsystem> = arrayListOf<Subsystem>()

    fun addReqirements(vararg requirements: Subsystem) {
        for(i in this.requirements){
            this.requirements.add(i)
        }
    }

    fun getRequirements(): ArrayList<Subsystem> {
        return requirements
    }

    open fun initialize(){}
    open fun execute(){}
    open fun end(interupted:Boolean){}
    open fun isFinished() = false
}