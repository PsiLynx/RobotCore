package org.firstinspires.ftc.teamcode.sim

import org.firstinspires.ftc.teamcode.util.json.JsonList
import org.firstinspires.ftc.teamcode.util.json.JsonObject
import org.firstinspires.ftc.teamcode.util.json.tokenize
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path


object DataAnalyzer {
    var data = ""

    var motors = mutableMapOf<String, ArrayList<MotorDataPoint>>()

    fun analyze(): MutableMap<String, ArrayList<MotorDataPoint>> {
        if(data == ""){ loadConfigData() }
        val json = tokenize(data)

        motors = mutableHashMapOf(
            (json["motors"] as JsonList<String>).map {
                Pair(it, ArrayList())
            }
        )

        for(i in (json["data"] as JsonList<JsonObject>).indices){
            val moment = json["data"] as JsonList<JsonObject>
            for( motor in moment["m"] as JsonList<JsonObject>){
                motors[motor["n"]]!!.add(
                    MotorDataPoint(
                        motor["volt"].toString().toDouble(),
                        motor["v"   ].toString().toDouble(),
                        motor["a"   ].toString().toDouble()
                    )
                )
            }
        }
        return motors
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