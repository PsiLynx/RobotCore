package org.firstinspires.ftc.teamcode.util

class CacheValue<T>(val value: T, val creationTime: Double) {
    constructor(value: T): this(value, Globals.currentTime)

    fun age() = Globals.currentTime - creationTime
}