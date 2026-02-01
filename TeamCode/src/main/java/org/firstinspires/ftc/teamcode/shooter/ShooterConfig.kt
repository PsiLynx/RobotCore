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
    @JvmField var maxGoalHeight = 50

    //how far back from the goal point the through point should be
    @JvmField var defaultThroughPointOffsetX = -7
    //the default height of the through point from the ground
    @JvmField var defaultThroughPointY = 45
    @JvmField var redGoalX = 66
    @JvmField var redGoalY = 66
    var redGoal = Vector2D(redGoalX, redGoalY)
    var blueGoal = Vector2D(-redGoal.x, redGoal.y)
}