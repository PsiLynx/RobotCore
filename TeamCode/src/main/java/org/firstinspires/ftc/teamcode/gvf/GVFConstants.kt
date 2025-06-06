package org.firstinspires.ftc.teamcode.gvf

import com.acmerobotics.dashboard.config.Config

@Config
object GVFConstants {
    @JvmField var SPLINE_RES = 0.001

    @JvmField var FEED_FORWARD = 0.0

    @JvmField var DRIVE_P = 0.09

    @JvmField var DRIVE_D = 1.2

    @JvmField var TRANS_P = 0.09

    @JvmField var TRANS_D = 0.005

    @JvmField var HEADING_P = 0.6

    @JvmField var HEADING_D = 0.3

    @JvmField var CENTRIPETAL = 0.000003

    @JvmField var PATH_END_T = 0.99

    @JvmField var MAX_VELO = 75.0

    @JvmField var USE_COMP = true
    @JvmField var USE_CENTRIPETAL = true

}