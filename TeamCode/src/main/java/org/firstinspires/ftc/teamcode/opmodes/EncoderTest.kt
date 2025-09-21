package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import org.psilynx.psikit.core.Logger
import org.psilynx.psikit.core.rlog.RLOGServer
import org.psilynx.psikit.ftc.PsiKitOpMode
import org.psilynx.psikit.ftc.wrappers.MotorWrapper

@TeleOp(name = "encoder test")
class EncoderTest: PsiKitOpMode() {
    override fun runOpMode() {
        psiKitSetup()
        val server = RLOGServer()
        server.start()
        Logger.addDataReceiver(server)
        Logger.start()

        val device = this.hardwareMap.get(DcMotor::class.java, "m1")
        while (!isStopRequested){
            Logger.periodicBeforeUser()
            processHardwareInputs()

            this.telemetry.addData("ticks", device.currentPosition)
            this.telemetry.update()
            println(device.currentPosition)

            Logger.periodicAfterUser(0.0, 0.0)
        }
        server.end()
    }
}