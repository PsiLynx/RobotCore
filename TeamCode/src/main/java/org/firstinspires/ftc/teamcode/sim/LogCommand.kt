package org.firstinspires.ftc.teamcode.sim

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Robot
import org.firstinspires.ftc.teamcode.subsystem.Subsystem
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.Globals.robotVoltage
import org.firstinspires.ftc.teamcode.util.json.JsonList
import org.firstinspires.ftc.teamcode.util.json.JsonObject
import org.firstinspires.ftc.teamcode.util.json.jsonObject
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.Date


class LogCommand(var subsystem: Subsystem) : Command() {
    private val startDate = Date().toString()
    private val startTime = Globals.timeSinceStart
    private val log = JsonList<JsonObject>(arrayListOf())
    init {
        addRequirement(subsystem, write=false)
    }

    override fun execute() {
        log.add( jsonObject {
            "sec" `is` Globals.timeSinceStart - startTime
            "volts" `is` robotVoltage
            "motors" `is` JsonList(subsystem.motors.map {
                jsonObject {
                    "name" `is` it.name
                    "volt" `is` it.lastWrite * robotVoltage
                    "pos" `is` it.position
                    "vel" `is` it.velocity
                    "acc" `is` it.acceleration
                }
            })
        } )
    }

    override fun end(interrupted: Boolean) {

        val text: String = jsonObject {
            "start time" `is` startDate
            "version"    `is` "0.0.2b"
            "motors"     `is` JsonList(subsystem.motors.map { it.name })
            "data"       `is` log

        }.toString()

        val path = Paths.get("logs/$startDate.json")

        Files.write(
            path,
            text.toByteArray(),
            StandardOpenOption.CREATE
        )
    }
}
/**
 * version log:
 * 0.0.1:
 *      first version, logs start time, version, data: [ seconds, voltage, motors: [{ name, voltage, position }] ]
 *
 * 0.0.2:
 *      update motors to be motors: [{name, voltage, position, velocity, acceleration}]
 *
 * 0.0.2a:
 *      add motors field in root object containing names of all the motors being tracked
 *
 * 0.0.2b:
 *      shorten variable names in array to {s, v, m, [{n, volt, p, s, v}]
 */