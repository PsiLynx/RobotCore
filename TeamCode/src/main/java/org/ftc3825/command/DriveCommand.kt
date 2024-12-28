package org.ftc3825.command

import org.ftc3825.gvf.HeadingType
import org.ftc3825.gvf.Line
import org.ftc3825.gvf.Path
import org.ftc3825.command.internal.Command
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.util.Pose2D
import org.ftc3825.util.Vector2D

class DriveCommand(
    val direction: Direction,
    val distance: Double
): Command(requirements = mutableSetOf(Drivetrain)) {
    private val travelVector = when (direction) {
        Direction.FORWARD -> Vector2D(0, 1)
        Direction.BACK -> Vector2D(0, -1)
        Direction.LEFT -> Vector2D(-1, 0)
        Direction.RIGHT -> Vector2D(1, 0)

    }
    lateinit var path: Path

    init {
        addRequirement(Drivetrain)

    }

    override fun initialize() {
        path = Path(
            arrayListOf(
                Line(
                    Drivetrain.position.vector,
                    Drivetrain.position.vector + travelVector * distance,
                    HeadingType.Tangent()
                )
            )
        )
    }

    override fun execute() {
        Drivetrain.setWeightedDrivePower(
            path.pose(Drivetrain.position, Drivetrain.velocity)
        )
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
