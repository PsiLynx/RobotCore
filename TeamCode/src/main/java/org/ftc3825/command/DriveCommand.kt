package org.ftc3825.command

import org.ftc3825.GVF.Line
import org.ftc3825.GVF.Path
import org.ftc3825.command.internal.Command
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.util.Pose2D
import org.ftc3825.util.Rotation2D
import org.ftc3825.util.Vector2D

class DriveCommand(
    val direction: Direction,
    val distance: Double
): Command(requirements = arrayListOf(Drivetrain)) {
    private val travelVector = when (direction) {
        Direction.FORWARD -> Vector2D(0, 1)
        Direction.BACK -> Vector2D(0, -1)
        Direction.LEFT -> Vector2D(-1, 0)
        Direction.RIGHT -> Vector2D(1, 0)

    }
    var path = Path()

    init {
        addRequirement(Drivetrain)

    }

    override fun initialize() {
        path = Path(
            Line(
                Drivetrain.position.vector,
                Drivetrain.position.vector + travelVector * distance
            )
        )
    }

    override fun execute() {
        Drivetrain.setWeightedDrivePower(path.pose(Drivetrain.position) + Rotation2D())
    }

    override fun isFinished(): Boolean {
        return (Drivetrain.position.vector - path[-1].end).mag < (0.7)
    }

    override fun end(interrupted: Boolean) =
        Drivetrain.setWeightedDrivePower( Pose2D(0, 0, 0) )

   enum class Direction{
       FORWARD, BACK, LEFT, RIGHT
   }
}
