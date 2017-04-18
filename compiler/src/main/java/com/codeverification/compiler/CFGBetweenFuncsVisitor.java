package com.codeverification.compiler;

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
        return super.visitCallExpr(ctx);
    }

    public Set<MethodSignature> getSet() {
        return set;
    }

}
