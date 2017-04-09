package com.codeverification;

import com.codeverification.Var3Parser.ExprContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * Created by 1 on 09.04.2017.
 */
public class ControlFlowGraphVisitor extends com.codeverification.Var3BaseVisitor<GraphNode<ExprContext>> {


    @Override
    public GraphNode<ExprContext> visitSource(com.codeverification.Var3Parser.SourceContext ctx) {
        return null;
    }

    @Override
    public GraphNode<ExprContext> visitFuncDef(com.codeverification.Var3Parser.FuncDefContext ctx) {
        GraphNode<ExprContext> startNode = new OrdinaryGraphNode<>();
        GraphNode<ExprContext> lastNode = startNode;
//        for (StatementContext context :  ) {
        lastNode = visit(ctx.statement(1));

    }

    @Override
    public GraphNode<ExprContext> visitFuncSignature(com.codeverification.Var3Parser.FuncSignatureContext ctx) {
        return null;
    }

    @Override
    public GraphNode<ExprContext> visitArgDef(com.codeverification.Var3Parser.ArgDefContext ctx) {
        return null;
    }

    @Override
    public GraphNode<ExprContext> visitBuiltin(com.codeverification.Var3Parser.BuiltinContext ctx) {
        return null;
    }

    @Override
    public GraphNode<ExprContext> visitCustom(com.codeverification.Var3Parser.CustomContext ctx) {
        return null;
    }

    @Override
    public GraphNode<ExprContext> visitIfStatement(com.codeverification.Var3Parser.IfStatementContext ctx) {
        return null;
    }

    @Override
    public GraphNode<ExprContext> visitWhileStatement(com.codeverification.Var3Parser.WhileStatementContext ctx) {
        return null;
    }

    @Override
    public GraphNode<ExprContext> visitDoStatement(com.codeverification.Var3Parser.DoStatementContext ctx) {
        return null;
    }

    @Override
    public GraphNode<ExprContext> visitBreakStatement(com.codeverification.Var3Parser.BreakStatementContext ctx) {
        return null;
    }

    @Override
    public GraphNode<ExprContext> visitExpressionStatement(com.codeverification.Var3Parser.ExpressionStatementContext ctx) {
        return null;
    }

    @Override
    public GraphNode<ExprContext> visitPlaceExpr(com.codeverification.Var3Parser.PlaceExprContext ctx) {
        return null;
    }

    @Override
    public GraphNode<ExprContext> visitUnaryExpr(com.codeverification.Var3Parser.UnaryExprContext ctx) {
        return null;
    }

    @Override
    public GraphNode<ExprContext> visitBracesExpr(com.codeverification.Var3Parser.BracesExprContext ctx) {
        return null;
    }

    @Override
    public GraphNode<ExprContext> visitLiteralExpr(com.codeverification.Var3Parser.LiteralExprContext ctx) {
        return null;
    }

    @Override
    public GraphNode<ExprContext> visitBinaryExpr(com.codeverification.Var3Parser.BinaryExprContext ctx) {
        return null;
    }

    @Override
    public GraphNode<ExprContext> visitCallExpr(com.codeverification.Var3Parser.CallExprContext ctx) {
        return null;
    }

    @Override
    public GraphNode<ExprContext> visitListArgDef(com.codeverification.Var3Parser.ListArgDefContext ctx) {
        return null;
    }

    @Override
    public GraphNode<ExprContext> visitListExpr(com.codeverification.Var3Parser.ListExprContext ctx) {
        return null;
    }

    @Override
    public GraphNode<ExprContext> visitIdentifier(com.codeverification.Var3Parser.IdentifierContext ctx) {
        return null;
    }

    @Override
    public GraphNode<ExprContext> visitBinOp(com.codeverification.Var3Parser.BinOpContext ctx) {
        return null;
    }
}
