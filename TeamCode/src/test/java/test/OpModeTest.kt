package test

import org.ftc3825.opmodes.Auto
import org.ftc3825.subsystem.Localizer
import org.ftc3825.util.OpModeRunner
import org.ftc3825.util.isWithin
import org.ftc3825.util.of
import org.ftc3825.util.TestClass
import org.junit.Test

class OpModeTest: TestClass(){
    @Test fun testAuto() = OpModeRunner(
        Auto(),
        assertAfterExecute = { opMode ->
            opMode as Auto
            (
                Localizer.position.x isWithin 0.5 of 66
                 and (Localizer.position.y isWithin 0.5 of -65)
            )
        }
    ).run()
}
