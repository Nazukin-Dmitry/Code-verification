package com.codeverification;

import com.codeverification.Var3Parser.ExprContext;
import org.apache.commons.collections.CollectionUtils;

/**
 * Created by 1 on 09.04.2017.
 */
public class ControlFlowGraphVisitor extends com.codeverification.Var3BaseVisitor<GraphNode<ExprContext>> {

    private OrdinaryGraphNode<ExprContext> lastNode;

    @Override
    public GraphNode<ExprContext> visitSource(com.codeverification.Var3Parser.SourceContext ctx) {
        return null;
    }

    @Override
    public GraphNode<ExprContext> visitFuncDef(com.codeverification.Var3Parser.FuncDefContext ctx) {
        GraphNode<ExprContext> startNode = visit(ctx.statement(0));
        for (int i = 0; i < ctx.statement().size(); i++) {
            lastNode.setNextNode(visit(ctx.statement(i)));
        }
        return startNode;
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
        ConditionGraphNode<ExprContext> startNode = new ConditionGraphNode<>();
        startNode.setNode(ctx.expr());
        if (CollectionUtils.isNotEmpty(ctx.trueSts)) {
            startNode.setTrueNextNode(visit(ctx.trueSts.get(0)));
            for (int i = 0; i < ctx.trueSts.size(); i++) {
                lastNode.setNextNode();
            }
        }


        startNode.setFalseNextNode(visit(ctx.falseSts.get(0)));
        return startNode;
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
