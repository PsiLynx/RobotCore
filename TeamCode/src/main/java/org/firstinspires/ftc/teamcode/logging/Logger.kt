package org.firstinspires.ftc.teamcode.logging

import org.firstinspires.ftc.teamcode.util.Globals
import java.io.File
import java.util.Date

object Logger {
    const val rootPath = "/sdcard/FIRST/logs/"
    const val linesToBuffer = 3
    var buffer = arrayListOf<Moment>()
    lateinit var fileName: String
    lateinit var file: File

    val inputs = arrayListOf<Input>()

    fun init(fileName: String = Date().toString()){
        this.fileName = fileName
        file = File("$rootPath$fileName.replayLog")
        file.createNewFile()
        writeData(arrayListOf("FTC3825LOG", "0.0.0"))
    }

    fun update(){
        buffer.add(Moment(
            Globals.currentTime,
            inputs.associate { it.uniqueName to it.getRealValue() }
        ))
        if(buffer.size >= linesToBuffer){
            buffer.forEach { line ->
                writeData(
                    listOf(line.time.toString())
                    + ";"
                    + line.data.map {
                        it.key + ":" + it.value.joinToString(",") + ";"
                    }
                )
            }
            buffer = arrayListOf()
        }
    }

    fun add(input: Input){
        inputs.add(input)
    }
    fun writeData(data: List<String>) = file.appendText(
        data.joinToString(";") + "\n"
    )
}