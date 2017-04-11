package com.codeverification;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Dmitrii Nazukin
 */
public class CFGBetweenFuncsVisitor extends com.codeverification.Var3BaseVisitor<Void> {

    Set<String> set = new HashSet<>();

    @Override
    public Void visitCallExpr(com.codeverification.Var3Parser.CallExprContext ctx) {
        set.add(ctx.expr().getText());
        return super.visitCallExpr(ctx);
    }

    public Set<String> getSet() {
        return set;
    }
}
