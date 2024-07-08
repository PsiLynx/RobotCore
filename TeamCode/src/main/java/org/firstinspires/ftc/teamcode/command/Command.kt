package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.subsystem.Subsystem

open class Command {
    private var requirements:ArrayList<Subsystem> = arrayListOf()
    private var readOnly:ArrayList<Subsystem> = arrayListOf()

    fun addReqirement(requirement: Subsystem, write: Boolean=true) {
        if(write){
            this.requirements.add(requirement)
        }
        else{
            this.readOnly.add(requirement)
        }
    }

    fun getRequirements(): ArrayList<Subsystem> {
        return requirements
    }

    open fun initialize(){}
    open fun execute(){}
    open fun end(interrupted:Boolean){}
    open fun isFinished() = false
}