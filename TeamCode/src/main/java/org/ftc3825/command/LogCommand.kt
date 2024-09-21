package org.ftc3825.command

import org.ftc3825.command.internal.Command
import org.ftc3825.subsystem.Subsystem
import org.ftc3825.util.Globals
import org.ftc3825.util.Globals.robotVoltage
import org.ftc3825.util.json.JsonList
import org.ftc3825.util.json.JsonObject
import org.ftc3825.util.json.jsonObject
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.Date


class LogCommand(var subsystem: Subsystem<*>) : Command() {
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

        var path = "$startDate.json"
        if(Globals.state == Globals.State.Running) {
            path = "/sdcard/FIRST/userLogs/$startDate.json"
        }

        FileWriter(path, false).write(
            text.toCharArray()
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