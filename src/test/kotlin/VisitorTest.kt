import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.assertEquals

class VisitorTest {
    @Test
    fun ifTest(@TempDir tempDir: File) {
        val tempFile = File(tempDir, "test.kt")

        val kotlinCode = """
            fun main() {
                val x = 10
                if (x == 10) {
                    println("Hello")
                } else {
                    println("By")
                }
            }
        """.trimIndent()
        tempFile.writeText(kotlinCode)

        assertTrue(tempFile.exists())
        val testedVisitor = KotlinFileVisitor()

        testedVisitor.visitFile(tempFile.absolutePath)
        assertEquals(testedVisitor.getFunctionComplexityCondCount()["main"], 1)
        assertEquals(testedVisitor.getFunctionComplexityCondDepth()["main"], 1)

    }
    @Test
    fun forTest(@TempDir tempDir: File) {
        val tempFile = File(tempDir, "test.kt")

        val kotlinCode = """
            fun main() {
                for (i in 1..10) {
                    println(i.toString())
                }
            }
        """.trimIndent()
        tempFile.writeText(kotlinCode)

        assertTrue(tempFile.exists())
        val testedVisitor = KotlinFileVisitor()

        testedVisitor.visitFile(tempFile.absolutePath)
        assertEquals(testedVisitor.getFunctionComplexityCondCount()["main"], 1)
        assertEquals(testedVisitor.getFunctionComplexityCondDepth()["main"], 1)

    }
    @Test
    fun whileTest(@TempDir tempDir: File) {
        val tempFile = File(tempDir, "test.kt")

        val kotlinCode = """
            fun main() {
                val x = 5
                while (x > 0) {
                    x -= 1
                }
            }
        """.trimIndent()
        tempFile.writeText(kotlinCode)

        assertTrue(tempFile.exists())
        val testedVisitor = KotlinFileVisitor()

        testedVisitor.visitFile(tempFile.absolutePath)
        assertEquals(testedVisitor.getFunctionComplexityCondCount()["main"], 1)
        assertEquals(testedVisitor.getFunctionComplexityCondDepth()["main"], 1)

    }
    @Test
    fun whenTest(@TempDir tempDir: File) {
        val tempFile = File(tempDir, "test.kt")

        val kotlinCode = """
            fun main() {
                println("Enter a number:")
                val number = readLine()?.toIntOrNull()
                val result = when (number) {
                    0 -> "Zero"
                    1 -> "One"
                    2 -> "Two"
                    3 -> "Three"
                    4 -> "Four"
                    5 -> "Five"
                    else -> "Unknown"
                }
            }
        """.trimIndent()
        tempFile.writeText(kotlinCode)

        assertTrue(tempFile.exists())
        val testedVisitor = KotlinFileVisitor()

        testedVisitor.visitFile(tempFile.absolutePath)
        assertEquals(testedVisitor.getFunctionComplexityCondCount()["main"], 1)
        assertEquals(testedVisitor.getFunctionComplexityCondDepth()["main"], 1)
    }
    @Test
    fun doWhileTest(@TempDir tempDir: File) {
        val tempFile = File(tempDir, "test.kt")

        val kotlinCode = """
            fun main() {
                do {
                    val y = retrieveData()
                } while (y != null) // y is visible here!
            }
        """.trimIndent()
        tempFile.writeText(kotlinCode)

        assertTrue(tempFile.exists())
        val testedVisitor = KotlinFileVisitor()

        testedVisitor.visitFile(tempFile.absolutePath)
        assertEquals(testedVisitor.getFunctionComplexityCondCount()["main"], 1)
        assertEquals(testedVisitor.getFunctionComplexityCondDepth()["main"], 1)

    }
    @Test
    fun oneFunctionTest(@TempDir tempDir: File) {
        val tempFile = File(tempDir, "test.kt")

        val kotlinCode = """
            fun main() {
                val x = 10
                if (x == 10) {
                    println("Hello")
                    if( x == 1) {
                        println("AA")
                    }
                    for (i in 1..10) {
                        println(i)
                    }
                }
                val myLambda: (Int) -> String = { num ->
                    if (num % 2 == 0) {
                        "Even"
                    } else {
                        "Odd"
                    }
                }
                val y = 5
                while (y > 0) {
                    y -= 1
                }
            }
        """.trimIndent()
        tempFile.writeText(kotlinCode)

        assertTrue(tempFile.exists())
        val testedVisitor = KotlinFileVisitor()

        testedVisitor.visitFile(tempFile.absolutePath)
        assertEquals(testedVisitor.getFunctionComplexityCondCount()["main"], 5)
        assertEquals(testedVisitor.getFunctionComplexityCondDepth()["main"], 2)

    }
    @Test
    fun oneClassTest(@TempDir tempDir: File) {
        val tempFile = File(tempDir, "test.kt")

        val kotlinCode = """
            class MyClass {
                fun myMethod() {
                    var t = 0
                    when (t) {
                        0 -> t += 1
                        1 -> t += 2
                    }
                    println("This is my method")
                }
            }
        """.trimIndent()
        tempFile.writeText(kotlinCode)

        assertTrue(tempFile.exists())
        val testedVisitor = KotlinFileVisitor()

        testedVisitor.visitFile(tempFile.absolutePath)
        assertEquals(testedVisitor.getFunctionComplexityCondCount()["myMethod"], 1)
        assertEquals(testedVisitor.getFunctionComplexityCondDepth()["myMethod"], 1)

    }
    @Test
    fun combinedTest(@TempDir tempDir: File) {
        val tempFile = File(tempDir, "test.kt")

        val kotlinCode = """
            import java.sql.DriverManager.println
            
            class MyClass {
                fun myMethod() {
                    var t = 0
                    when (t) {
                        0 -> t += 1
                        1 -> t += 2
                        // Add more cases as needed
                    }
                    println("This is my method")
                }
            }
            
            fun nestedFunction() {
                val number = 10
            
                if (number > 0) {
                    println("First level if")
                    
                    if (number % 2 == 0) {
                        println("Second level if")
                        
                        if (number > 5) {
                            println("Third level if")
                            
                            if (number % 3 == 0) {
                                println("Fourth level if")
                                
                                if (number < 15) {
                                    println("Fifth level if")
                                    
                                    for (i in 1..3) {
                                        println("Inside for loop: i")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            fun main() {
                val x = 10
                if (x == 10) {
                    println("Hello")
                    if( x == 1) {
                        println("AA")
                    }
                    for (i in 1..10) {
                        println(i.toString())
                    }
                }
                val myLambda: (Int) -> String = { num ->
                    if (num % 2 == 0) {
                        "Even"
                    } else {
                        "Odd"
                    }
                }
                var y = 5
                while (y > 0) {
                    y -= 1
                }
            }
        """.trimIndent()
        tempFile.writeText(kotlinCode)

        assertTrue(tempFile.exists())
        val testedVisitor = KotlinFileVisitor()

        testedVisitor.visitFile(tempFile.absolutePath)
        assertEquals(testedVisitor.getFunctionComplexityCondCount()["main"], 5)
        assertEquals(testedVisitor.getFunctionComplexityCondDepth()["main"], 2)
        assertEquals(testedVisitor.getFunctionComplexityCondCount()["myMethod"], 1)
        assertEquals(testedVisitor.getFunctionComplexityCondDepth()["myMethod"], 1)
        assertEquals(testedVisitor.getFunctionComplexityCondCount()["nestedFunction"], 6)
        assertEquals(testedVisitor.getFunctionComplexityCondDepth()["nestedFunction"], 6)

    }
}