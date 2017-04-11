package com.codeverification;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import com.codeverification.Var3Parser.BinOpContext;
import com.codeverification.Var3Parser.BuiltinContext;
import com.codeverification.Var3Parser.ExprContext;
import com.codeverification.Var3Parser.FuncDefContext;
import com.codeverification.Var3Parser.IdentifierContext;
import com.codeverification.Var3Parser.IfStatementContext;
import com.codeverification.Var3Parser.LiteralExprContext;
import com.codeverification.Var3Parser.SourceContext;
import com.codeverification.Var3Parser.StatementContext;
import com.codeverification.Var3Parser.UnOpContext;

/**
 * Created by 1 on 08.04.2017.
 */
public class ParserFacade {
    private com.codeverification.Var3Parser parser;
    private com.codeverification.Var3Lexer lexer;
    private Map<GraphNode<ExprContext>, Integer> map = new HashMap<>();
    int count = 0;

    public com.codeverification.Var3Parser.SourceContext parse(String filePath) {
        try {
            lexer = new com.codeverification.Var3Lexer(CharStreams
                    .fromFileName(filePath));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            parser = new com.codeverification.Var3Parser(tokens);
            return parser.source();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void printAST(SourceContext ctx, String outputPath) {
        PrintStream printStream = null;
        try {
            printStream = new PrintStream(new FileOutputStream(outputPath));
            explore(ctx, 0, printStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            printStream.close();
        }

    }

    private void explore(RuleContext ctx, int indentation, PrintStream printStream) {
        String ruleName = com.codeverification.Var3Parser.ruleNames[ctx.getRuleIndex()];

        for (int i = 0; i < indentation; i++) {
            printStream.print("  ");
        }

        if (ctx instanceof LiteralExprContext
                || ctx instanceof BinOpContext
                || ctx instanceof UnOpContext
                || ctx instanceof IdentifierContext
                || ctx instanceof BuiltinContext) {
            printStream.println(
                    ctx.getClass().getSimpleName().replaceAll("Context", "") + ": " + ctx.getChild(0).getText());
        } else if (ctx instanceof IfStatementContext) {
            printStream.println(ctx.getClass().getSimpleName().replaceAll("Context", ""));
            for (int i = 0; i < indentation; i++) {
                printStream.print("  ");
            }
            printStream.println("trueStmts");
            for (StatementContext stmt : ((IfStatementContext) ctx).trueSts) {
                explore((RuleContext)stmt, indentation + 1, printStream);
            }
            for (int i = 0; i < indentation; i++) {
                printStream.print("  ");
            }
            printStream.println("falseStmts");
            for (StatementContext stmt : ((IfStatementContext) ctx).falseSts) {
                explore((RuleContext)stmt, indentation + 1, printStream);
            }
        } else {
            printStream.println(ctx.getClass().getSimpleName().replaceAll("Context", ""));
            for (int i = 0; i < ctx.getChildCount(); i++) {
                ParseTree element = ctx.getChild(i);
                if (element instanceof RuleContext) {
                    explore((RuleContext)element, indentation + 1, printStream);
                }
            }
        }

    }

    public GraphNode<ExprContext> createControlFlowGraph(SourceContext ctx) {
        return new ControlFlowGraphVisitor().visitFuncDef((FuncDefContext) ctx.sourceItem(0));
    }

    public void printCFG(GraphNode<ExprContext> node) {
        int incr = 0;

//        if (node instanceof OrdinaryGraphNode) {
//            if (map.containsKey(node)) {
//                System.out.println("goto" + map.get(node));
//            } else {
//                System.out.println(count + ":" + node.getNodeValue().getText());
//                map.put(node, count);
//            }
//            printCFG(node.getNextNode());
//        }
//        if (node instanceof ConditionGraphNode) {
//            System.out.println();
//        }

        if (node instanceof OrdinaryGraphNode) {
            System.out.println(node.getNodeValue().getText() + "->" + node.getNextNode().getNodeValue().getText());
            printCFG(node.getNextNode(), incr++);
            map.put(node, 0);
        }
        if (node instanceof ConditionGraphNode) {
            System.out.println("Success:" + node.getNodeValue().getText() + "->" + ((ConditionGraphNode<ExprContext>) node).getTrueNextNode().getNodeValue().getText());
            System.out.println("Success:" + node.getNodeValue().getText() + "->" + ((ConditionGraphNode<ExprContext>) node).getFalseNextNode().getNodeValue().getText());
            incr++;
            printCFG(((ConditionGraphNode<ExprContext>) node).getTrueNextNode(), incr);
            printCFG(((ConditionGraphNode<ExprContext>) node).getFalseNextNode(), incr);
        }
    }

    private void printCFG(GraphNode<ExprContext> node, int incr) {
        for (int i = 0; i < incr; i++) {
            System.out.println("  ");
        }
        if (node instanceof OrdinaryGraphNode) {
            System.out.println(node.getNodeValue().getText() + "->" + node.getNextNode().getNodeValue().getText());
            if (!map.containsKey(node)) {
                printCFG(node.getNextNode(), incr++);
            }
        }
        if (node instanceof ConditionGraphNode) {
            System.out.println("Success:" + node.getNodeValue().getText() + "->" + ((ConditionGraphNode<ExprContext>) node).getTrueNextNode().getNodeValue().getText());
            System.out.println("Success:" + node.getNodeValue().getText() + "->" + ((ConditionGraphNode<ExprContext>) node).getFalseNextNode().getNodeValue().getText());
            incr++;
            printCFG(((ConditionGraphNode<ExprContext>) node).getTrueNextNode(), incr);
            printCFG(((ConditionGraphNode<ExprContext>) node).getFalseNextNode(), incr);
        }
    }

    public void printFuncCFG(String outPath, Map<FuncDefContext, Set<String>> cfg) {
        for (FuncDefContext ctx : cfg.keySet()) {
            PrintStream printStream = null;
            try {
                String funcName = ctx.funcSignature().identifier().getText();
                printStream = new PrintStream(new FileOutputStream(outPath + "\\"+funcName +".txt"));
                for (String func : cfg.get(ctx)) {
                    printStream.println(funcName + "->" + func);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                printStream.close();
            }
        }
    }

    public void checkFuncCGF(Map<FuncDefContext, Set<String>> cfg) throws Exception {
        Set<String> funcs = cfg.keySet().stream().map(func -> func.funcSignature().identifier().getText()).collect(Collectors.toSet());
        for (FuncDefContext ctx : cfg.keySet()) {
            for (String func : cfg.get(ctx)) {
                if (!funcs.contains(func)) {
                    throw new Exception("Function with name \" " + func + " \" doesn't exist");
                }
            }
        }
    }

    public Map<FuncDefContext, Set<String>> getFuncCFG(Set<SourceContext> sources) {
        Map<FuncDefContext, Set<String>> funcCFG = new HashMap<>();
        for (SourceContext ctx : sources) {
            for (com.codeverification.Var3Parser.SourceItemContext item : ctx.sourceItem()) {
                CFGBetweenFuncsVisitor cfgVisitor = new CFGBetweenFuncsVisitor();
                cfgVisitor.visitFuncDef((FuncDefContext) item);
                funcCFG.put((FuncDefContext) item, cfgVisitor.getSet());
            }
        }
        return funcCFG;
    }


    public com.codeverification.Var3Parser getParser() {
        return parser;
    }

    public void setParser(com.codeverification.Var3Parser parser) {
        this.parser = parser;
    }

    public com.codeverification.Var3Lexer getLexer() {
        return lexer;
    }

    public void setLexer(com.codeverification.Var3Lexer lexer) {
        this.lexer = lexer;
    }
}
