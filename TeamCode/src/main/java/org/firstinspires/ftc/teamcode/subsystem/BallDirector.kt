package org.firstinspires.ftc.teamcode.subsystem

import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.util.log
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.component.AnalogDistanceSensor

import org.firstinspires.ftc.teamcode.util.Globals.BallColor.GREEN
import org.firstinspires.ftc.teamcode.util.Globals.BallColor.PURPLE
import org.firstinspires.ftc.teamcode.util.Globals.BallColor.UNKNOWN
import kotlin.math.abs

// Little buddy that tracks bigger time changes with distance sensor
object TimeLogger {
    // Entry class
    class TimeEntry(val timeSpan: Double, val startDist: Double, val endDist: Double)

    private const val MAX_TIME_FRAMES = 20 // Max number of time frames in list
    private const val MAX_DELTA_DIST  = 42.0 // Max distance in order for entry to be made

    private val timeEntries      = mutableListOf<TimeEntry>()
    private var timeSpan        = 0.0 // Used to track delta time
    private var startingDist    = 0.0 // Starting distance of sensor
    private var endingDist      = 0.0 // Ending distance of sensor

    fun updateLogger(deltaTime: Double, currentDistance: Double) {

        // Add an entry or update current values
        if (abs(startingDist - endingDist) > MAX_DELTA_DIST) {
            timeEntries.add(TimeEntry(timeSpan, startingDist, endingDist))

            timeSpan = 0.0 // Wipe values
            startingDist = currentDistance
        } else {
            timeSpan += deltaTime
            endingDist = currentDistance
        }

        // Limit our book's size
        if (timeEntries.size > MAX_TIME_FRAMES) timeEntries.removeAt(0)
    }

    // Create a package of entries; add entries using the desired number of entries/distance
    fun getPackage(numEntries: Int = 0, distance: Double = 0.0): Pair<Double, TimeEntry> {

        // Invalid; only one argument at a time
        if (numEntries > 0 && distance > 0.01) {
            return Pair(0.0, TimeEntry(0.0, 0.0, 0.0))
        }

        if (numEntries > 0 && numEntries <= 20) {
            val sD = timeEntries[timeEntries.size - numEntries - 1].startDist // startDist
            val eD = timeEntries[timeEntries.size - 1].endDist                // endDist

            var distTraveled = 0.0
            var tS = 0.0 // timespan

            // update these values :D
            for (i in timeEntries.size - numEntries - 1 .. timeEntries.size - 1) {
                distTraveled += timeEntries[i].endDist - timeEntries[i].startDist
                tS += timeEntries[i].timeSpan
            }

            return Pair(distTraveled, TimeEntry(tS, sD, eD))
        }

        if (distance > 0.0 && distance < MAX_DELTA_DIST * timeEntries.size) {
            // TODO: Complete this
            // I'm tired, boss
        }

        // Remaining values drain down to my friend the error output
        return Pair(0.0, TimeEntry(0.0, 0.0, 0.0))
    }
}


object BallDirector: Subsystem<BallDirector>() {

    override val components: List<Component>
        get() = TODO("Not yet implemented")

    const val BALL_DIAMETER = 42.0

    const val CONTACT_DIST = 42.0   // Closest possible distance between ball and sensor
    const val MAX_FACE_DIST = BALL_DIAMETER / 3.0
                                    // Maximum distance between the ball's "face" and the sensor;
                                    // If it's less than this, then the distance sensor skipped
                                    // over a hole (most likely)

    const val EPSILON = 0.01        // Hehe doubles fun



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
        trueValue = GREEN,
        falseValue = PURPLE
    )

    // List of balls hehe
    val balls: MutableList<Globals.BallColor> = mutableListOf()
    val canShift get() = topDistSensor.distance < CONTACT_DIST

    val shoot get() = false // abs(currentTopDist - prevTopDist) >= MAX_DELTA_DIST
    // TODO: figure this out :I
    // One way is to continuously monitor the top sensor distance
    // and then say the ball was shot if the distance between last cycle (i.e. update)
    // and the current cycle (i.e. update) exceeds a set threshold

    // Convert Ball Color to string
    fun strBallColor (c: Globals.BallColor): String { return if (c == GREEN) "Green"; else "Purple" }

    // Initialize dist pair variables (previous distance, current distance) with current distances
    var topDistSPair = Pair<Double, Double>(0.0, topDistSensor.distance)
    var bottomDistSPair = Pair<Double, Double>(0.0, lowDistSensor.distance)

    // Update and filter for face distances
    fun updateDistance(cD: Double, useTopSensor: Boolean): Pair<Double, Double> {
        // Get the true distance from the sensor
        val trueD = if (useTopSensor) topDistSensor.distance else lowDistSensor.distance

        val prevD = cD
        val currentD = if (trueD > MAX_FACE_DIST) cD else trueD // Use prev value if invalid

        return Pair(prevD, currentD)
    }

    override fun update(deltaTime: Double) {

        // Update distances
        topDistSPair = updateDistance(topDistSPair.second, true)
        bottomDistSPair = updateDistance(bottomDistSPair.second, false)

        // More fun values
        val deltaTDS = topDistSPair.second - topDistSPair.first
        val deltaBDS = bottomDistSPair.second - bottomDistSPair.first

        //TODO: use TimeLogger to recognize when balls are eaten and spitted out
/*
        // Update eating and spitting out balls (not done)
        if (bottomDistSPair.second < CONTACT_DIST) { // WE GOT A BALL

            val c = colorSensor.value // Easier to say
            // add the ball if:
            if (c != UNKNOWN && balls.isEmpty()) {
                balls.add(c)
            }
        } else if (lowDistSensor.distance > BALL_DIAMETER - 42.0) { // Add a small margin

        }

        // Get rid of the ball we shot
        if (shoot) {
            balls.removeAt(0)
        }
*/

        // Logs
        log("Top Sensor Distance") value topDistSensor.distance
        log("Bottom Sensor Distance") value lowDistSensor.distance

        log("Can Shift Head") value canShift
        // TODO: Log whether the turret can shoot the ball
        // I suspect this could be the same as can shift head
        log("Color of Ball in Chamber") value strBallColor(balls[0])
        // TODO: Log the new stuff
    }

}