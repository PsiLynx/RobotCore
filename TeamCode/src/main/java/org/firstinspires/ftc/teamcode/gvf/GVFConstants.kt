package org.firstinspires.ftc.teamcode.gvf

import com.acmerobotics.dashboard.config.Config

@Config
object GVFConstants {
    @JvmField var SPLINE_RES = 0.001

    @JvmField var HEADING_POW = 1.0

    @JvmField var FEED_FORWARD = 0.06

    @JvmField var DRIVE_P = 0.06
    @JvmField var DRIVE_D = 0.006

    @JvmField var TRANS_P = 0.2
    @JvmField var TRANS_D = 0.007

    @JvmField var HEADING_P = 1.0
    @JvmField var HEADING_D = 0.0

    @JvmField var CENTRIPETAL = 0.01

    @JvmField var PATH_END_T = 0.99

    @JvmField var USE_COMP = true
    @JvmField var USE_CENTRIPETAL = true

}