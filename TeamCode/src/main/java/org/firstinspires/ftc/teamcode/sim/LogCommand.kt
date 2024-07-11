package org.firstinspires.ftc.teamcode.sim

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Robot
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.json.JsonList
import org.firstinspires.ftc.teamcode.util.json.JsonObject
import org.firstinspires.ftc.teamcode.util.json.jsonObject
import org.firstinspires.ftc.teamcode.util.nanoseconds
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.Date


class LogCommand(): Command() {
    val startDate = Date().toString()
    val startTime = Globals.timeSinceStart
    val log = JsonList<JsonObject>(arrayListOf())
    init {
        addRequirement(Drivetrain, write=false)
        addRequirement(Robot, write=false)
    }

    override fun execute() {
        log.add(
            jsonObject {
                "seconds" `is` Globals.timeSinceStart - startTime
                "voltage" `is` Robot.voltage
                "motors" `is` JsonList<JsonObject>(Drivetrain.motors.map {
                    jsonObject {
                        "name" `is` it.name
                        "voltage" `is` it.lastWrite * Robot.voltage
                        "position" `is` it.position
                        "velocity" `is` it.velocity
                        "acceleration" `is` it.acceleration
                    }
                })
            }
        )
    }

    override fun end(interrupted: Boolean) {

        var text: String = jsonObject {
            "start time" `is` startDate
            "version" `is` "0.0.2a"
            "motors" `is` JsonList(Drivetrain.motors.map { it.name })
            "data" `is` log

        }.toString()

        var path = Paths.get("logs/$startDate.json")

        Files.write(path, text.toByteArray(), StandardOpenOption.CREATE)
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
 */