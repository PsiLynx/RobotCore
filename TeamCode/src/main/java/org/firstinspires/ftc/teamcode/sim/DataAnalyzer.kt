package org.firstinspires.ftc.teamcode.sim

import org.firstinspires.ftc.teamcode.util.json.JsonList
import org.firstinspires.ftc.teamcode.util.json.JsonObject
import org.firstinspires.ftc.teamcode.util.json.tokenize
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.readText


class DataAnalyzer {
    var data = ""

    fun analyze(){
        if(data == ""){ loadConfigData() }
        var json = tokenize(data)
        for(moment in json.data["data"] as JsonList<JsonObject>){
            for( motor in moment.data["motors"] as JsonList<JsonObject>){

            }
        }
    }

    fun load(filePath: String){
        data = File(filePath).readText()
    }

    fun loadMostRecentLog(){
        var list = Files.list(Path("logs")).toList()
        data = list.maxBy {
            it.toFile().lastModified()
        }.readText()
        //load()
    }

    fun loadConfigData(){
        load("configData/data.json")
    }
}