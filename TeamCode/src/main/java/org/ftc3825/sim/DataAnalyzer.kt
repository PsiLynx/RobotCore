package org.ftc3825.sim

import org.ftc3825.util.json.JsonList
import org.ftc3825.util.json.JsonObject
import org.ftc3825.util.json.tokenize
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path


object DataAnalyzer {
    var data = ""

    fun analyze() {
        if(data == ""){ loadConfigData() }
        val json = tokenize(data)

        val motorData = mutableHashMapOf<String, ArrayList<MotorDataPoint>>(
            (json["motors"] as JsonList<String>).map {
                Pair(it, ArrayList())
            }
        )

        val dataPoints = json["data"] as JsonList<JsonObject>

        for( i in 0..<dataPoints.size - 2 ){
            val moment = dataPoints[i]
            for( j in (moment["motors"] as JsonList<*>).indicies ){
                val motor = (moment["motors"] as JsonList<JsonObject>)[j]
                val dataPoint = MotorDataPoint(
                    motor["volt"].toString().toDouble(),
                    motor["vel"].toString().toDouble(),
                    (dataPoints[i + 1]["motors"] as JsonList<JsonObject>)[j]["acc"].toString()
                        .toDouble()
                )
                motorData[motor["name"] as String]!!.add(
                    dataPoint
                )

            }
        }

        for( motor in motorData){
            SimulatedHardwareMap.dcMotor.put(motor.key, SimulatedMotor())
        }
    }

    private fun <K, V> mutableHashMapOf(pairs: List<Pair<K, V>>): MutableMap<K, V> {
        val map = mutableMapOf<K, V>()

        for(elem in pairs){
            map[elem.first] = elem.second
        }
        return map
    }

    fun load(filePath: String){
        data = File(filePath).readText()
    }

    fun loadMostRecentLog(){
        val list = arrayListOf<File>()

        Files.list(Path("logs")).forEach { list.add(it.toFile()!!) }

        data = list.maxBy { it.lastModified() }.readText()
    }

    fun loadConfigData(){
        load("configData/data.json")
    }

    fun loadTestData(){
        load("src/test/java/test/logTestData.json")
    }
}