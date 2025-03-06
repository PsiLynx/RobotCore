package org.firstinspires.ftc.teamcode.util.graph

import org.firstinspires.ftc.teamcode.util.geometry.Vector2D

class FieldPanel(val min: Vector2D, val max: Vector2D, val step: Int = 1) {
    val width = (max.x - min.x).toInt() / step
    val height = (max.y - min.y).toInt() / step

}