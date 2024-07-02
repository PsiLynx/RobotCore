package org.firstinspires.ftc.teamcode.test

import org.firstinspires.ftc.teamcode.GVF.Line
import org.firstinspires.ftc.teamcode.GVF.Path
import org.firstinspires.ftc.teamcode.GVF.Spline
import org.firstinspires.ftc.teamcode.command.Command
import org.firstinspires.ftc.teamcode.command.CommandScheduler
import org.firstinspires.ftc.teamcode.command.FollowPathCommand
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.fakehardware.FakeLocalizer
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.ThreeDeadWheelLocalizer
import org.firstinspires.ftc.teamcode.util.Pose2D
import org.firstinspires.ftc.teamcode.util.Vector2D
import org.junit.Assert.assertTrue
import org.junit.Test


class GVFTest {
    private var hardwareMap = FakeHardwareMap()
    var drivetrain = Drivetrain(hardwareMap)
    var localizer = FakeLocalizer(hardwareMap)
    val scheduler = CommandScheduler()

    @Test
    fun lineTest() {
        var path = Path(
            Line(
            0, -1,
            50, -1
            )
        )
        test(path)

    }
    @Test
    fun splineTest() {
        var path = Path(
            Spline(
                0, 0,
                30, 0,
                20, 50,
                20, 30
            )
        )
        test(path)
    }

    @Test
    fun sequenceTest() {
        var path = Path(
            Line(
                0, -1,
                50, -1
            ),
            Spline(
                50, -1,
                80, -1,
                70, 50,
                70, 30
            ),
            Line(
                70, 50,
                70, 100
            )
        )

        test(path)
    }

    private fun test(path: Path) {
        localizer = FakeLocalizer(hardwareMap)
        scheduler.schedule(FollowPathCommand(drivetrain, localizer, path))
        for(i in 0..1000*path.length) {
            scheduler.update()
            hardwareMap.updateDevices()
        }
        assertTrue( (localizer.position.vector - path[-1].end).mag < 0.5)
    }

}