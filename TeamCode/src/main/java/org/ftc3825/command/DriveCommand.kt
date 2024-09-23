package org.ftc3825.command

import org.ftc3825.GVF.Line
import org.ftc3825.GVF.Path
import org.ftc3825.command.internal.Command
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.ThreeDeadWheelLocalizer
import org.ftc3825.util.Pose2D
import org.ftc3825.util.Rotation2D
import org.ftc3825.util.Vector2D
import org.ftc3825.util.inches
import kotlin.io.path.Path

class DriveCommand(
    val localizer: ThreeDeadWheelLocalizer,
    val direction: Direction,
    val distance: Double
): Command() {
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
                localizer.position.vector,
                localizer.position.vector + travelVector * distance
            )
        )
    }

    override fun execute() {
        localizer.update()
        Drivetrain.setWeightedDrivePower(path.vector(localizer.position) + Rotation2D())
    }

    override fun isFinished(): Boolean {
        return (localizer.position.vector - path[-1].end).mag < (0.7)
    }

    override fun end(interrupted: Boolean) =
        Drivetrain.setWeightedDrivePower( Pose2D(0, 0, 0) )

   enum class Direction(){
       FORWARD, BACK, LEFT, RIGHT
   }
}