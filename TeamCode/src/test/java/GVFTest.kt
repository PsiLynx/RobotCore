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
import org.firstinspires.ftc.teamcode.util.inches
import org.junit.Assert.assertTrue
import org.junit.Test


class GVFTest {
    private var hardwareMap = FakeHardwareMap()
    var localizer = FakeLocalizer(hardwareMap)
    val scheduler = CommandScheduler()

    @Test
    fun lineTest() {
        var path = Path(
            Line(
            inches(0), inches(-1),
            inches(50), inches(-1)
            )
        )
        test(path)

    }
    @Test
    fun splineTest() {
        var path = Path(
            Spline(
                inches(0), inches(0),
                inches(30), inches(0),
                inches(20), inches(50),
                inches(20), inches(30)
            )
        )
        test(path)
    }

    @Test
    fun sequenceTest() {
        var path = Path(
            Line(
                inches(0), inches(-1),
                inches(50), inches(-1)
            ),
            Spline(
                inches(50), inches(-1),
                inches(80), inches(-1),
                inches(70), inches(50),
                inches(70), inches(30)
            ),
            Line(
                inches(70), inches(50),
                inches(70), inches(100)
            )
        )

        test(path)
    }

    private fun test(path: Path) {
        localizer = FakeLocalizer(hardwareMap)
        scheduler.schedule(FollowPathCommand(Drivetrain, localizer, path))
        for(i in 0..1000*path.length) {
            scheduler.update()
            hardwareMap.updateDevices()
        }
        assertTrue( (localizer.position.vector - path[-1].end).mag < inches(0.5))
    }

}