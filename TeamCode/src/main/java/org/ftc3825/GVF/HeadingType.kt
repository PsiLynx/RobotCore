package org.ftc3825.GVF

sealed interface HeadingType {
    data class Constant(val theta: Double): HeadingType
    data class Linear(val theta1: Double, val theta2: Double): HeadingType
    class Tangent: HeadingType
    companion object{
        fun tangent() = Tangent()
        fun constant(theta: Double) = Constant(theta)
        fun linear(theta1: Double, theta2: Double) = Linear(theta1, theta2)
    }
}
