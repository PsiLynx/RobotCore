package org.ftc3825.util.graph

import org.ftc3825.util.Vector2D

class Field(val min: Vector2D, val max: Vector2D, val step: Int = 1){
    val width = (max.x - min.x).toInt() / step
    val height = (max.y - min.y).toInt() / step
    private val chars = Array(height) { Array(width) { '-' } }
    fun put(x: Int, y: Int, char: Char){
        val _x = (x - min.x).toInt() / step
        val _y = (y - min.y).toInt() / step
        //println("$_x, $_y")
        if(_x >= width - 1 || _y >= height - 1 || _x < 0 || _y < 0) return
        chars[_y][_x] = char
    }
    fun put(pos: Vector2D, char: Char) = put(pos.x.toInt(), pos.y.toInt(), char)
    fun print() = chars.forEach { line ->
        println(line.joinToString(separator = ""))
    }
}