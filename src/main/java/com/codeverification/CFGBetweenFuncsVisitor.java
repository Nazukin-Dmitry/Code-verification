package com.codeverification;

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

    public class MethodSignature {
        private String funcName;
        private int argCount;

        public MethodSignature(String funcName, int argCount) {
            this.funcName = funcName;
            this.argCount = argCount;
        }

        public String getFuncName() {
            return funcName;
        }

        public void setFuncName(String funcName) {
            this.funcName = funcName;
        }

        public int getArgCount() {
            return argCount;
        }

        public void setArgCount(int argCount) {
            this.argCount = argCount;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MethodSignature that = (MethodSignature) o;

            if (argCount != that.argCount) return false;
            return funcName != null ? funcName.equals(that.funcName) : that.funcName == null;
        }

        @Override
        public int hashCode() {
            int result = funcName != null ? funcName.hashCode() : 0;
            result = 31 * result + argCount;
            return result;
        }
    }

}
