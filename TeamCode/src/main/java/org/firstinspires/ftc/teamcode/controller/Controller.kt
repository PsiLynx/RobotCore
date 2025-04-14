package org.firstinspires.ftc.teamcode.controller

import java.util.function.Supplier

abstract class Controller <S, R> (state: Supplier<State<S>>) {
    abstract fun apply(): R
}