package org.firstinspires.ftc.teamcode.logging

import java.util.Date

object Logger {
    val rootPath = "/sdcard/FIRST/logs/"
    lateinit var fileName: String

    val inputs = arrayListOf<Input<*>>()

    fun init(fileName: String = Date().toString()){
        this.fileName = fileName
    }

    fun add(input: Input<*>){
        inputs.add(input)
    }
}