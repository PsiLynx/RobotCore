package org.firstinspires.ftc.teamcode.subsystem

import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.util.log
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.component.AnalogDistanceSensor


object BallDirector: Subsystem<BallDirector>() {

    override val components: List<Component>
        get() = TODO("Not yet implemented")

    const val MAX_DIST_SHIFT = 42   // Max distance between ball and sensor
                                    // for the "black thing" to move
    const val DETECT_DIST = 42      // Max distance between ball and sensor
                                    // for the detector to be sure what it sees is a ball

    // Declare sensors
    val topDistSensor = HardwareMap.topSensor(
        minDist = 0.0,
        maxDist = 100.0,
        zeroVoltage = 0.0,
        maxVoltage = 3.2
    )

    val lowDistSensor = HardwareMap.bottomSensor(
        minDist = 0.0,
        maxDist = 100.0,
        zeroVoltage = 0.0,
        maxVoltage = 3.2
    )

    val colorSensor = HardwareMap.colorSensor(
        trueValue = Globals.BallColor.GREEN,
        falseValue = Globals.BallColor.PURPLE
    )

    // List of balls hehe
    val balls: MutableList<Globals.BallColor> = MutableListOf()

    val canShift get() = topDistSensor.distance < MAX_DIST_SHIFT
    val shoot get() = false // TODO: figure this out :I


    override fun update(deltaTime: Double) {
        // Logs
        log("Top Sensor Distance") value topDistSensor.distance
        log("Bottom Sensor Distance") value lowDistSensor.distance

        log("Can Shift Head") value canShift
        // TODO: Log whether the turret can shoot the ball
        log("Color of Ball in Chamber") value balls[0] // best name I have for the log

        // Update eating and spitting out balls (not done)
        if (lowDistSensor.distance < DETECT_DIST) { // WE GOT A BALL
            // TODO: Verify that this is ball and not hand (i.e., color valid)
            balls.add(colorSensor.value)
        }

        // Get rid of the ball we shot
        if (shoot) {
            balls.removeAt(0)
        }
    }

}