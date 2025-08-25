package org.firstinspires.ftc.teamcode.util.geometry

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit

typealias SDKPose = org.firstinspires.ftc.robotcore.external.navigation.Pose2D

fun SDKPose.fromSDKPose() = Pose2D(
    this.getX(DistanceUnit.INCH),
    this.getY(DistanceUnit.INCH),
    this.getHeading(AngleUnit.RADIANS),
)