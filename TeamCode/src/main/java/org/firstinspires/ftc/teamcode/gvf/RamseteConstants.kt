package org.firstinspires.ftc.teamcode.gvf

import com.acmerobotics.dashboard.config.Config

@Config
object RamseteConstants {
    @JvmField var DRIVE_P = 3.0
    @JvmField var DRIVE_D = 0.0

    @JvmField var HEADING_P = 1.5
    @JvmField var HEADING_D = 0.0
    @JvmField var HEADING_F = 1.0

    @JvmField var A_MAX = 70.0
    @JvmField var D_MAX = 40.0

    @JvmField var ACCEL_F  = 0.0025
    @JvmField var HEADING_ACCEL_F  = 1.4

    @JvmField var FEED_FORWARD = 0.07

    @JvmField var CENTRIPETAL_MAX = 40

    @JvmField var USE_COMP = true
}