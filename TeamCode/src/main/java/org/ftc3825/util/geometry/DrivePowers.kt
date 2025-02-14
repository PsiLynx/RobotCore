package org.ftc3825.util.geometry

class DrivePowers(
    drive: Number = 0.0,
    strafe: Number = 0.0,
    turn: Number = 0.0
){
    val drive = drive.toDouble()
    val strafe = strafe.toDouble()
    val turn = turn.toDouble()
}