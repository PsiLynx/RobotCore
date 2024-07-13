package org.firstinspires.ftc.teamcode.sim

import org.firstinspires.ftc.teamcode.util.json.JsonList
import org.firstinspires.ftc.teamcode.util.json.JsonObject
import org.firstinspires.ftc.teamcode.util.json.tokenize
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path


class DataAnalyzer {
    var data = ""

    fun analyze(){
        if(data == ""){ loadConfigData() }
        val json = tokenize(data)

        val motors = mutableHashMapOf(
            (json["motors"] as JsonList<String>).map {
                Pair(it, ArrayList<MotorDataPoint>())
            }
        )

        for(moment in json["data"] as JsonList<JsonObject>){
            for( motor in moment["motors"] as JsonList<JsonObject>){
                motors[motor["name"]]!!.add(
                    MotorDataPoint(
                        motor["voltage"     ].toString().toDouble(),
                        motor["velocity"    ].toString().toDouble(),
                        motor["acceleration"].toString().toDouble()
                    )
                )
            }
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
}