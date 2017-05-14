package com.codeverification.compiler;

import com.codeverification.Var3Parser.CallExprContext;
import com.codeverification.Var3Parser.MemberExprContext;
import com.codeverification.Var3Parser.NativeFuncContext;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Dmitrii Nazukin
 */
public class CFGBetweenFuncsVisitor extends com.codeverification.Var3BaseVisitor<Void> {

    Set<MethodSignature> set = new HashSet<>();

    @Override
    public Void visitCallExpr(com.codeverification.Var3Parser.CallExprContext ctx) {
        String name = ctx.expr().getText();
        set.add(new MethodSignature(name, ctx.listExpr().expr().size()));
        return null;
    }

    @Override
    public Void visitNativeFunc(NativeFuncContext ctx) {
        return null;
    }

    @Override
    public Void visitMemberExpr(MemberExprContext ctx) {
        if (ctx.expr(1) instanceof CallExprContext) {
            String name = ctx.expr(0).getText() + "." + ctx.expr(1).getText();
            set.add(new MethodSignature(name, ((CallExprContext)ctx.expr(1)).listExpr().expr().size()));
        }
        return null;
    }

    public Set<MethodSignature> getSet() {
        return set;
    }

}
