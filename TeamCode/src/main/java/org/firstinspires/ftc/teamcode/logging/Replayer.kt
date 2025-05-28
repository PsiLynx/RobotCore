package org.firstinspires.ftc.teamcode.logging

import java.io.File

object Replayer {
    var loop = 0
    val allData = arrayListOf<Moment>()

    fun loadData(file: String){
        var lines = File(file).readText().replace(" ", "").split("\n")
        // val revision = lines[0].split(";")[0].toDouble()
        lines = lines.slice(1..lines.size - 1)
        lines.forEach { line ->
            val dataPoints = mutableMapOf<String, Array<Double>>()
            var inputs = line.split(";")

            val time = inputs[0].toDouble()

            inputs = inputs.slice(1..inputs.size - 1)

            inputs.forEach { input ->
                val values = input.split(":")[1].split(",")
                dataPoints.put(
                    input.split(":")[0],
                    values.map { it.toDouble() }.toTypedArray()
                )
            }
            allData.add(Moment(time, dataPoints))
        }
    }

    val currentTime: Double
        get() = allData[loop].time
    fun valueForName(name: String) = allData[loop].data[name]!!

}