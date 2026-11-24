package org.firstinspires.ftc.teamcode.gvf

import com.acmerobotics.dashboard.config.Config
import kotlin.math.PI

@Config
object RamseteConstants {
    @JvmField var DRIVE_P = 3.0
    @JvmField var DRIVE_D = 0.0
    @JvmField var DRIVE_Ks = 0.0

    @JvmField var HEADING_P = 1.5
    @JvmField var HEADING_D = 0.0
    @JvmField var HEADING_Ks = 0.0

    @JvmField var A_MAX = 50.0
    @JvmField var D_MAX = 50.0

    @JvmField var ACCEL_F  = 0.01
    @JvmField var HEADING_ACCEL_F  = 0.02

    @JvmField var FEED_FORWARD = 0.07

    @JvmField var CENTRIPETAL_MAX = 40.0

    @JvmField var USE_COMP = true

    @JvmField var MAX_VELO = 78.0
    @JvmField var MAX_HEADING_VELO = 4 * PI
}