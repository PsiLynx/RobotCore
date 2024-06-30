package org.firstinspires.ftc.teamcode.test

import org.firstinspires.ftc.teamcode.GVF.Line
import org.firstinspires.ftc.teamcode.GVF.Path
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
            20, -1
            )
        )
        for(i in 0..50) {
            drivetrain.follow(path)
            println(path[0]( path[0].closestT(Vector2D(0.0, 0.0)) ))
            hardwareMap.updateDevices()

            println(drivetrain.position.vector)
        }
        assertTrue( (drivetrain.position.vector - path[0].end).mag < 1e-1)

    }

}