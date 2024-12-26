package org.ftc3825.GVF

import kotlin.math.ceil

object GVFConstants {
    const val splineResolution = 0.001

    const val headingPower = 1.0
    const val aggresiveness = 1.0

    const val driveP = 1.0
    const val driveD = 0.0

    const val headingP = 1.0
    const val headingD = 0.0

    const val pathEndTValue = 0.99

    val pointsInLUT = ceil(1 / splineResolution)
}