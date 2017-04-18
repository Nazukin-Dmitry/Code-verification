package com.codeverification.compiler;

import java.util.ArrayDeque;
import java.util.Deque;

import org.apache.commons.collections.CollectionUtils;

import com.codeverification.compiler.ConditionGraphNode.ConditionType;
import com.codeverification.Var3Parser.ExprContext;

/**
 * Created by 1 on 09.04.2017.
 */
public class ControlFlowGraphVisitor extends com.codeverification.Var3BaseVisitor<GraphNode<ExprContext>> {

    private OrdinaryGraphNode<ExprContext> lastNode;

    private Deque<OrdinaryGraphNode<ExprContext> > loopsStack = new ArrayDeque<>();

    private boolean isBreak;

    @Override
    public GraphNode<ExprContext> visitSource(com.codeverification.Var3Parser.SourceContext ctx) {
        return null;
    }

    @Override
    public GraphNode<ExprContext> visitFuncDef(com.codeverification.Var3Parser.FuncDefContext ctx) {
        GraphNode<ExprContext> startNode = visit(ctx.statement(0));
        for (int i = 1; i < ctx.statement().size(); i++) {
            lastNode.setNextNode(visit(ctx.statement(i)));
        }
        return startNode;
    }

    @Override
    public GraphNode<ExprContext> visitIfStatement(com.codeverification.Var3Parser.IfStatementContext ctx) {
        ConditionGraphNode<ExprContext> ifNode = new ConditionGraphNode<>();
        ifNode.setType(ConditionType.IF);
        ifNode.setNodeValue(ctx.expr());

        boolean initialIsBreak = isBreak;

        OrdinaryGraphNode<ExprContext> endIfNode = new EndIfGraphNode<>();

        if (CollectionUtils.isNotEmpty(ctx.trueSts)) {
            ifNode.setTrueNextNode(visit(ctx.trueSts.get(0)));
            for (int i = 1; i < ctx.trueSts.size(); i++) {
                if (!isBreak) {
                    lastNode.setNextNode(visit(ctx.trueSts.get(i)));
                } else {
                    break;
                }
            }

            lastNode.setNextNode(endIfNode);

        } else {
            ifNode.setTrueNextNode(endIfNode);
        }

        initialIsBreak = isBreak;

        isBreak = false;

        if (CollectionUtils.isNotEmpty(ctx.falseSts)) {
            ifNode.setFalseNextNode(visit(ctx.falseSts.get(0)));
            for (int i = 1; i < ctx.falseSts.size(); i++) {
                if (!isBreak) {
                    lastNode.setNextNode(visit(ctx.falseSts.get(i)));
                } else {
                    break;
                }
            }

            lastNode.setNextNode(endIfNode);

        } else {
            ifNode.setFalseNextNode(endIfNode);
        }

        isBreak = isBreak && initialIsBreak;
        lastNode = endIfNode;
        return ifNode;
    }

    @Override
    public GraphNode<ExprContext> visitWhileStatement(com.codeverification.Var3Parser.WhileStatementContext ctx) {
        ConditionGraphNode<ExprContext> whileNode = new ConditionGraphNode<>();
        whileNode.setType(ConditionType.WHILE);
        whileNode.setNodeValue(ctx.expr());
        OrdinaryGraphNode<ExprContext> endWhileNode = new OrdinaryGraphNode<ExprContext>();
        loopsStack.push(endWhileNode);
        whileNode.setFalseNextNode(endWhileNode);

        if (CollectionUtils.isNotEmpty(ctx.statement())) {
            whileNode.setTrueNextNode(visit(ctx.statement().get(0)));
            for (int i = 1; i < ctx.statement().size(); i++) {
                if (!isBreak){
                    lastNode.setNextNode(visit(ctx.statement().get(i)));
                } else {
                    break;
                }
            }
            if (!isBreak) {
                lastNode.setNextNode(whileNode);
            }
        } else {
            whileNode.setTrueNextNode(whileNode);
        }

        lastNode = endWhileNode;
        loopsStack.pop();
        isBreak = false;
        return whileNode;
    }

    @Override
    public GraphNode<ExprContext> visitDoStatement(com.codeverification.Var3Parser.DoStatementContext ctx) {
        GraphNode<ExprContext> startDoNode;
        ConditionGraphNode<ExprContext> doNode = new ConditionGraphNode<>();
        doNode.setNodeValue(ctx.expr());

        OrdinaryGraphNode<ExprContext> endDoNode = new OrdinaryGraphNode<ExprContext>();
        loopsStack.push(endDoNode);

        if (CollectionUtils.isNotEmpty(ctx.statement())) {
            startDoNode = visit(ctx.statement(0));
            for (int i = 1; i < ctx.statement().size(); i++) {
                if (!isBreak) {
                    lastNode.setNextNode(visit(ctx.statement(i)));
                } else {
                    break;
                }
            }
            if (!isBreak) {
                lastNode.setNextNode(doNode);
            }

        } else {
            startDoNode = doNode;

        }
        if (ctx.type.getType() == com.codeverification.Var3Lexer.WHILE) {
            doNode.setType(ConditionType.DO_WHILE);
            doNode.setTrueNextNode(startDoNode);
            doNode.setFalseNextNode(endDoNode);
        } else {
            doNode.setType(ConditionType.DO_UNTIL);
            doNode.setFalseNextNode(startDoNode);
            doNode.setTrueNextNode(endDoNode);
        }
        lastNode = endDoNode;

        loopsStack.pop();
        isBreak = false;
        return startDoNode;
    }

    @Override
    public GraphNode<ExprContext> visitBreakStatement(com.codeverification.Var3Parser.BreakStatementContext ctx) {
        isBreak = true;
        if (loopsStack.peek() == null){
            throw new RuntimeException("Break statement is not inside loop");
        }
        return loopsStack.peek();
    }

    @Override
    public GraphNode<ExprContext> visitExpressionStatement(com.codeverification.Var3Parser.ExpressionStatementContext ctx) {
        GraphNode<ExprContext> node = new OrdinaryGraphNode<>();
        node.setNodeValue(ctx.expr());
        lastNode = (OrdinaryGraphNode<ExprContext>)node;
        return node;
    }

}
