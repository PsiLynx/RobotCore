package org.firstinspires.ftc.teamcode.shooter

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import kotlin.math.PI

@Config object ShooterConfig {
    @JvmField var flywheelOffset = Vector3D(-1, 0, 13)
    var flywheelRadius = 2.0
    var ballOffset = Vector2D(-flywheelRadius - 2.5, 0) rotatedBy PI / 4
    @JvmField var minGoalHeight = 40
    @JvmField var maxGoalHeight = 45
    @JvmField var defaultThroughPointX = -2
    @JvmField var defaultThroughPointY = 1
    var defaultThroughPoint = Vector2D(defaultThroughPointX, defaultThroughPointY)
    @JvmField var redGoalX = 64
    @JvmField var redGoalY = 64
    var redGoal = Vector2D(redGoalX, redGoalY)
    var blueGoal = Vector2D(-redGoal.x, redGoal.y)

}