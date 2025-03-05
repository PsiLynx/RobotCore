package org.teamcode.util.graph

const val charsInLine = 78.0

class Graph(
    private vararg var functions: Function,
    val min: Double,
    val max: Double,
    ) {

    private val range = max - min

    fun printLine(){
        val pointsToGraph = Array(
            size = functions.size,
            init = { i -> (functions[i].output.asDouble - min ) / range * charsInLine}
        )

        for(i in 0..charsInLine.toInt()){

            var j = 0
            while (true){
                if(j >= pointsToGraph.size) { print(" "); break }

                if( pointsToGraph[j].toInt() == i ){
                    print(functions[j].char)
                    break
                }
                j ++
            }
        }
        println()
    }
}
