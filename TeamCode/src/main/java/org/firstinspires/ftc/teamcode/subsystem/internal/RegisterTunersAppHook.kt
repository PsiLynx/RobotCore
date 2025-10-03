package org.firstinspires.ftc.teamcode.subsystem.internal

/*
import com.qualcomm.robotcore.eventloop.opmode.AnnotatedOpModeManager
import dev.frozenmilk.sinister.SinisterFilter
import dev.frozenmilk.sinister.apphooks.OpModeRegistrar
import dev.frozenmilk.sinister.targeting.TeamCodeSearch
import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.controller.State
import org.firstinspires.ftc.teamcode.controller.State.DoubleState
import org.firstinspires.ftc.teamcode.controller.pid.PIDFController
import org.firstinspires.ftc.teamcode.controller.pid.TunablePIDF
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.typeOf

@Suppress("Unused")
object RegisterTunersAppHook: OpModeRegistrar, SinisterFilter {
    var tunables = mutableMapOf<String, Tunable<*>>()
    override fun registerOpModes(opModeManager: AnnotatedOpModeManager) {
        val opModeMetaConstructor = OpModeMeta::class.constructors.first()
        opModeMetaConstructor.isAccessible = true
        tunables.forEach { name, value ->
            opModeManager.register(
                opModeMetaConstructor.call(
                    OpModeMeta.Flavor.TELEOP,
                    "tuning",
                    "Tune $name",
                    "Tune $name",
                    "",
                    OpModeMeta.Source.BLOCKLY
                ),
                object : CommandOpMode () {
                    override fun initialize() {
                        var max = value.tuningForward
                        var min = value.tuningBack

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
                            dpadUp.  onTrue( value.tuningCommand(max) )
                            dpadDown.onTrue( value.tuningCommand(min) )
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
            tunables[tunable::class.simpleName!!] = tunable
        }
        clazz.kotlin.members.forEach { member ->
            val annotation = member.findAnnotation<TunablePIDF>()
            if(
                annotation != null
                && member.returnType.isSubtypeOf(typeOf<PIDFController>())
            ){
                tunables[clazz::class.simpleName + " " + member.name] = object :
                    Tunable<DoubleState> {
                    override val tuningForward = DoubleState(
                        annotation.max
                    )
                    override val tuningBack = DoubleState(
                        annotation.min
                    )
                    override val tuningCommand = { target: State<*> ->
                        object : Subsystem<Subsystem.DummySubsystem>() {
                            val controller = member as PIDFController
                            override val components = listOf<Component>()

                            override fun update(deltaTime: Double) {
                                controller.targetPosition = (
                                    target as DoubleState
                                ).value
                                controller.updateController(deltaTime)
                            }
                        }.justUpdate()
                    }
                }
            }
        }
    }

}
 */