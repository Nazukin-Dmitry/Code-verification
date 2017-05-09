package com.codeverification.compiler;

import com.codeverification.Var3Lexer;
import com.codeverification.Var3Parser;
import com.codeverification.Var3Parser.*;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @author Dmitrii Nazukin
 */
public class CodeGenerationVisitor extends com.codeverification.Var3BaseVisitor<Void> {

    ClassDefinition classDefinition;

    MethodSignature methodSignature;

    List<Command> programm = new ArrayList<>();

    int lastComNum = -1;

    Map<String, Integer> vars = new LinkedHashMap<>();

    int lastVarNum = -1;

    Map<Const, Integer> consts = new LinkedHashMap<>();

    int lastConstNum = -1;

    int lastRegistrNum = -1;

    Map<String, Integer> funcs = new LinkedHashMap<>();

    int lastFuncNum = -1;

    List<Integer> breakJumpCom = new ArrayList<>();

    private boolean isNative;
    private String library;

    @Override
    public Void visitSourceItem(SourceItemContext ctx) {
        if (ctx.funcDef() != null) {
            visit(ctx.funcDef());
        } else if (ctx.classDef() != null) {
            visit(ctx.classDef());
        } else if (ctx.nativeFunc() != null) {
            visit(ctx.nativeFunc());
        }
        return null;
    }

    @Override
    public Void visitFuncDef(com.codeverification.Var3Parser.FuncDefContext ctx) {
        visit(ctx.funcSignature());
        for (StatementContext st : ctx.statement()) {
            visit(st);
        }
        gen("END");
        return null;
    }

    @Override
    public Void visitNativeFunc(Var3Parser.NativeFuncContext ctx) {
        visit(ctx.funcSignature());
        isNative = true;
        library = ctx.library.getText().substring(1, ctx.library.getText().length()-1);
        return null;
    }

    @Override
    public Void visitClassDef(ClassDefContext ctx) {
        classDefinition = new ClassDefinition();
        classDefinition.setClassName(ctx.identifier().getText());
        // visit all fields
        int counter = 0;
        for (MemberContext memberContext : ctx.member()) {
            if (memberContext.field() != null) {
                if (memberContext.modifier().getText().equals("public")) {
                    for (IdentifierContext name : memberContext.field().listIdentifier().identifier()) {
                        classDefinition.getPublicFields().put(name.getText(), counter++);
                    }
                } else if (memberContext.modifier().getText().equals("private")) {
                    for (IdentifierContext name : memberContext.field().listIdentifier().identifier()) {
                        classDefinition.getPrivateFields().put(name.getText(), counter++);
                    }
                }
            }
        }
        // visit all functions
        for (MemberContext memberContext : ctx.member()) {
            if (memberContext.field() == null) {
                CodeGenerationVisitor codeGenerationVisitor = new CodeGenerationVisitor();
                codeGenerationVisitor.classDefinition = classDefinition;
                if (memberContext.nativeFunc() != null) {
                    codeGenerationVisitor.visit(memberContext.nativeFunc());
                } else if (memberContext.funcDef() != null) {
                    codeGenerationVisitor.visit(memberContext.funcDef());
                }
                if (memberContext.modifier().getText().equals("public")) {
                    classDefinition.getPublicFunctions().put(codeGenerationVisitor.methodSignature,
                            codeGenerationVisitor.map2MethodDefinition());
                } else if (memberContext.modifier().getText().equals("private")) {
                    classDefinition.getPrivateFunctions().put(codeGenerationVisitor.methodSignature,
                            codeGenerationVisitor.map2MethodDefinition());
                }
            }
        }
        return null;
    }

    @Override
    public Void visitFuncSignature(com.codeverification.Var3Parser.FuncSignatureContext ctx) {
        methodSignature = new MethodSignature(ctx.funcName.IDENTIFIER().getText(), ctx.listArgDef().argDef().size());
        if (ctx.typeRef() != null) {
            methodSignature.setReturnType(DataType.getDataType(ctx.typeRef().getText()));
        } else {
            methodSignature.setReturnType(DataType.UNDEFINED);
        }
        vars.put(ctx.funcName.IDENTIFIER().getText(), ++lastVarNum);
        if (ctx.param == null) {
            for (ArgDefContext arg : ctx.listArgDef().argDef()) {
                vars.put(arg.identifier().IDENTIFIER().getText(), ++lastVarNum);
                methodSignature.getArgsType().add(DataType.getDataType(arg.typeRef().getText()));
            }
        } else {
            String paramType = ctx.param.getText();
            methodSignature.setGeneric(true);
            for (ArgDefContext arg : ctx.listArgDef().argDef()) {
                vars.put(arg.identifier().IDENTIFIER().getText(), ++lastVarNum);
                methodSignature.getArgsType().add(DataType.getDataType(
                        arg.typeRef().getText().equals(paramType) ? "undefined" : arg.typeRef().getText()));
            }
        }
        return null;
    }

    @Override
    public Void visitIfStatement(com.codeverification.Var3Parser.IfStatementContext ctx) {
        visit(ctx.expr());
        gen("JMPFALSE", lastRegistrNum--);
        int adr1 = lastComNum;
        for (com.codeverification.Var3Parser.StatementContext stCtx : ctx.trueSts) {
            visit(stCtx);
        }
        gen("JMP");
        int adr2 = lastComNum;
        programm.get(adr1).addArg(lastComNum + 1);
        for (com.codeverification.Var3Parser.StatementContext stCtx : ctx.falseSts) {
            visit(stCtx);
        }
        programm.get(adr2).addArg(lastComNum + 1);
        return null;
    }

    @Override
    public Void visitWhileStatement(com.codeverification.Var3Parser.WhileStatementContext ctx) {
        int startWhile = lastComNum + 1;
        visit(ctx.expr());
        gen("JMPFALSE", lastRegistrNum--);
        int jmpFalse = lastComNum;
        for (com.codeverification.Var3Parser.StatementContext stCtx : ctx.statement()) {
            visit(stCtx);
        }
        gen("JMP", startWhile);

        programm.get(jmpFalse).addArg(lastComNum + 1);
        if (!breakJumpCom.isEmpty()) {
            breakJumpCom.forEach(com -> programm.get(com).addArg(lastComNum + 1));
            breakJumpCom.clear();
        }
        return null;
    }

    @Override
    public Void visitDoStatement(com.codeverification.Var3Parser.DoStatementContext ctx) {
        int startDo = lastComNum + 1;
        for (com.codeverification.Var3Parser.StatementContext stCtx : ctx.statement()) {
            visit(stCtx);
        }
        visit(ctx.expr());
        if (ctx.type.getType() == com.codeverification.Var3Lexer.WHILE) {
            gen("JMPTRUE", lastRegistrNum--, startDo);
        }
        if (ctx.type.getType() == com.codeverification.Var3Lexer.UNTIL) {
            gen("JMPFALSE", lastRegistrNum--, startDo);
        }

        if (!breakJumpCom.isEmpty()) {
            breakJumpCom.forEach(com -> programm.get(com).addArg(lastComNum + 1));
            breakJumpCom.clear();
        }
        return null;
    }

    @Override
    public Void visitBreakStatement(com.codeverification.Var3Parser.BreakStatementContext ctx) {
        gen("JMP");
        breakJumpCom.add(lastComNum);
        return null;
    }

    @Override
    public Void visitExpressionStatement(com.codeverification.Var3Parser.ExpressionStatementContext ctx) {
        visit(ctx.expr());
        return null;
    }

    @Override
    public Void visitPlaceExpr(com.codeverification.Var3Parser.PlaceExprContext ctx) {
        visit(ctx.identifier());
        return null;
    }

    @Override
    public Void visitUnaryExpr(com.codeverification.Var3Parser.UnaryExprContext ctx) {
        String unOp = ctx.unOp().getText();
        switch (unOp) {
            case "-":
                visit(ctx.expr());
                gen("UNMINUS", lastRegistrNum, lastRegistrNum);
                break;
            case "+":
                visit(ctx.expr());
                gen("UNADD", lastRegistrNum, lastRegistrNum);
                break;
            default:
                throw new RuntimeException("Unexpected unary operator" + unOp);
        }
        return null;
    }

    @Override
    public Void visitBracesExpr(com.codeverification.Var3Parser.BracesExprContext ctx) {
        visit(ctx.expr());
        return null;
    }

    @Override
    public Void visitLiteralExpr(com.codeverification.Var3Parser.LiteralExprContext ctx) {
        Const constt = new Const(ctx.getText());
        switch (ctx.value.getType()) {
            case Var3Lexer.BOOL:
                constt.setType(DataType.BOOL);
                break;
            case Var3Lexer.STR:
                constt.setType(DataType.STRING);
                break;
            case Var3Lexer.CHAR:
                constt.setType(DataType.CHAR);
                break;
            case Var3Lexer.HEX:
                constt.setType(DataType.LONG);
                break;
            case Var3Lexer.BITS:
                constt.setType(DataType.LONG);
                break;
            case Var3Lexer.DEC:
                constt.setType(DataType.LONG);
                break;
            default:
                throw new RuntimeException();
        }
        consts.put(constt, ++lastConstNum);
        gen("PUSHCONST", lastConstNum, ++lastRegistrNum);
        return null;
    }

    @Override
    public Void visitAssignExpr(AssignExprContext ctx) {
        visit(ctx.expr(1));
        int valueRegistrNum = lastRegistrNum;
        if (ctx.expr(0) instanceof PlaceExprContext) {
            String var = ((PlaceExprContext) ctx.expr(0)).identifier().getText();
            if (vars.containsKey(var)) {
                gen("LOADVAR", lastRegistrNum, vars.get(var));
            } else {
                // check that class
                if (classDefinition != null && classDefinition.getField(var) != null) {
                    gen("LOADCLASSVAR", lastRegistrNum, classDefinition.getField(var));
                } else {
                    vars.put(var, ++lastVarNum);
                    gen("LOADVAR", lastRegistrNum, vars.get(var));
                }
            }
        } else if (ctx.expr(0) instanceof MemberExprContext) {
            MemberExprContext memberExprContext = (MemberExprContext) ctx.expr(0);
            visit(memberExprContext.expr(0));
            String fieldName = memberExprContext.expr(1).getText();
            Const constant = new Const(fieldName, DataType.STRING);
            consts.put(constant, ++lastConstNum);
            gen("LOADOBJECTFIELD", valueRegistrNum, lastRegistrNum, lastConstNum);
        } else {
            throw new RuntimeException("=. Left operand should be identifier");
        }
        return null;
    }

    @Override
    public Void visitMemberExpr(MemberExprContext ctx) {
        visit(ctx.expr(0));
        int objectRegNum = lastRegistrNum;
        if (ctx.expr(1) instanceof CallExprContext) {
            visit(ctx.expr(1));
            Command callCommand = programm.get(lastComNum);
            callCommand.setName("CALLOBJECTFUN");
            callCommand.args.set(1, objectRegNum);
        } else if (ctx.expr(1) instanceof PlaceExprContext) {
            String varName = ctx.expr(1).getText();
            Const constant = new Const(varName, DataType.STRING);
            consts.put(constant, ++lastConstNum);
            gen("PUSHOBJECTFIELD", objectRegNum, lastConstNum, ++lastRegistrNum);
        } else {
            throw new RuntimeException();
        }
        return null;
    }

    @Override
    // TODO don't store redundant values
    public Void visitBinaryExpr(com.codeverification.Var3Parser.BinaryExprContext ctx) {
        String binOp = ctx.binOp().getText();
        int firstReg;
        switch (binOp) {
            case "+":
                visit(ctx.expr(0));
                firstReg = lastRegistrNum;
                visit(ctx.expr(1));
                gen("ADD", firstReg, lastRegistrNum, firstReg);
                lastRegistrNum = firstReg;
                break;
            case "-":
                visit(ctx.expr(0));
                firstReg = lastRegistrNum;
                visit(ctx.expr(1));
                gen("MINUS", firstReg, lastRegistrNum, firstReg);
                lastRegistrNum = firstReg;
                break;
            case "*":
                visit(ctx.expr(0));
                firstReg = lastRegistrNum;
                visit(ctx.expr(1));
                gen("MULT", firstReg, lastRegistrNum, firstReg);
                lastRegistrNum = firstReg;
                break;
            case "/":
                visit(ctx.expr(0));
                firstReg = lastRegistrNum;
                visit(ctx.expr(1));
                gen("DIV", firstReg, lastRegistrNum, firstReg);
                lastRegistrNum = firstReg;
                break;
            case "%":
                visit(ctx.expr(0));
                firstReg = lastRegistrNum;
                visit(ctx.expr(1));
                gen("MOD", firstReg, lastRegistrNum, firstReg);
                lastRegistrNum = firstReg;
                break;
            case "<":
                visit(ctx.expr(0));
                firstReg = lastRegistrNum;
                visit(ctx.expr(1));
                gen("LESS", firstReg, lastRegistrNum, firstReg);
                lastRegistrNum = firstReg;
                break;
            case ">":
                visit(ctx.expr(0));
                firstReg = lastRegistrNum;
                visit(ctx.expr(1));
                gen("LARGER", firstReg, lastRegistrNum, firstReg);
                lastRegistrNum = firstReg;
                break;
            case "==":
                visit(ctx.expr(0));
                firstReg = lastRegistrNum;
                visit(ctx.expr(1));
                gen("EQUAL", firstReg, lastRegistrNum, firstReg);
                lastRegistrNum = firstReg;
                break;
            case "||":
                visit(ctx.expr(0));
                firstReg = lastRegistrNum;
                visit(ctx.expr(1));
                gen("OR", firstReg, lastRegistrNum, firstReg);
                lastRegistrNum = firstReg;
                break;
            case "&&":
                visit(ctx.expr(0));
                firstReg = lastRegistrNum;
                visit(ctx.expr(1));
                gen("AND", firstReg, lastRegistrNum, firstReg);
                lastRegistrNum = firstReg;
                break;
            default:
                throw new RuntimeException("Unexpected binary operator" + binOp);
        }
        return null;
    }

    @Override
    public Void visitCallExpr(com.codeverification.Var3Parser.CallExprContext ctx) {
        List<Integer> args = new ArrayList<>();
        for (ExprContext arg : ctx.listExpr().expr()) {
            visit(arg);
            args.add(lastRegistrNum);
        }
        if (ctx.expr() instanceof PlaceExprContext) {
            if (args.isEmpty()) {
                lastRegistrNum ++;
                gen("CALL", lastRegistrNum, getFunNum(ctx.expr().getText()));
            } else {
                gen("CALL", args.get(0), getFunNum(ctx.expr().getText()));
                programm.get(lastComNum).addArg(args.toArray(new Integer[args.size()]));
                lastRegistrNum = args.get(0);
            }
        } else {
            throw new RuntimeException("Wrong func name:" + ctx.expr().getText());
        }
        return null;
    }

    @Override
    public Void visitIdentifier(com.codeverification.Var3Parser.IdentifierContext ctx) {
        String varName = ctx.IDENTIFIER().getText();
        if (vars.containsKey(varName)) {
            gen("PUSHVAR", vars.get(varName), ++lastRegistrNum);
        } else if (classDefinition != null && classDefinition.getField(varName) != null) {
            gen("PUSHCLASSVAR", classDefinition.getField(varName), ++lastRegistrNum);
        } else {
            throw new RuntimeException("Variable is not defined:" + varName);
        }
        return null;
    }

    @Override
    public Void visitInitExpr(InitExprContext ctx) {
        String className = ctx.identifier().getText();
        Const constant = new Const(className, DataType.STRING);
        consts.put(constant, ++lastConstNum);
        int classNameConstNum = lastConstNum;
        List<Integer> args = new ArrayList<>();
        for (ExprContext arg : ctx.listExpr().expr()) {
            visit(arg);
            args.add(lastRegistrNum);
        }
        gen("INITIALIZE", lastRegistrNum, classNameConstNum);
        programm.get(lastComNum).addArg(args.toArray(new Integer[args.size()]));
        return null;
    }

    private void gen(String com, int... strings) {
        Command command = new Command(com);
        programm.add(command);
        lastComNum++;
        for (int arg : strings) {
            command.addArg(arg);
        }
    }

    private Integer getFunNum(String func) {
        if (!funcs.containsKey(func)) {
            funcs.put(func, ++lastFuncNum);
        }
        return funcs.get(func);
    }

    public void print(PrintStream printStream) {
        if (classDefinition == null) {
            printStream.println(".methodSignature");
            printStream.println(methodSignature);
            if (isNative) {
                printStream.println("from " + library);
            } else {
                printStream.println(".funcs");
                funcs.entrySet()
                        .forEach(e -> printStream.println(e.getValue() + ":" + e.getKey()));
                printStream.println(".vars");
                vars.entrySet()
                        .forEach(e -> printStream.println(e.getValue() + ":" + e.getKey()));
                printStream.println(".consts");
                consts.entrySet()
                        .forEach(e -> printStream.println(e.getValue() + ":" + e.getKey()));
                printStream.println(".programm");
                IntStream.range(0, programm.size()).forEach(idx -> printStream.println(idx + ": " + programm.get(idx)));
            }
        } else {
            printStream.println(".Class");
            printStream.println(classDefinition.getClassName());
            printStream.println(".public fields");
            printStream.println(classDefinition.getPublicFields());
            printStream.println(".private fields");
            printStream.println(classDefinition.getPrivateFields());
            printStream.println(".public functions");
            printStream.println(classDefinition.getPublicFunctions());
            printStream.println(".private functions");
            printStream.println(classDefinition.getPrivateFunctions());

        }
    }

    public MethodDefinition map2MethodDefinition() {
        MethodDefinition methodDefinition = new MethodDefinition();
        methodDefinition.setMethodSignature(methodSignature);
        methodDefinition.setVarsCount(vars.values().size());
        if (!isNative) {
            methodDefinition.getCommands().addAll(programm);
            methodDefinition.getConsts().addAll(consts.keySet());
            methodDefinition.getFuncs().addAll(funcs.keySet());
        } else {
            methodDefinition.setNative(true);
            methodDefinition.setLibraryName(library);
        }

        return methodDefinition;
    }

    public static class Const implements Serializable {
        private static final long serialVersionUID = 4;

        private String constName;
        private DataType type;

        public Const(String constName) {
            if (constName.startsWith("\"")) {
                constName = constName.substring(1, constName.length() - 1);
            }
            this.constName = constName;
        }

        public Const(String constName, DataType type) {
            if (constName.startsWith("\"")) {
                constName = constName.substring(1, constName.length() - 1);
            }
            this.constName = constName;
            this.type = type;
        }

        public String getConstName() {
            return constName;
        }

        public void setConstName(String constName) {
            this.constName = constName;
        }

        public DataType getType() {
            return type;
        }

        public void setType(DataType type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "Const{" +
                    "constName='" + constName + '\'' +
                    ", type=" + type +
                    '}';
        }
    }
}
