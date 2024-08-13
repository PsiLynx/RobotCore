package org.ftc3825.sim

import org.ftc3825.json.JsonList
import org.ftc3825.json.JsonObject
import org.ftc3825.json.tokenize
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

        for(i in 0..<dataPoints.size - 1 ){
            val moment = dataPoints[i]
            for(j in 0..<(moment["motors"] as JsonList<*>).size){
                val motor = (moment["motors"] as JsonList<JsonObject>)[j]
                motorData[motor["name"] as String]!!.add(
                    MotorDataPoint(
                        motor["volt"].toString().toDouble(),
                        motor["vel"   ].toString().toDouble(),
                        (dataPoints[i + 1]["motors"] as JsonList<JsonObject>)[j]["acc"].toString().toDouble()
                    )
                )
            }
        }

        for( motor in motorData){
            SimulatedHardwareMap.dcMotor.put(motor.key, SimulatedMotor(motor.value))
        }
    }

    private fun <K, V> mutableHashMapOf(pairs: List<Pair<K, V>>): MutableMap<K, V> {
        val hash = mutableMapOf<K, V>()

        for(elem in pairs){
            hash[elem.first] = elem.second
        }
        return hash
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
        load("src/test/java/logTestData.json")
    }
}