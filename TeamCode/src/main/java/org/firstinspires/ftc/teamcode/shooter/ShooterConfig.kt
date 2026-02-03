package org.firstinspires.ftc.teamcode.shooter

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import kotlin.math.PI

@Config object ShooterConfig {
    var flywheelOffset = Vector3D(-3, 0, 13)
    var flywheelRadius = 2.0
    @JvmField var closeGoalHeight = 34
    @JvmField var farGoalHeight = 34

    //how far back from the goal point the through point should be
    @JvmField var defaultThroughPointOffsetX = -15
    //the default height of the through point from the ground
    @JvmField var defaultThroughPointY = 45
    @JvmField var redGoalX = 68
    @JvmField var redGoalY = 58
    val redGoal get() = Vector2D(redGoalX, redGoalY)
    val blueGoal get() = Vector2D(-redGoal.x, redGoal.y)
}