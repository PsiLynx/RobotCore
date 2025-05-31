package org.firstinspires.ftc.teamcode.logging

import org.firstinspires.ftc.teamcode.util.Globals
import java.io.File
import java.util.Date
import kotlin.math.floor

object Logger {
    const val rootPath = "/sdcard/FIRST/replayLogs/"
    const val linesToBuffer = 3
    var buffer = arrayListOf<Moment>()
    lateinit var fileName: String
    lateinit var file: File

    val inputs = arrayListOf<Input>()

    fun init(fileName: String = Date().toString()){
        this.fileName = fileName
        file = File("$rootPath$fileName.replayLog")
        if(Globals.running) {
            file.createNewFile()
            writeData(arrayListOf("FTC3825LOG", "0.0.0"))
        }
    }

    fun update(){
        buffer.add(Moment(
            Globals.currentTime,
            inputs.associate { it.uniqueName to it.getRealValue() }
        ))
        if(buffer.size >= linesToBuffer){
            buffer.forEach { line ->
                writeData(
                    listOf(
                        line.time.toString(),
                    ) +
                    line.data.map {
                        (
                            it.key
                            + ":"
                            + it.value.map {
                                floor( it * 1e6) / 1e6
                            }.joinToString(",")
                        )
                    }
                )
            }
            buffer = arrayListOf()
        }
    }

    fun add(input: Input){
        inputs.add(input)
    }
    fun writeData(data: List<String>) = if(Globals.running) file.appendText(
        data.joinToString(";") + "\n"
    ) else Unit
}