package org.firstinspires.ftc.teamcode.gvf


import org.firstinspires.ftc.teamcode.geometry.ChassisSpeeds
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Ramsete is a nonlinear time-varying feedback controller for unicycle models that drives the model
 * to a desired pose along a two-dimensional trajectory. Why would we need a nonlinear control law
 * in addition to the linear ones we have used so far like PID? If we use the original approach with
 * PID controllers for left and right position and velocity states, the controllers only deal with
 * the local pose. If the robot deviates from the path, there is no way for the controllers to
 * correct and the robot may not reach the desired global pose. This is due to multiple endpoints
 * existing for the robot which have the same encoder path arc lengths.
 *
 *
 * Instead of using wheel path arc lengths (which are in the robot's local coordinate frame),
 * nonlinear controllers like pure pursuit and Ramsete use global pose. The controller uses this
 * extra information to guide a linear reference tracker like the PID controllers back in by
 * adjusting the references of the PID controllers.
 *
 *
 * The paper "Control of Wheeled Mobile Robots: An Experimental Overview" describes a nonlinear
 * controller for a wheeled vehicle with unicycle-like kinematics; a global pose consisting of x, y,
 * and theta; and a desired pose consisting of x_d, y_d, and theta_d. We call it Ramsete because
 * that's the acronym for the title of the book it came from in Italian ("Robotica Articolata e
 * Mobile per i SErvizi e le TEcnologie").
 *
 *
 * See [this paper](https://file.tavsys.net/control/ramsete-unicycle-controller.pdf)
 * for a derivation and analysis.
 */
class RamseteController
/**
 * Construct a Ramsete unicycle controller.
 *
 * @param m_b Tuning parameter (b &gt; 0 rad²/m²) for which larger values make convergence more
 * aggressive like a proportional term.
 * @param m_zeta Tuning parameter (0 rad⁻¹ &lt; zeta &lt; 1 rad⁻¹) for which larger values provide
 * more damping in response.
 */  (
    private val m_b: Double = 2.0,
    private val m_zeta: Double = 0.7,
) {
    private var m_poseError = Pose2D()

    /**
     * Returns the next output of the Ramsete controller.
     *
     *
     * The reference pose, linear velocity, and angular velocity should come from a drivetrain
     * trajectory.
     *
     * @param currentPose The current pose.
     * @param poseRef The desired pose.
     * @param linearVelocityRefInches The desired linear velocity in inches per second.
     * @param angularVelocityRefRadiansPerSecond The desired angular velocity in radians per second.
     * @return The next controller output.
     */
    fun calculate(
        currentPose: Pose2D,
        poseRef: Pose2D,
        linearVelocityRefInches: Double,
        angularVelocityRefRadiansPerSecond: Double
    ): ChassisSpeeds {
        m_poseError = poseRef.relativeTo(currentPose)

        // Aliases for equation readability
        val eX = m_poseError.x / 39.37
        val eY = m_poseError.y / 39.37
        val eTheta = m_poseError.heading.toDouble()
        val vRef = linearVelocityRefInches / 39.37
        val omegaRef = angularVelocityRefRadiansPerSecond

        // k = 2ζ√(ω_ref² + b v_ref²)
        val k = 2.0 * m_zeta * sqrt(omegaRef.pow(2.0) + m_b * vRef.pow(2.0))

        // v_cmd = v_ref cos(e_θ) + k e_x
        // ω_cmd = ω_ref + k e_θ + b v_ref sinc(e_θ) e_y
        return ChassisSpeeds(
            0.0,
            (
                vRef * cos(eTheta) + k * eX
            ) * 39.37,
            omegaRef + k * eTheta + m_b * vRef * sinc(eTheta) * eY
        )
    }

    companion object {
        /**
         * Returns sin(x) / x.
         *
         * @param x Value of which to take sinc(x).
         */
        private fun sinc(x: Double): Double {
            if (abs(x) < 1e-9) {
                return 1.0 - 1.0 / 6.0 * x * x
            } else {
                return sin(x) / x
            }
        }
    }
}
