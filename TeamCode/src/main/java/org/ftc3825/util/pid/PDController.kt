package org.ftc3825.util.pid

import org.ftc3825.util.geometry.Rotation2D
import org.ftc3825.util.geometry.Vector2D

fun pdControl(
    error: Double,
    velocity: Double,
    p: Double,
    d: Double,
) = ( p * error - d * velocity).coerceIn(-1.0, 1.0)
fun pdControl(
    error: Vector2D,
    velocity: Vector2D,
    p: Double,
    d: Double,
) = ( error * p - velocity * d).unit

fun pdControl(
    error: Rotation2D,
    velocity: Rotation2D,
    p: Double,
    d: Double,
) = ( error * p - velocity * d).coerceIn(-1.0, 1.0)
