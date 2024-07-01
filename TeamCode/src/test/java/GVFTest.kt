package org.firstinspires.ftc.teamcode.test

import org.firstinspires.ftc.teamcode.GVF.Line
import org.firstinspires.ftc.teamcode.GVF.Path
import org.firstinspires.ftc.teamcode.GVF.Spline
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.util.Pose2D
import org.firstinspires.ftc.teamcode.util.Vector2D
import org.junit.Assert.assertTrue
import org.junit.Test


class GVFTest {
    private var hardwareMap = FakeHardwareMap()
    var drivetrain = Drivetrain(hardwareMap)

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

    fun test(path: Path) {
        for(i in 0..500*path.length) {
            drivetrain.follow(path)
            hardwareMap.updateDevices()

            println(drivetrain.position.vector)
        }
        assertTrue( (drivetrain.position.vector - path[-1].end).mag < 0.5)
    }

}