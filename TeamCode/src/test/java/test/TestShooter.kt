package test

import android.provider.Settings
import org.firstinspires.ftc.teamcode.command.ShootingState
import org.firstinspires.ftc.teamcode.command.ShootingStateOTM
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Prism3D
import org.firstinspires.ftc.teamcode.geometry.Quad3D
import org.firstinspires.ftc.teamcode.geometry.Triangle3D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.teamcode.sim.SimulatedArtifact
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.FlywheelConfig
import org.firstinspires.ftc.teamcode.subsystem.Hood
import org.firstinspires.ftc.teamcode.subsystem.Turret
import org.firstinspires.ftc.teamcode.trajcode.ComputeGoalThings
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.log
import org.junit.Test
import org.junit.runner.RunWith
import org.psilynx.psikit.core.Logger
import org.psilynx.psikit.core.rlog.RLOGServer
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.lang.Thread.sleep
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Config(shadows = [ShadowAppUtil::class])
@RunWith(RobolectricTestRunner::class)
class TestShooter: TestClass() {
    val goalBottom = Prism3D(
        Triangle3D(
            Vector3D(-70.16, -63.63, 30.73),
            Vector3D(-48.62, -63.44, 30.73),
            Vector3D(-70.16, -47.69, 35.00),
        ),
        0.39
    )
    val goalBackLeft = Prism3D(
        Quad3D(
            Vector3D(-70.16, -70.19, 53.75),
            Vector3D(-43.55, -70.19, 38.62),
            Vector3D(-43.55, -70.19, 00.00),
            Vector3D(-70.16, -70.19, 00.00),
        ),
        0.39
    )
    val goalBackRight = Prism3D(
        Quad3D(
            Vector3D(-70.16, -70.19, 00.00),
            Vector3D(-70.16, -43.19, 00.00),
            Vector3D(-70.16, -43.19, 38.63),
            Vector3D(-70.16, -70.19, 53.75),
        ),
        0.39
    )

    val goalFront = Prism3D(
        Quad3D(
            Vector3D(-48.17, -63.04, 38.75),
            Vector3D(-69.83, -47.33, 38.75),
            Vector3D(-69.83, -47.33, 00.00),
            Vector3D(-48.17, -63.04, 00.00),
        ),
        -0.39
    )
    val goalRamp = Prism3D(
        Quad3D(
            Vector3D(-70.16, -63.63, 30.73),
            Vector3D(-70.16, -69.94, 31.00),
            Vector3D(-42.88, -69.94, 23.13),
            Vector3D(-42.88, -63.63, 22.90),
        ),
        -1.0
    )
    val goalRampLeft = Prism3D(
        Quad3D(
            Vector3D(-69.76, -69.01, 30.78),
            Vector3D(-69.94, -68.30, 30.08),
            Vector3D(-70.09, -69.01, 29.53),
            Vector3D(-69.94, -69.58, 30.08),
        ),
        28.0
    )
    val goalRampRight = Prism3D(
        Quad3D(
            Vector3D(-69.76, -69.01 + 4.5, 30.78),
            Vector3D(-69.94, -68.30 + 4.5, 30.08),
            Vector3D(-70.09, -69.01 + 4.5, 29.53),
            Vector3D(-69.94, -69.58 + 4.5, 30.08),
        ),
        28.0
    )

    val goalArchBlocking = Prism3D(
        Quad3D(
            Vector3D(-48.23, -69.94, 38.75),
            Vector3D(-48.23, -63.69, 38.75),
            Vector3D(-48.23, -63.69, 00.00),
            Vector3D(-48.23, -69.94, 00.00),
        ),
        -0.39
    )

    @Test fun testNoHood() {
        val pos = Vector3D(-36, 36, 13)
        val goal = ComputeGoalThings.goalPos(pos.groundPlane)

        val shootingSpeed = Flywheel.getVelNoHood(
            (pos.groundPlane * Vector2D(1, 1) - goal.groundPlane).mag
        )

        val verticalSpeed = sin(Flywheel.phiNoHood) * shootingSpeed
        val horizontalSpeed = cos(Flywheel.phiNoHood) * shootingSpeed

        test(
            pos,
            Vector3D(
                - horizontalSpeed / sqrt(2.0),
                horizontalSpeed / sqrt(2.0),
                verticalSpeed
            ),
        )
    }

    @Test fun testWithHoodTurret(){
        Globals.alliance = Globals.Alliance.BLUE
        val pos = Vector3D(-40, 30, Globals.flywheelOffset.y)
        val botVel = Pose2D(-5, 10)
        val goal = ComputeGoalThings.goalPos(pos.groundPlane)
        println("dist_to_target: ${(goal.groundPlane-pos.groundPlane)}")

        val command = ShootingStateOTM (
            {(pos * Vector3D(1, 1,1)).groundPlane },
            { botVel },
            {goal},
        )
        val first = System.nanoTime()
        command.execute()
        val second = System.nanoTime()
        println("Total command time: ${(second - first)/1_000_000.0} ms")


        val angle = Turret.fieldCentricAngle
        val velGroundPlane = (
            -cos(Hood.targetAngle + PI/2)
            * Flywheel.targetState.velocity.toDouble()
        )

        println("heading ${angle*180/PI}")
        println("targetState angle ${(Hood.targetAngle + PI/2)*180/PI}")
        println("velGroundPlane $velGroundPlane")
        var vecX = cos(angle)*velGroundPlane + botVel.x
        var vecY = sin(angle)*velGroundPlane + botVel.y
        var vecZ = (
            sin(Hood.targetAngle + PI/2)
            * Flywheel.targetState.velocity.toDouble()
        )

        test(
            pos,
            Vector3D(
                vecX,
                vecY,
                vecZ,
            ),
        )
    }

    @Test fun testWithHood() {
        val pos = Vector3D(-50, 0, Globals.flywheelOffset.y)
        val goal = ComputeGoalThings.goalPos(pos.groundPlane)
        println("GoalPos $goal")
        println("Through Point ${Globals.throughPoint}")
        println("horizontalThorughPoint ${ComputeGoalThings.horizontalThroughPoint}")

        val command = ShootingState (
            fromPos = { pos.groundPlane * Vector2D(1,1) },
            target = {goal},
        )
        command.execute()

        println("flywheelVEl ${Flywheel.targetState.velocity}")


        val angle = atan2(goal.y-pos.groundPlane.y, goal.x - pos.groundPlane.x)
        val velGroundPlane = (
            cos(Hood.targetAngle)
            * Flywheel.targetState.velocity.toDouble()
        )

        println("heading ${angle*180/PI}")
        println("targetState angle ${Hood.targetAngle*180/PI}")
        println("velGroundPlane $velGroundPlane")
        var vecX = cos(angle)*velGroundPlane
        var vecY = sin(angle)*velGroundPlane
        var vecZ = (
            sin(Hood.targetAngle)
            * Flywheel.targetState.velocity.toDouble()
        )

        val launchVec = Vector3D(vecX, vecY, vecZ)
        println("magnitude: ${launchVec.mag}")

        test(
            pos,
            launchVec
        )
    }

    fun test(position: Vector3D, velocity: Vector3D){
        val pose_hist = arrayListOf<Vector3D>()

        val artifact = SimulatedArtifact(
            //This monkey business is needed because the positions gets flipped around
            // when sent to advantage scope.
            Vector3D(-position.y,position.x,position.z),
            Vector3D(-velocity.y,velocity.x,velocity.z),
            mutableListOf(
                goalBottom,
                goalBackLeft,
                goalBackRight,
                goalFront,
                goalRamp,
                goalArchBlocking
            )
        )
        var t = 0.0
        val dt = 0.01
        Logger.reset()
        Logger.setTimeSource { t }
        Logger.addDataReceiver(RLOGServer())
        Logger.start()

        sleep(50)
        var passing = false
        while(t < 5 && !passing){
            Logger.periodicBeforeUser()
            artifact.update(dt)
            log("goalBottom") value goalBottom
            log("goalBackLeft") value goalBackLeft
            log("goalBackRight") value goalBackRight

            log("goalFront") value goalFront
            log("goalRamp") value goalRamp
            log("goalRampLeft") value goalRampLeft
            log("goalRampRight") value goalRampRight
            log("goalArchBlocking") value goalArchBlocking

            if( ((t * 20) % 1) < 0.05 ) pose_hist.add(artifact.pos)
            Logger.recordOutput("pose_hist", pose_hist.map { it / 39.37 }.toTypedArray())
            Flywheel.update(dt)
            Hood.update(dt)

            log("ball") value artifact

            artifact.collisions.withIndex().forEach { (i, value) ->
                log("collision/$i") value value.vertices
            }
            if(
                artifact.collisions.find { collision ->
                    goalBottom.faces.find { it == collision } != null
                    ||goalRamp.faces.find { it == collision } != null
                } != null

                && artifact.vel.mag < 10
            ){
                passing = true
            }
            t += dt
            sleep(10)
            Logger.periodicAfterUser(0.0, 0.0)
        }
        assert(passing)
    }
}
