package org.firstinspires.ftc.teamcode.controller

interface Controller <S: State<S>, R> {
    val feedback: R
}