package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.subsystem.Subsystem
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.Globals.robotVoltage
import org.firstinspires.ftc.teamcode.util.json.JsonList
import org.firstinspires.ftc.teamcode.util.json.JsonObject
import org.firstinspires.ftc.teamcode.util.json.jsonObject
import java.io.FileWriter
import java.util.Date


class LogCommand(var subsystem: Subsystem<*>) : Command() {
    override val requirements = mutableSetOf(subsystem)

    private var startDate = Date().toString()
    private var log = JsonList<JsonObject>(arrayListOf())

    override fun initialize() {
        startDate = Date().toString()
        log = JsonList<JsonObject>(arrayListOf())

    }

    override fun execute() {
        log.add( jsonObject {
            "sec" `is` CommandScheduler.deltaTime
            "volts" `is` robotVoltage
            "components" `is` JsonList(subsystem.motors.map {
                jsonObject {
                    "name" `is` it.name
                    "volt" `is` (it.lastWrite or 0.0) * robotVoltage
                    "position" `is` it.ticks
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
            //"components"     `is` JsonList(subsystem.components.map { it.name })
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
 *      first version, logs start time, version, data: [ seconds, voltage, components: [{ name, voltage, position }] ]
 *
 * 0.0.2:
 *      update components to be components: [{name, voltage, position, velocity, acceleration}]
 *
 * 0.0.2a:
 *      add components field in root object containing names of all the components being tracked
 *
 * 0.0.2b:
 *      shorten variable names in array to {s, v, m, [{n, volt, p, s, v}]
 */
