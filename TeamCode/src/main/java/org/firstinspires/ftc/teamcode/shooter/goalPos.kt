package org.firstinspires.ftc.teamcode.shooter

import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Range
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.teamcode.geometry.valMap
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.util.Globals

object goalPos {
    val goalPos2D: Vector2D by lazy {
        if (Globals.alliance == Globals.Alliance.RED) {
            ShooterConfig.redGoal - Vector2D(
                Globals.artifactDiameter / 2,
                Globals.artifactDiameter / 2
            )
        } else if (Globals.alliance == Globals.Alliance.BLUE) {
            ShooterConfig.blueGoal - Vector2D(
                -Globals.artifactDiameter / 2,
                Globals.artifactDiameter / 2
            )
        } else {
            Vector2D()
        }
    }

    fun compGoalPos(
        fromPos: Pose2D = TankDrivetrain.position
    ): Vector3D {
        return Vector3D(
            goalPos2D.x,
            goalPos2D.y,
            valMap(
                (goalPos2D - fromPos.vector).mag,
                Range(0, 101),
                Range(ShooterConfig.minGoalHeight, ShooterConfig.maxGoalHeight)
            )
        )
    }
}

