package org.ftc3825.util.pid

import android.health.connect.datatypes.units.Velocity

fun pdControl(
    error: Double,
    velocity: Double,
    p: Double,
    d: Double,
) = ( p * error - d * velocity).coerceIn(-1.0, 1.0)