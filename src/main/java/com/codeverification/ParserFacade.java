package com.codeverification;

import com.codeverification.Var3Parser.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by 1 on 08.04.2017.
 */
public class ParserFacade {
    public static com.codeverification.Var3Parser.SourceContext parse(String filePath) {
        try {
            com.codeverification.Var3Lexer lexer = new com.codeverification.Var3Lexer(CharStreams.fromFileName("F:\\учеба\\codeerification\\src\\main\\java\\com\\codeverification\\1.txt"));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            com.codeverification.Var3Parser parser = new com.codeverification.Var3Parser(tokens);
            return parser.source();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void printAST(SourceContext ctx, String outputPath) {
        PrintStream printStream = null;
        try {
            printStream = new PrintStream(new FileOutputStream(outputPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        explore(ctx, 0, printStream);
    }

    private static void explore(RuleContext ctx, int indentation, PrintStream printStream) {
        String ruleName = com.codeverification.Var3Parser.ruleNames[ctx.getRuleIndex()];

        for (int i=0;i<indentation;i++) {
            printStream.print("  ");
            System.out.print("  ");
        }

        if (ctx instanceof LiteralExprContext || ctx instanceof BinOpContext || ctx instanceof UnOpContext
                || ctx instanceof IdentifierContext || ctx instanceof BuiltinContext) {
            printStream.println(ctx.getClass().getSimpleName().replaceAll("Context", "") + ": " + ctx.getChild(0).getText());
        } else {
            printStream.println(ctx.getClass().getSimpleName().replaceAll("Context", ""));
        }
        for (int i=0;i<ctx.getChildCount();i++) {
            ParseTree element = ctx.getChild(i);
            if (element instanceof RuleContext) {
                explore((RuleContext)element, indentation + 1, printStream);
            }
        }
    }

    public GraphNode<ExprContext> createControlFlowGraph(FuncDefContext ctx) {
        GraphNode<ExprContext> startNode = new OrdinaryGraphNode<>();
        GraphNode<ExprContext> lastNode = startNode;
//        for (StatementContext context :  ) {
            lastNode = visit(ctx.statement(1), lastNode);
//        }
        return null;
    }

    private GraphNode<ExprContext> visit(IfStatementContext ctx, GraphNode<ExprContext> lastNode) {
        return null;
    }

    private GraphNode<ExprContext> visit(WhileStatementContext ctx, GraphNode<ExprContext> lastNode) {
        return null;
    }

    private GraphNode<ExprContext> visit(DoStatementContext ctx, GraphNode<ExprContext> lastNode) {
        return null;
    }

    private GraphNode<ExprContext> visit(BreakStatementContext ctx, GraphNode<ExprContext> lastNode) {
        return null;
    }

    private GraphNode<ExprContext> visit(ExpressionStatementContext ctx, GraphNode<ExprContext> lastNode) {
        return null;
    }
}
