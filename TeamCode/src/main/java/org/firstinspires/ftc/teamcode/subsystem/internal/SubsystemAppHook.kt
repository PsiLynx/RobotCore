package org.firstinspires.ftc.teamcode.subsystem.internal

import com.qualcomm.robotcore.eventloop.opmode.AnnotatedOpModeManager
import dev.frozenmilk.sinister.SinisterFilter
import dev.frozenmilk.sinister.apphooks.OpModeRegistrar
import dev.frozenmilk.sinister.targeting.TeamCodeSearch
import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.controller.State
import kotlin.reflect.jvm.isAccessible

@Suppress("Unused")
object SubsystemAppHook: OpModeRegistrar, SinisterFilter {
    var subsystems = mutableListOf<Tunable<*>>()
    override fun registerOpModes(opModeManager: AnnotatedOpModeManager) {
        val opModeMetaConstructor = OpModeMeta::class.constructors.first()
        opModeMetaConstructor.isAccessible = true
        subsystems.forEach { it ->
            opModeManager.register(
                opModeMetaConstructor.call(
                    OpModeMeta.Flavor.TELEOP,
                    "tuning",
                    "Tune " + it::class.simpleName,
                    "Tune " + it::class.simpleName,
                    "",
                    OpModeMeta.Source.BLOCKLY
                ),
                object : CommandOpMode () {
                    override fun initialize() {
                        var max = it.tuningForward
                        var min = it.tuningBack

                        driver.apply {
                            if(a.isTriggered){
                                max /= 2
                                min /= 2
                            }
                            else if(b.isTriggered){
                                max /= 4
                                min /= 4
                            }
                            else if(y.isTriggered){
                                max /= 6
                                min /= 6
                            }
                            else if(x.isTriggered){
                                max /= 8
                                min /= 8
                            }
                            dpadUp.  onTrue( it.tuningCommand(max) )
                            dpadDown.onTrue( it.tuningCommand(min) )
                        }
                    }
                }
            )
        }
    }

    override val targets = TeamCodeSearch()

    override fun filter(clazz: Class<*>) {
        if(Tunable::class.java.isAssignableFrom(clazz)){
            var tunable
                = clazz.kotlin.objectInstance as Tunable<*>?
            if(tunable == null){
               tunable = clazz.newInstance() as Tunable<*>
            }
            subsystems.add(tunable)
        }
    }

}