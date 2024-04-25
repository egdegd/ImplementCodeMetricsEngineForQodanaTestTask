import java.io.File

fun main() {

    println("Enter the name of the file:")
    val fileName = readlnOrNull()?.trim()

    if (fileName.isNullOrBlank()) {
        println("Invalid file name.")
        return
    }
    val file = File(fileName)

    if (!file.exists()) {
        println("File not found.")
        return
    }
    println("Enter the type of metric (1 for number of conditional statements, 2 for maximum depth of conditional statements):")
    val metricType = readlnOrNull()?.toIntOrNull()

    if (metricType !in 1..2) {
        println("Invalid metric type.")
        return
    }

    try {
        val visitor = KotlinFileVisitor()
        visitor.visitFile(fileName)
        when (metricType) {
            1 -> {
                val sortedFunctionComplexityCount =
                    visitor.getFunctionComplexityCondCount().entries.sortedByDescending { it.value }
                println("Top 3 methods/functions with the highest complexity scores:")
                for (i in 0..<minOf(3, sortedFunctionComplexityCount.size)) {
                    println("${sortedFunctionComplexityCount[i].key}: ${sortedFunctionComplexityCount[i].value}")
                }
            }

            2 -> {
                val sortedFunctionComplexityDepth =
                    visitor.getFunctionComplexityCondDepth().entries.sortedByDescending { it.value }
                println("Top 3 methods/functions with the highest complexity scores:")
                for (i in 0..<minOf(3, sortedFunctionComplexityDepth.size)) {
                    println("${sortedFunctionComplexityDepth[i].key}: ${sortedFunctionComplexityDepth[i].value}")
                }
            }
        }
    } catch (e: Exception) {
        println("This file can't be parsed.")
    }
}
