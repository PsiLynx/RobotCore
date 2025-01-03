package org.ftc3825.util.graph

import org.ftc3825.util.Vector2D
import java.awt.*

class FieldPanel(val min: Vector2D, val max: Vector2D, val step: Int = 1) {
    val width = (max.x - min.x).toInt() / step
    val height = (max.y - min.y).toInt() / step

}