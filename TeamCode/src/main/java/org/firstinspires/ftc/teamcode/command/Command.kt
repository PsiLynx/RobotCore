package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.subsystem.Subsystem

open class Command {
    var requirements:ArrayList<Subsystem> = arrayListOf()
    var readOnly:ArrayList<Subsystem> = arrayListOf()

    fun addReqirement(requirement: Subsystem, write: Boolean=true) {
        if(write){
            this.requirements.add(requirement)
        }
        else{
            this.readOnly.add(requirement)
        }
    }

    open fun initialize(){}
    open fun execute(){}
    open fun end(interrupted:Boolean){}
    open fun isFinished() = false
}