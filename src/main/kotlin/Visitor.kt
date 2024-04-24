import kotlinx.ast.common.AstSource
import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.ast.AstNode
import kotlinx.ast.common.ast.AstTerminal
import kotlinx.ast.grammar.kotlin.target.antlr.kotlin.KotlinGrammarAntlrKotlinParser

class KotlinFileVisitor {
    private val functionComplexityCondCount = mutableMapOf<String, Int>()
    private val functionComplexityCondDepth = mutableMapOf<String, Int>()

    fun visitFile(fileName: String) {
        val source = AstSource.File(fileName)
        val ast = KotlinGrammarAntlrKotlinParser.parseKotlinFile(source)
        try {
            visitAst(ast, level = 0, "")
        } catch (e: Exception) {
            throw IllegalStateException("Error generating API surface for $fileName", e)
        }
    }

    fun getFunctionComplexityCondCount(): MutableMap<String, Int> {
        return functionComplexityCondCount
    }

    fun getFunctionComplexityCondDepth(): MutableMap<String, Int> {
        return functionComplexityCondDepth
    }

    private fun visitAst(ast: Ast, level: Int, funName: String) {
        when (ast) {
            is AstNode -> visitAstNode(ast, level, funName)
            is AstTerminal -> ignoreNode()
            else -> error("Unable to handle $ast")
        }
    }

    private fun ignoreNode() {
        // Do Nothing
    }

    private fun visitAstNode(node: AstNode, level: Int, funName: String) {
        var newLevel = level
        var newFunName = funName
        when(node.description) {
            "ifExpression" -> {
                newLevel = level + 1
                functionComplexityCondCount[funName] = functionComplexityCondCount.getOrDefault(funName, 0) + 1
                functionComplexityCondDepth[funName] =
                    functionComplexityCondDepth.getOrDefault(funName, 0).coerceAtLeast(newLevel)
            }
            "loopStatement" -> {
                newLevel = level + 1
                functionComplexityCondCount[funName] = functionComplexityCondCount.getOrDefault(funName, 0) + 1
                functionComplexityCondDepth[funName] =
                    functionComplexityCondDepth.getOrDefault(funName, 0).coerceAtLeast(newLevel)
            }
            "whenExpression" -> {
                newLevel = level + 1
                functionComplexityCondCount[funName] = functionComplexityCondCount.getOrDefault(funName, 0) + 1
                functionComplexityCondDepth[funName] =
                    functionComplexityCondDepth.getOrDefault(funName, 0).coerceAtLeast(newLevel)
            }
            "functionDeclaration" -> {
                newFunName = node.identifierName()
            }

        }
        node.children.forEach {
            visitAst(it, newLevel, newFunName)
        }
    }

    private fun AstNode.identifierName(): String {
        return childrenNodes("simpleIdentifier")
            .joinToString(".") {
                it.firstChildTerminalOrNull("Identifier")?.text.toString()
            }
    }

    private fun AstNode.childrenNodes(description: String): List<AstNode> {
        return children.filterIsInstance<AstNode>()
            .filter { it.description == description }
    }

    private fun AstNode.firstChildTerminalOrNull(description: String): AstTerminal? {
        return children.firstOrNull { it.isTerminal(description) } as? AstTerminal
    }

    private fun Ast.isTerminal(description: String): Boolean {
        return this is AstTerminal && this.description == description
    }
}