package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.component.Motor.ZeroPower.FLOAT
import org.firstinspires.ftc.teamcode.gvf.Path
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.DrivetrainConf.HEADING_D
import org.firstinspires.ftc.teamcode.subsystem.DrivetrainConf.HEADING_P
import org.firstinspires.ftc.teamcode.util.GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD
import org.firstinspires.ftc.teamcode.util.Drawing
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.geometry.Pose2D
import org.firstinspires.ftc.teamcode.util.control.PIDFController
import org.firstinspires.ftc.teamcode.util.inches
import org.firstinspires.ftc.teamcode.util.log
import org.firstinspires.ftc.teamcode.util.millimeters
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sign

@Config
object DrivetrainConf{
    @JvmField var HEADING_P = 0.4
    @JvmField var HEADING_D = 0.6
}

object Drivetrain : Subsystem<Drivetrain>() {
    const val pinpointPriority = 10.0

    private val frontLeft  = HardwareMap.frontLeft (FORWARD, 1.0, 1.0)
    private val frontRight = HardwareMap.frontRight(REVERSE, 1.0, 1.0)
    private val backLeft   = HardwareMap.backLeft  (FORWARD, 1.0, 1.0)
    private val backRight  = HardwareMap.backRight (REVERSE, 1.0, 1.0)
    val cornerPos = Pose2D(63, -66, PI / 2)
    var pinpointSetup = false

//    val octoQuad = OctoQuad(
//        "octoquad",
//        xPort = 0,
//        yPort = 1,
//        ticksPerMM = 13.26291192,
//        offset = Vector2D(-36.0 , -70.0),
//        xDirection = FORWARD,
//        yDirection = REVERSE,
//        headingScalar = 1.0
//    )
    val pinpoint = HardwareMap.pinpoint(pinpointPriority)
    override var components: List<Component> = arrayListOf<Component>(
        frontLeft,
        backLeft,
        backRight,
        frontRight,
        pinpoint
    )

    var position: Pose2D
        get() = arrayListOf(Pose2D(0.0, 0.0, 1.57),Pose2D(0.016, 0.0, 1.57),Pose2D(0.026, 0.0, 1.57),Pose2D(0.038, 0.0, 1.57),Pose2D(0.054, 0.0, 1.57),Pose2D(0.073, 0.0, 1.57),Pose2D(0.094, 0.0, 1.57),Pose2D(0.118, 0.0, 1.57),Pose2D(0.145, 0.0, 1.57),Pose2D(0.175, 0.0, 1.57),Pose2D(0.207, 0.0, 1.57),Pose2D(0.242, 0.0, 1.57),Pose2D(0.28, 0.0, 1.57),Pose2D(0.32, 0.0, 1.57),Pose2D(0.363, 0.0, 1.57),Pose2D(0.408, 0.0, 1.57),Pose2D(0.456, 0.0, 1.57),Pose2D(0.507, 0.0, 1.57),Pose2D(0.56, 0.0, 1.57),Pose2D(0.615, 0.0, 1.57),Pose2D(0.673, 0.0, 1.57),Pose2D(0.733, 0.0, 1.57),Pose2D(0.796, 0.0, 1.57),Pose2D(0.861, 0.0, 1.57),Pose2D(0.929, 0.0, 1.57),Pose2D(0.998, 0.0, 1.57),Pose2D(1.07, 0.0, 1.57),Pose2D(1.145, 0.0, 1.57),Pose2D(1.275, 0.0, 1.57),Pose2D(1.355, 0.0, 1.57),Pose2D(1.438, 0.0, 1.57),Pose2D(1.522, 0.0, 1.57),Pose2D(1.609, 0.0, 1.57),Pose2D(1.698, 0.0, 1.57),Pose2D(1.789, 0.0, 1.57),Pose2D(1.882, 0.0, 1.57),Pose2D(1.978, 0.0, 1.57),Pose2D(2.075, 0.0, 1.57),Pose2D(2.242, 0.0, 1.57),Pose2D(2.345, 0.0, 1.57),Pose2D(2.45, 0.0, 1.57),Pose2D(2.556, 0.0, 1.57),Pose2D(2.665, 0.0, 1.57),Pose2D(2.775, 0.0, 1.57),Pose2D(2.964, 0.0, 1.57),Pose2D(3.079, 0.0, 1.57),Pose2D(3.197, 0.0, 1.57),Pose2D(3.316, 0.0, 1.57),Pose2D(3.437, 0.0, 1.57),Pose2D(3.56, 0.0, 1.57),Pose2D(3.768, 0.0, 1.57),Pose2D(3.896, 0.0, 1.57),Pose2D(4.025, 0.0, 1.57),Pose2D(4.156, 0.0, 1.57),Pose2D(4.378, 0.0, 1.57),Pose2D(4.513, 0.0, 1.57),Pose2D(4.65, 0.0, 1.57),Pose2D(4.789, 0.0, 1.57),Pose2D(5.024, 0.0, 1.57),Pose2D(5.166, 0.0, 1.57),Pose2D(5.311, 0.0, 1.57),Pose2D(5.555, 0.0, 1.57),Pose2D(5.704, 0.0, 1.57),Pose2D(5.854, 0.0, 1.57),Pose2D(6.107, 0.001, 1.57),Pose2D(6.261, 0.001, 1.57),Pose2D(6.521, 0.001, 1.57),Pose2D(6.679, 0.001, 1.57),Pose2D(6.945, 0.001, 1.57),Pose2D(7.107, 0.001, 1.57),Pose2D(7.379, 0.002, 1.57),Pose2D(7.544, 0.002, 1.57),Pose2D(7.823, 0.002, 1.57),Pose2D(7.991, 0.002, 1.57),Pose2D(8.276, 0.003, 1.57),Pose2D(8.448, 0.003, 1.57),Pose2D(8.737, 0.003, 1.57),Pose2D(8.913, 0.003, 1.57),Pose2D(9.208, 0.004, 1.57),Pose2D(9.387, 0.004, 1.57),Pose2D(9.687, 0.005, 1.57),Pose2D(9.869, 0.005, 1.57),Pose2D(10.175, 0.006, 1.57),Pose2D(10.359, 0.006, 1.57),Pose2D(10.67, 0.007, 1.57),Pose2D(10.858, 0.008, 1.57),Pose2D(11.173, 0.009, 1.57),Pose2D(11.364, 0.009, 1.57),Pose2D(11.684, 0.01, 1.57),Pose2D(12.007, 0.011, 1.57),Pose2D(12.333, 0.012, 1.57),Pose2D(12.662, 0.014, 1.57),Pose2D(12.993, 0.015, 1.57),Pose2D(13.327, 0.017, 1.57),Pose2D(13.663, 0.018, 1.57),Pose2D(14.003, 0.02, 1.57),Pose2D(14.344, 0.022, 1.57),Pose2D(14.688, 0.024, 1.57),Pose2D(15.034, 0.026, 1.57),Pose2D(15.383, 0.028, 1.57),Pose2D(15.734, 0.031, 1.57),Pose2D(16.087, 0.034, 1.57),Pose2D(16.443, 0.037, 1.57),Pose2D(16.8, 0.04, 1.57),Pose2D(17.16, 0.043, 1.57),Pose2D(17.522, 0.047, 1.57),Pose2D(17.885, 0.051, 1.57),Pose2D(18.251, 0.055, 1.57),Pose2D(18.618, 0.059, 1.57),Pose2D(18.987, 0.064, 1.57),Pose2D(19.358, 0.069, 1.57),Pose2D(19.731, 0.074, 1.57),Pose2D(20.105, 0.08, 1.57),Pose2D(20.481, 0.086, 1.57),Pose2D(20.859, 0.092, 1.57),Pose2D(21.238, 0.099, 1.57),Pose2D(21.618, 0.106, 1.57),Pose2D(22.0, 0.114, 1.57),Pose2D(22.383, 0.122, 1.57),Pose2D(22.768, 0.131, 1.57),Pose2D(23.153, 0.14, 1.57),Pose2D(23.54, 0.15, 1.57),Pose2D(23.928, 0.161, 1.57),Pose2D(24.317, 0.172, 1.57),Pose2D(24.707, 0.183, 1.57),Pose2D(25.098, 0.196, 1.57),Pose2D(25.489, 0.209, 1.57),Pose2D(25.882, 0.223, 1.57),Pose2D(26.275, 0.238, 1.57),Pose2D(26.669, 0.254, 1.57),Pose2D(27.063, 0.271, 1.57),Pose2D(27.458, 0.288, 1.57),Pose2D(27.853, 0.307, 1.57),Pose2D(28.249, 0.327, 1.57),Pose2D(28.644, 0.347, 1.57),Pose2D(29.04, 0.369, 1.57),Pose2D(29.437, 0.393, 1.57),Pose2D(29.833, 0.417, 1.57),Pose2D(30.229, 0.443, 1.57),Pose2D(30.625, 0.47, 1.57),Pose2D(31.021, 0.499, 1.57),Pose2D(31.416, 0.53, 1.57),Pose2D(31.811, 0.562, 1.57),Pose2D(32.205, 0.595, 1.57),Pose2D(32.599, 0.631, 1.57),Pose2D(32.992, 0.668, 1.57),Pose2D(33.385, 0.708, 1.57),Pose2D(33.776, 0.749, 1.57),Pose2D(34.167, 0.792, 1.57),Pose2D(34.556, 0.837, 1.57),Pose2D(34.945, 0.885, 1.57),Pose2D(35.332, 0.935, 1.57),Pose2D(35.718, 0.987, 1.57),Pose2D(36.103, 1.042, 1.57),Pose2D(36.486, 1.098, 1.57),Pose2D(36.867, 1.158, 1.57),Pose2D(37.247, 1.22, 1.57),Pose2D(37.625, 1.285, 1.57),Pose2D(38.002, 1.352, 1.57),Pose2D(38.377, 1.422, 1.57),Pose2D(38.749, 1.495, 1.57),Pose2D(39.12, 1.571, 1.57),Pose2D(39.489, 1.65, 1.57),Pose2D(39.856, 1.731, 1.57),Pose2D(40.22, 1.816, 1.57),Pose2D(40.582, 1.904, 1.57),Pose2D(40.941, 1.996, 1.57),Pose2D(41.298, 2.09, 1.57),Pose2D(41.653, 2.188, 1.57),Pose2D(42.005, 2.289, 1.57),Pose2D(42.355, 2.393, 1.57),Pose2D(42.702, 2.501, 1.57),Pose2D(43.047, 2.611, 1.57),Pose2D(43.388, 2.726, 1.57),Pose2D(43.728, 2.843, 1.57),Pose2D(44.064, 2.964, 1.57),Pose2D(44.398, 3.089, 1.57),Pose2D(44.729, 3.217, 1.57),Pose2D(45.057, 3.348, 1.57),Pose2D(45.383, 3.483, 1.57),Pose2D(45.705, 3.621, 1.57),Pose2D(46.025, 3.763, 1.57),Pose2D(46.342, 3.908, 1.57),Pose2D(46.656, 4.057, 1.57),Pose2D(46.967, 4.209, 1.57),Pose2D(47.275, 4.365, 1.57),Pose2D(47.58, 4.525, 1.57),Pose2D(47.882, 4.688, 1.57),Pose2D(48.18, 4.854, 1.57),Pose2D(48.476, 5.025, 1.57),Pose2D(48.769, 5.198, 1.57),Pose2D(49.059, 5.376, 1.57),Pose2D(49.346, 5.557, 1.57),Pose2D(49.629, 5.742, 1.57),Pose2D(49.909, 5.93, 1.57),Pose2D(50.187, 6.122, 1.57),Pose2D(50.461, 6.318, 1.57),Pose2D(50.731, 6.518, 1.57),Pose2D(50.999, 6.721, 1.57),Pose2D(51.263, 6.928, 1.57),Pose2D(51.524, 7.139, 1.57),Pose2D(51.782, 7.353, 1.57),Pose2D(52.036, 7.571, 1.57),Pose2D(52.288, 7.793, 1.57),Pose2D(52.632, 8.111, 1.57),Pose2D(52.97, 8.435, 1.57),Pose2D(53.302, 8.767, 1.57),Pose2D(53.627, 9.105, 1.57),Pose2D(53.944, 9.449, 1.57),Pose2D(54.256, 9.8, 1.57),Pose2D(54.56, 10.156, 1.57),Pose2D(54.857, 10.518, 1.57),Pose2D(55.147, 10.885, 1.57),Pose2D(55.431, 11.257, 1.57),Pose2D(55.707, 11.634, 1.57),Pose2D(55.901, 11.906, 1.57),Pose2D(56.165, 12.291, 1.57),Pose2D(56.423, 12.679, 1.57),Pose2D(56.603, 12.959, 1.57),Pose2D(56.848, 13.354, 1.57),Pose2D(57.02, 13.639, 1.57),Pose2D(57.253, 14.04, 1.57),Pose2D(57.416, 14.328, 1.57),Pose2D(57.638, 14.734, 1.57),Pose2D(57.852, 15.142, 1.57),Pose2D(58.059, 15.553, 1.57),Pose2D(58.26, 15.967, 1.57),Pose2D(58.454, 16.382, 1.57),Pose2D(58.641, 16.799, 1.57),Pose2D(58.821, 17.218, 1.57),Pose2D(58.995, 17.638, 1.57),Pose2D(59.162, 18.059, 1.57),Pose2D(59.323, 18.482, 1.57),Pose2D(59.478, 18.905, 1.57),Pose2D(59.625, 19.328, 1.57),Pose2D(59.767, 19.752, 1.57),Pose2D(59.903, 20.176, 1.57),Pose2D(60.032, 20.6, 1.57),Pose2D(60.155, 21.024, 1.57),Pose2D(60.272, 21.448, 1.57),Pose2D(60.384, 21.871, 1.57),Pose2D(60.489, 22.293, 1.57),Pose2D(60.589, 22.715, 1.57),Pose2D(60.683, 23.135, 1.57),Pose2D(60.772, 23.555, 1.57),Pose2D(60.855, 23.973, 1.57),Pose2D(60.933, 24.389, 1.57),Pose2D(61.005, 24.803, 1.57),Pose2D(61.072, 25.216, 1.57),Pose2D(61.134, 25.627, 1.57),Pose2D(61.191, 26.035, 1.57),Pose2D(61.244, 26.442, 1.57),Pose2D(61.291, 26.846, 1.57),Pose2D(61.333, 27.247, 1.57),Pose2D(61.371, 27.645, 1.57),Pose2D(61.404, 28.041, 1.57),Pose2D(61.433, 28.434, 1.57),Pose2D(61.457, 28.824, 1.57),Pose2D(61.477, 29.211, 1.57),Pose2D(61.493, 29.594, 1.57),Pose2D(61.505, 29.974, 1.57),Pose2D(61.512, 30.351, 1.57),Pose2D(61.516, 30.724, 1.57),Pose2D(61.516, 31.093, 1.57),Pose2D(61.511, 31.459, 1.57),Pose2D(61.504, 31.821, 1.57),Pose2D(61.492, 32.178, 1.57),Pose2D(61.477, 32.532, 1.57),Pose2D(61.458, 32.882, 1.57),Pose2D(61.436, 33.226, 1.57),Pose2D(61.409, 33.566, 1.57),Pose2D(61.379, 33.902, 1.57),Pose2D(61.345, 34.232, 1.57),Pose2D(61.307, 34.558, 1.57),Pose2D(61.267, 34.879, 1.57),Pose2D(61.223, 35.195, 1.57),Pose2D(61.175, 35.506, 1.57),Pose2D(61.125, 35.812, 1.57),Pose2D(61.071, 36.113, 1.57),Pose2D(61.014, 36.409, 1.57),Pose2D(60.955, 36.7, 1.57),Pose2D(60.892, 36.986, 1.57),Pose2D(60.827, 37.266, 1.57),Pose2D(60.759, 37.542, 1.57),Pose2D(60.689, 37.812, 1.57),Pose2D(60.616, 38.077, 1.57),Pose2D(60.54, 38.337, 1.57),Pose2D(60.462, 38.591, 1.57),Pose2D(60.382, 38.841, 1.57),Pose2D(60.3, 39.085, 1.57),Pose2D(60.215, 39.324, 1.57),Pose2D(60.128, 39.557, 1.57),Pose2D(60.09, 39.656, 1.57),Pose2D(60.0, 39.882, 1.57),Pose2D(59.961, 39.978, 1.57),Pose2D(59.869, 40.196, 1.57),Pose2D(59.828, 40.289, 1.57),Pose2D(59.761, 40.441, 1.57),Pose2D(59.664, 40.648, 1.57),Pose2D(59.594, 40.794, 1.57),Pose2D(59.495, 40.993, 1.57),Pose2D(59.422, 41.133, 1.57),Pose2D(59.32, 41.324, 1.57),Pose2D(59.246, 41.458, 1.57),Pose2D(59.141, 41.641, 1.57),Pose2D(59.065, 41.77, 1.57),Pose2D(58.958, 41.945, 1.57),Pose2D(58.88, 42.068, 1.57),Pose2D(58.77, 42.236, 1.57),Pose2D(58.691, 42.354, 1.57),Pose2D(58.579, 42.515, 1.57),Pose2D(58.498, 42.628, 1.57),Pose2D(58.417, 42.739, 1.57),Pose2D(58.302, 42.889, 1.57),Pose2D(58.219, 42.995, 1.57),Pose2D(58.136, 43.099, 1.57),Pose2D(58.018, 43.24, 1.57),Pose2D(57.934, 43.34, 1.57),Pose2D(57.849, 43.437, 1.57),Pose2D(57.763, 43.532, 1.57),Pose2D(57.678, 43.626, 1.57),Pose2D(57.626, 43.681, 1.57),Pose2D(57.504, 43.807, 1.57),Pose2D(57.417, 43.895, 1.57),Pose2D(57.33, 43.981, 1.57),Pose2D(57.242, 44.065, 1.57),Pose2D(57.153, 44.148, 1.57),Pose2D(57.029, 44.26, 1.57),Pose2D(56.94, 44.339, 1.57),Pose2D(56.85, 44.416, 1.57),Pose2D(56.724, 44.521, 1.57),Pose2D(56.634, 44.594, 1.57),Pose2D(56.544, 44.665, 1.57),Pose2D(56.417, 44.762, 1.57),Pose2D(56.326, 44.83, 1.57),Pose2D(56.198, 44.922, 1.57),Pose2D(56.106, 44.986, 1.57),Pose2D(55.977, 45.072, 1.57),Pose2D(55.885, 45.133, 1.57),Pose2D(55.756, 45.215, 1.57),Pose2D(55.664, 45.272, 1.57),Pose2D(55.534, 45.35, 1.57),Pose2D(55.441, 45.404, 1.57),Pose2D(55.311, 45.476, 1.57),Pose2D(55.218, 45.527, 1.57),Pose2D(55.087, 45.595, 1.57),Pose2D(54.994, 45.643, 1.57),Pose2D(54.863, 45.707, 1.57),Pose2D(54.807, 45.734, 1.57),Pose2D(54.676, 45.795, 1.57),Pose2D(54.62, 45.821, 1.57),Pose2D(54.489, 45.878, 1.57),Pose2D(54.432, 45.903, 1.57),Pose2D(54.301, 45.957, 1.57),Pose2D(54.245, 45.98, 1.57),Pose2D(54.114, 46.031, 1.57),Pose2D(54.057, 46.052, 1.57),Pose2D(53.926, 46.1, 1.57),Pose2D(53.87, 46.12, 1.57),Pose2D(53.739, 46.165, 1.57),Pose2D(53.683, 46.184, 1.57),Pose2D(53.552, 46.226, 1.57),Pose2D(53.496, 46.244, 1.57),Pose2D(53.365, 46.283, 1.57),Pose2D(53.309, 46.3, 1.57),Pose2D(53.179, 46.336, 1.57),Pose2D(53.123, 46.352, 1.57),Pose2D(52.992, 46.385, 1.57),Pose2D(52.937, 46.4, 1.57),Pose2D(52.807, 46.431, 1.57),Pose2D(52.751, 46.444, 1.57),Pose2D(52.621, 46.473, 1.57),Pose2D(52.566, 46.485, 1.57),Pose2D(52.437, 46.511, 1.57),Pose2D(52.381, 46.522, 1.57),Pose2D(52.252, 46.546, 1.57),Pose2D(52.197, 46.556, 1.57),Pose2D(52.069, 46.578, 1.57),Pose2D(52.014, 46.587, 1.57),Pose2D(51.886, 46.606, 1.57),Pose2D(51.831, 46.614, 1.57),Pose2D(51.704, 46.631, 1.57),Pose2D(51.649, 46.639, 1.57),Pose2D(51.522, 46.654, 1.57),Pose2D(51.468, 46.66, 1.57),Pose2D(51.342, 46.673, 1.57),Pose2D(51.287, 46.679, 1.57),Pose2D(51.162, 46.69, 1.57),Pose2D(51.108, 46.694, 1.57),Pose2D(50.983, 46.703, 1.57),Pose2D(50.929, 46.707, 1.57),Pose2D(50.805, 46.715, 1.57),Pose2D(50.751, 46.718, 1.57),Pose2D(50.628, 46.723, 1.57),Pose2D(50.575, 46.725, 1.57),Pose2D(50.452, 46.729, 1.57),Pose2D(50.399, 46.731, 1.57),Pose2D(50.277, 46.733, 1.57),Pose2D(50.224, 46.734, 1.57),Pose2D(50.102, 46.734, 1.57),Pose2D(50.05, 46.734, 1.57),Pose2D(49.929, 46.733, 1.57),Pose2D(49.878, 46.733, 1.57),Pose2D(49.758, 46.73, 1.57),Pose2D(49.706, 46.729, 1.57),Pose2D(49.587, 46.725, 1.57),Pose2D(49.536, 46.723, 1.57),Pose2D(49.417, 46.717, 1.57),Pose2D(49.366, 46.715, 1.57),Pose2D(49.249, 46.708, 1.57),Pose2D(49.198, 46.705, 1.57),Pose2D(49.081, 46.697, 1.57),Pose2D(49.031, 46.693, 1.57),Pose2D(48.915, 46.684, 1.57),Pose2D(48.866, 46.68, 1.57),Pose2D(48.75, 46.669, 1.57),Pose2D(48.701, 46.665, 1.57),Pose2D(48.587, 46.653, 1.57),Pose2D(48.538, 46.647, 1.57),Pose2D(48.425, 46.635, 1.57),Pose2D(48.376, 46.629, 1.57),Pose2D(48.264, 46.615, 1.57),Pose2D(48.215, 46.609, 1.57),Pose2D(48.104, 46.594, 1.57),Pose2D(48.056, 46.587, 1.57),Pose2D(47.945, 46.571, 1.57),Pose2D(47.898, 46.564, 1.57),Pose2D(47.788, 46.547, 1.57),Pose2D(47.741, 46.539, 1.57),Pose2D(47.633, 46.521, 1.57),Pose2D(47.586, 46.513, 1.57),Pose2D(47.478, 46.494, 1.57),Pose2D(47.432, 46.486, 1.57),Pose2D(47.325, 46.466, 1.57),Pose2D(47.279, 46.458, 1.57),Pose2D(47.174, 46.437, 1.57),Pose2D(47.128, 46.428, 1.57),Pose2D(47.023, 46.406, 1.57),Pose2D(46.979, 46.397, 1.57),Pose2D(46.875, 46.375, 1.57),Pose2D(46.83, 46.365, 1.57),Pose2D(46.727, 46.342, 1.57),Pose2D(46.683, 46.332, 1.57),Pose2D(46.581, 46.309, 1.57),Pose2D(46.538, 46.298, 1.57),Pose2D(46.437, 46.274, 1.57),Pose2D(46.393, 46.263, 1.57),Pose2D(46.294, 46.238, 1.57),Pose2D(46.251, 46.227, 1.57),Pose2D(46.152, 46.202, 1.57),Pose2D(46.109, 46.191, 1.57),Pose2D(46.011, 46.165, 1.57),Pose2D(45.97, 46.153, 1.57),Pose2D(45.873, 46.126, 1.57),Pose2D(45.831, 46.115, 1.57),Pose2D(45.735, 46.088, 1.57),Pose2D(45.694, 46.076, 1.57),Pose2D(45.599, 46.048, 1.57),Pose2D(45.559, 46.036, 1.57),Pose2D(45.465, 46.008, 1.57),Pose2D(45.425, 45.996, 1.57),Pose2D(45.332, 45.967, 1.57),Pose2D(45.266, 45.946, 1.57),Pose2D(45.201, 45.925, 1.57),Pose2D(45.162, 45.912, 1.57),Pose2D(45.072, 45.881, 1.57),Pose2D(45.034, 45.867, 1.57),Pose2D(44.946, 45.835, 1.57),Pose2D(44.909, 45.821, 1.57),Pose2D(44.823, 45.788, 1.57),Pose2D(44.786, 45.774, 1.57),Pose2D(44.701, 45.74, 1.57),Pose2D(44.665, 45.726, 1.57),Pose2D(44.582, 45.691, 1.57),Pose2D(44.547, 45.676, 1.57),Pose2D(44.465, 45.64, 1.57),Pose2D(44.43, 45.625, 1.57),Pose2D(44.373, 45.599, 1.57),Pose2D(44.316, 45.573, 1.57),Pose2D(44.26, 45.546, 1.57),Pose2D(44.226, 45.53, 1.57),Pose2D(44.149, 45.492, 1.57),Pose2D(44.116, 45.476, 1.57),Pose2D(44.062, 45.449, 1.57),Pose2D(44.03, 45.432, 1.57),Pose2D(43.955, 45.393, 1.57),Pose2D(43.923, 45.376, 1.57),Pose2D(43.871, 45.348, 1.57),Pose2D(43.84, 45.331, 1.57),Pose2D(43.768, 45.29, 1.57),Pose2D(43.737, 45.273, 1.57),Pose2D(43.686, 45.244, 1.57),Pose2D(43.656, 45.226, 1.57),Pose2D(43.587, 45.185, 1.57),Pose2D(43.557, 45.167, 1.57),Pose2D(43.508, 45.138, 1.57),Pose2D(43.479, 45.12, 1.57),Pose2D(43.412, 45.078, 1.57),Pose2D(43.384, 45.059, 1.57),Pose2D(43.336, 45.029, 1.57),Pose2D(43.308, 45.011, 1.57),Pose2D(43.244, 44.968, 1.57),Pose2D(43.216, 44.949, 1.57),Pose2D(43.171, 44.918, 1.57),Pose2D(43.144, 44.899, 1.57),Pose2D(43.082, 44.856, 1.57),Pose2D(43.055, 44.837, 1.57),Pose2D(43.011, 44.805, 1.57),Pose2D(42.985, 44.787, 1.57),Pose2D(42.925, 44.742, 1.57),Pose2D(42.899, 44.723, 1.57),Pose2D(42.857, 44.691, 1.57),Pose2D(42.832, 44.672, 1.57),Pose2D(42.774, 44.627, 1.57),Pose2D(42.75, 44.608, 1.57),Pose2D(42.709, 44.575, 1.57),Pose2D(42.685, 44.556, 1.57),Pose2D(42.653, 44.53, 1.57),Pose2D(42.621, 44.504, 1.57),Pose2D(42.574, 44.465, 1.57),Pose2D(42.543, 44.439, 1.57),Pose2D(42.512, 44.413, 1.57),Pose2D(42.482, 44.387, 1.57),Pose2D(42.451, 44.36, 1.57),Pose2D(42.429, 44.34, 1.57),Pose2D(42.377, 44.294, 1.57),Pose2D(42.355, 44.275, 1.57),Pose2D(42.318, 44.242, 1.57),Pose2D(42.296, 44.222, 1.57),Pose2D(42.268, 44.195, 1.57),Pose2D(42.239, 44.169, 1.57),Pose2D(42.211, 44.142, 1.57),Pose2D(42.19, 44.122, 1.57),Pose2D(42.142, 44.076, 1.57),Pose2D(42.121, 44.056, 1.57),Pose2D(42.087, 44.022, 1.57),Pose2D(42.067, 44.002, 1.57),Pose2D(42.033, 43.969, 1.57),Pose2D(42.014, 43.949, 1.57),Pose2D(41.987, 43.922, 1.57),Pose2D(41.961, 43.895, 1.57),Pose2D(41.935, 43.868, 1.57),Pose2D(41.916, 43.848, 1.57),Pose2D(41.872, 43.801, 1.57),Pose2D(41.853, 43.781, 1.57),Pose2D(41.822, 43.748, 1.57),Pose2D(41.804, 43.728, 1.57),Pose2D(41.773, 43.694, 1.57),Pose2D(41.755, 43.674, 1.57),Pose2D(41.725, 43.641, 1.57),Pose2D(41.707, 43.62, 1.57),Pose2D(41.678, 43.587, 1.57),Pose2D(41.66, 43.567, 1.57),Pose2D(41.632, 43.533, 1.57),Pose2D(41.614, 43.513, 1.57),Pose2D(41.586, 43.48, 1.57),Pose2D(41.569, 43.46, 1.57),Pose2D(41.541, 43.426, 1.57),Pose2D(41.525, 43.406, 1.57),Pose2D(41.503, 43.379, 1.57),Pose2D(41.481, 43.353, 1.57),Pose2D(41.46, 43.326, 1.57),Pose2D(41.444, 43.306, 1.57),Pose2D(41.407, 43.259, 1.57),Pose2D(41.391, 43.239, 1.57),Pose2D(41.366, 43.206, 1.57),Pose2D(41.35, 43.186, 1.57),Pose2D(41.33, 43.159, 1.57),Pose2D(41.31, 43.133, 1.57),Pose2D(41.29, 43.106, 1.57),Pose2D(41.275, 43.086, 1.57),Pose2D(41.241, 43.04, 1.57),Pose2D(41.227, 43.02, 1.57),Pose2D(41.203, 42.987, 1.57),Pose2D(41.189, 42.968, 1.57),Pose2D(41.17, 42.941, 1.57),Pose2D(41.152, 42.915, 1.57),Pose2D(41.133, 42.889, 1.57),Pose2D(41.12, 42.869, 1.57),Pose2D(41.088, 42.823, 1.57),Pose2D(41.075, 42.803, 1.57),Pose2D(41.053, 42.771, 1.57),Pose2D(41.04, 42.751, 1.57),Pose2D(41.023, 42.725, 1.57),Pose2D(41.005, 42.699, 1.57),Pose2D(40.98, 42.66, 1.57),Pose2D(40.968, 42.641, 1.57),Pose2D(40.947, 42.608, 1.57),Pose2D(40.935, 42.589, 1.57),Pose2D(40.918, 42.563, 1.57),Pose2D(40.902, 42.537, 1.57),Pose2D(40.879, 42.499, 1.57),Pose2D(40.867, 42.48, 1.57),Pose2D(40.847, 42.448, 1.57),Pose2D(40.836, 42.429, 1.57),Pose2D(40.821, 42.403, 1.57),Pose2D(40.809, 42.384, 1.57),Pose2D(40.794, 42.359, 1.57),Pose2D(40.772, 42.321, 1.57),Pose2D(40.761, 42.302, 1.57),Pose2D(40.743, 42.27, 1.57),Pose2D(40.732, 42.251, 1.57),Pose2D(40.718, 42.226, 1.57),Pose2D(40.707, 42.207, 1.57),Pose2D(40.694, 42.182, 1.57),Pose2D(40.673, 42.145, 1.57),Pose2D(40.663, 42.126, 1.57),Pose2D(40.646, 42.095, 1.57),Pose2D(40.636, 42.076, 1.57),Pose2D(40.626, 42.058, 1.57),Pose2D(40.603, 42.015, 1.57),Pose2D(40.594, 41.996, 1.57),Pose2D(40.581, 41.971, 1.57),Pose2D(40.568, 41.947, 1.57),Pose2D(40.559, 41.929, 1.57),Pose2D(40.54, 41.892, 1.57),Pose2D(40.54, 41.892, 1.57),Pose2D(40.54, 41.892, 1.57),Pose2D(40.54, 41.892, 1.57)
        )[(Globals.currentTime * 50).toInt()]
        set(value) = pinpoint.setStart(value)
    val velocity: Pose2D
        get() = pinpoint.velocity

    val robotCentricVelocity: Pose2D
        get() = velocity rotatedBy -position.heading

    var gvfPaths = arrayListOf<Path>()
    private var poseHistory = Array(1000) { Pose2D() }

    init {
        motors.forEach {
            it.useInternalEncoder(384.5, millimeters(104))
            it.setZeroPowerBehavior(FLOAT)
        }
    }

    fun resetPoseHistory() {
        poseHistory = Array(1000) { Pose2D() }
    }

    override fun enable()  { pinpoint.priority = pinpointPriority }
    override fun disable() { pinpoint.priority = 0.0              }

    override fun update(deltaTime: Double) {
        controllers.forEach { it.updateError(deltaTime) }

        log("position") value position.asAkitPose()
    }
    fun resetToCorner(next: Command) = (
        InstantCommand {
            pinpoint.hardwareDevice.resetPosAndIMU()
            position = cornerPos
        }
        andThen WaitCommand(0.5)
        andThen InstantCommand { next.schedule() }
    )

    fun driveFieldCentric(
        power: Pose2D,
        feedForward: Double = 0.0,
        comp: Boolean = false
    ){
        val pose = power.vector.rotatedBy( -position.heading ) + power.heading
        setWeightedDrivePower(
            drive = pose.x,
            strafe = -pose.y,
            turn = pose.heading.toDouble(),
            feedForward = feedForward,
            comp = comp
        )
    }
    fun fieldCentricPowers(
        powers: List<Pose2D>,
        feedForward: Double,
        comp: Boolean
    ){
        var current = Pose2D()
        for(element in powers){
            var power = element
            power = ( power rotatedBy -position.heading )
            val next = current + power
            val maxPower = (
                  abs(next.x)
                + abs(next.y)
                + abs(next.heading.toDouble())
                + feedForward
            )

            if (maxPower > 1) {
                // Compute scale factor to normalize max wheel power to 1
                val scale = (
//                    (1 - feedForward)
//                    / (
//                          abs(power.x)
//                        + abs(power.y)
//                        + abs(power.heading.toDouble())
//                    )
                    1
                )

                if (scale > 0) {
                    current += Pose2D(
                        power.x * scale,
                        power.y * scale,
                        power.heading.toDouble() * scale
                    )
                }
                break
            } else {
                current = next
            }
        }
        setWeightedDrivePower(
            current.x,
            -current.y,
            current.heading.toDouble(),
            feedForward,
            comp
        )
    }
    fun resetHeading() {
        pinpoint.resetHeading()
    }

    override fun reset() {
        super.reset()
        pinpoint.resetInternals()
        headingController.targetPosition = position.heading.toDouble()
        controllers.forEach { it.resetController() }

        ensurePinpointSetup()
    }

    fun ensurePinpointSetup() {
        if(!pinpointSetup) {
            pinpoint.apply {
                xEncoderOffset = -36.0 // mm
                yEncoderOffset = -70.0 // mm
                podType = goBILDA_SWINGARM_POD
                xEncoderDirection = FORWARD
                yEncoderDirection = REVERSE
            }
            pinpointSetup = true
        }
    }

    fun setWeightedDrivePower(
        drive: Double = 0.0,
        strafe: Double = 0.0,
        turn: Double = 0.0,
        feedForward: Double = 0.0,
        comp: Boolean = false
    ) {
        var flPower = drive + strafe * 1.1 - turn
        var frPower = drive - strafe * 1.1 + turn
        var brPower = drive + strafe * 1.1 + turn
        var blPower = drive - strafe * 1.1 - turn
        flPower += feedForward * flPower.sign
        frPower += feedForward * frPower.sign
        brPower += feedForward * brPower.sign
        blPower += feedForward * blPower.sign
        val max = maxOf(flPower, frPower, brPower, blPower)
        if (max > 1 + 1e-4) {

            flPower /= max
            frPower /= max
            blPower /= max
            brPower /= max
        }
        if(comp){
            frontLeft .compPower( flPower )
            frontRight.compPower( frPower )
            backLeft  .compPower( blPower )
            backRight .compPower( brPower )
        } else {
            frontLeft .power = flPower
            frontRight.power = frPower
            backRight .power = brPower
            backLeft  .power = blPower
        }
    }

    val xVelocityController = PIDFController(
        P = 0.005,
        D = 0.0,
        setpointError = { - robotCentricVelocity.x },
        apply = { },
        pos = { 0.0 }
    )
    val yVelocityController = PIDFController(
        P = 0.005,
        D = 0.0,
        setpointError = { robotCentricVelocity.y },
        apply = { },
        pos = { 0.0 }
    )
    val headingController = PIDFController(
        P = { HEADING_P },
        D = { HEADING_D },
        setpointError = {
            arrayListOf(
                targetPosition - position.heading.toDouble(),
                targetPosition - position.heading.toDouble() + 2*PI,
                targetPosition - position.heading.toDouble() - 2*PI,
            ).minBy { abs(it) } // smallest absolute value with wraparound
        },
        apply = { },
        pos = { 0.0 }
    )
    private val controllers = arrayListOf(
        xVelocityController,
        yVelocityController,
        headingController
    )
}
