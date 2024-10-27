package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.fakehardware.FakeHardwareMap
import org.ftc3825.util.Slides
import kotlin.random.Random
import com.qualcomm.hardware.lynx.LynxModule

@Disabled
abstract class CommandOpMode: OpMode() {

    lateinit var allHubs: List<LynxModule>

    fun initialize() {
        allHubs = hardwareMap.getAll(LynxModule::class.java)

        GlobalHardwareMap.init(hardwareMap)
        CommandScheduler.init(hardwareMap)
        allHubs.forEach { it ->
            it.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL) 
        }
    }

    override fun loop() {
        allHubs.forEach { it -> it.clearBulkCache() }
        CommandScheduler.update()
    }

    override fun stop() {
        CommandScheduler.end()
    }
}
