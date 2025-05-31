package test

import org.firstinspires.ftc.teamcode.akit.LogTable
import org.firstinspires.ftc.teamcode.akit.Logger
import org.firstinspires.ftc.teamcode.akit.RLOGServer
import org.junit.Test
import java.lang.Thread.sleep
import kotlin.random.Random

class AKitTest {
    @Test fun runLogger(){
        val server = RLOGServer()
        Logger.addDataReceiver(server)
        Logger.start()
        Logger.periodicAfterUser(0, 0)
        while (true){
            Logger.periodicBeforeUser()
            Logger.recordOutput("test", Random.nextDouble())
            Logger.periodicAfterUser(0L, 0L)
            sleep(20L)
        }
    }
}