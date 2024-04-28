
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
        visitAst(ast, level = 0, "")
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

    private fun conditionalExpressionProcessing(level: Int, funName: String): Int {
        val newLevel: Int = level + 1
        functionComplexityCondCount[funName] = functionComplexityCondCount.getOrDefault(funName, 0) + 1
        functionComplexityCondDepth[funName] =
            functionComplexityCondDepth.getOrDefault(funName, 0).coerceAtLeast(newLevel)
        return newLevel
    }

    private fun visitAstNode(node: AstNode, level: Int, funName: String) {
        var newLevel: Int = level
        var newFunName: String = funName
        when(node.description) {
            "ifExpression" -> {
                newLevel = conditionalExpressionProcessing(level, funName)
            }
            "loopStatement" -> {
                newLevel = conditionalExpressionProcessing(level, funName)
            }
            "whenExpression" -> {
                newLevel = conditionalExpressionProcessing(level, funName)
            }
            "functionDeclaration" -> {
                newFunName = node.identifierName()
                functionComplexityCondCount[newFunName] = 0
                functionComplexityCondDepth[newFunName] = 0
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