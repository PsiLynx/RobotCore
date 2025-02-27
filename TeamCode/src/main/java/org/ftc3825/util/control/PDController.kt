package org.ftc3825.util.control

import org.ftc3825.util.geometry.Rotation2D
import org.ftc3825.util.geometry.Vector2D
import kotlin.math.abs
import kotlin.math.sign

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
) = ( error * p - velocity * d)

fun pdControl(
    error: Rotation2D,
    velocity: Rotation2D,
    p: Double,
    d: Double,
) = ( error * p - velocity * d).coerceIn(-1.0, 1.0)

fun pdfControl(
    error: Double,
    velocity: Double,
    p: Double,
    d: Double,
    f: Double,
    fIsAbsolute: Boolean
): Double {
    val pd = ( p * error - d * velocity )
    return ( pd + if(fIsAbsolute) f else abs(f) * pd.sign ).coerceIn(-1.0, 1.0)
}

fun pdfControl(
    error: Vector2D,
    velocity: Vector2D,
    p: Double,
    d: Double,
    f: Double,
): Vector2D {
    val pd = ( error * p - velocity * d )
    pd.mag += f
    return pd
}

fun pdfControl(
    error: Rotation2D,
    velocity: Rotation2D,
    p: Double,
    d: Double,
    f: Double,
    fIsAbsolute: Boolean
): Rotation2D {
    val pd = ( error * p - velocity * d )
    return (
        pd
        + Rotation2D(
            if(fIsAbsolute) f
            else abs(f) * pd.sign
        )
    ).coerceIn(-1.0, 1.0)
}