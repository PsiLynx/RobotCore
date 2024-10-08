package org.ftc3825.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap
import org.ftc3825.component.Encoder
import org.ftc3825.component.Motor
import org.ftc3825.util.Pose2D
import org.ftc3825.util.millimeters
import kotlin.math.PI

object LocalizerSubsystem: Subsystem<LocalizerSubsystem>{
    override var initialized = false

    private val par1YTicks = 2329.2
    private val par2YTicks = -2329.2
    private val perpXTicks = 1526.77
    private val ticksPerRev = 2000.0
    private val inPerTick = millimeters(48) * PI / 2.54 / 2000.0

    override val motors = arrayListOf<Motor>()
    lateinit var encoders: ArrayList<Encoder>

    var position = Pose2D()
    
    override fun init(hardwareMap: HardwareMap){
        if(!initialized){
            encoders = arrayListOf(
                Encoder(Drivetrain.motors[0].motor, ticksPerRev),
                Encoder(Drivetrain.motors[1].motor, ticksPerRev),
                Encoder(Drivetrain.motors[3].motor, ticksPerRev)
            )
        }
    }

    override fun update(deltaTime: Double){
        encoders.forEach { it.update() }

        var par1 = encoders[0]
        var perp = encoders[1]
        var par2 = encoders[3]

        val deltaX = (
                (par1YTicks * par1.delta - par2YTicks * par1.delta)
                / (par1YTicks - par2YTicks)
        ) * inPerTick
        val deltaY = (
                perpXTicks / (par1YTicks - par2YTicks)
                * (par2.delta - par1.delta)
                + perp.delta
            ) * inPerTick

        val deltaR = (par1.delta - par2.delta) / (par1YTicks - par2YTicks)

        position.applyToEnd(Pose2D(deltaX, deltaY, deltaR))
    }

}
