package org.firstinspires.ftc.teamcode.shooter

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import kotlin.math.PI

@Config object ShooterConfig {
    //Shooter globals:
    @JvmField var flywheelOffset = Vector3D(-1, 0, 13)
    @JvmField val flywheelRadius = 2.0
    @JvmField val ballOffset = Vector2D(-flywheelRadius - 2.5, 0) rotatedBy PI / 4
    @JvmField val minGoalHeight = 40
    @JvmField val maxGoalHeight = 45
    @JvmField val defaultThroughPoint = Vector2D(-2,1)
    @JvmField val redGoal = Vector2D(64, 64)
    @JvmField val blueGoal = Vector2D(-64, 64)
}