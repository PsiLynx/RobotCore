package org.firstinspires.ftc.teamcode.shooter

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import kotlin.math.PI

@Config object ShooterConfig {
    var flywheelOffset = Vector3D(-3, 0, 13)
    var flywheelRadius = 2.0
    var ballOffset = Vector2D(-flywheelRadius - 2.5, 0) rotatedBy ( PI / 4 )
    var turretDisabled = false
    var flywheelDisabled = false
    @JvmField var closeGoalHeight = 36
    @JvmField var farGoalHeight = 36

    //how far back from the goal point the through point should be
    @JvmField var throughPointOffsetX = -10
    //the default height of the through point from the ground
    @JvmField var defaultThroughPointY = 45
    @JvmField var redGoalX = 68
    @JvmField var redGoalY = 66
    val redGoal get() = Vector2D(redGoalX, redGoalY)
    val blueGoal get() = Vector2D(-redGoal.x, redGoal.y)
}