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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by 1 on 08.04.2017.
 */
public class ParserFacade {
    private com.codeverification.Var3Parser parser;
    private com.codeverification.Var3Lexer lexer;
    private Map<GraphNode<ExprContext>, Integer> map = new HashMap<>();
    private int count = 0;

    public com.codeverification.Var3Parser.SourceContext parse(String filePath) throws IOException {
        try {
            lexer = new com.codeverification.Var3Lexer(CharStreams
                    .fromFileName(filePath));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            parser = new com.codeverification.Var3Parser(tokens);
            return parser.source();
        } catch (IOException e) {
            throw e;
        }
    }

    public void printAST(SourceContext ctx, String outputPath) throws FileNotFoundException {
        try (PrintStream printStream = new PrintStream(new FileOutputStream(outputPath))) {
            explore(ctx, 0, printStream);
        } catch (FileNotFoundException e) {
            throw e;
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
            for (int i = 0; i < indentation + 1; i++) {
                printStream.print("  ");
            }
            printStream.println("trueStmts");
            for (StatementContext stmt : ((IfStatementContext) ctx).trueSts) {
                explore((RuleContext) stmt, indentation + 1, printStream);
            }
            for (int i = 0; i < indentation + 1; i++) {
                printStream.print("  ");
            }
            printStream.println("falseStmts");
            for (StatementContext stmt : ((IfStatementContext) ctx).falseSts) {
                explore((RuleContext) stmt, indentation + 1, printStream);
            }
        } else if (ctx instanceof DoStatementContext) {
            printStream.println(ctx.getClass().getSimpleName().replaceAll("Context", ""));
            for (int i = 0; i < indentation + 1; i++) {
                printStream.print("  ");
            }
            printStream.println("Type: " + ((DoStatementContext) ctx).type.getText());
            for (int i = 0; i < ctx.getChildCount(); i++) {
                ParseTree element = ctx.getChild(i);
                if (element instanceof RuleContext) {
                    explore((RuleContext) element, indentation + 1, printStream);
                }

            }
        } else {
            printStream.println(ctx.getClass().getSimpleName().replaceAll("Context", ""));
            for (int i = 0; i < ctx.getChildCount(); i++) {
                ParseTree element = ctx.getChild(i);
                if (element instanceof RuleContext) {
                    explore((RuleContext) element, indentation + 1, printStream);
                }
            }
        }

    }

    public GraphNode<ExprContext> createControlFlowGraph(FuncDefContext ctx) {
        return new ControlFlowGraphVisitor().visitFuncDef(ctx);
    }

    public void printCFG(GraphNode<ExprContext> node, String outputPath) throws FileNotFoundException {
        try (PrintStream printStream = new PrintStream(new FileOutputStream(outputPath))) {
            map = new HashMap<>();
            count = 0;

            map.put(node, count);
            count++;
            printStream.println("start" + " -> " + (count - 1));
            printCFG(node, 0, printStream);
            map.entrySet().stream().sorted((o1, o2) -> Integer.compare(o1.getValue(), o2.getValue())).forEach(s -> printStream.println(s.getValue() + ":" + s.getKey().getNodeValue().getText()));
        } catch (Exception e) {
            throw e;
        }
    }

    public void printCFG(GraphNode<ExprContext> node, int space, PrintStream printStream) {

        if (node instanceof OrdinaryGraphNode) {
//            for (int i = 0; i < space; i++) {
//                printStream.print("  ");
//            }
            printStream.print(map.get(node) + " -> ");

            if (node.getNextNode() == null) {
                printStream.println("end");
                return;
            } else {
                GraphNode<ExprContext> nextNode = node.getNextNode();
                if (nextNode.getNodeValue() == null) {
                    nextNode = nextNode.getNextNode();
                    if (nextNode == null) {
                        printStream.println("end");
                        return;
                    }
                }

                if (map.containsKey(nextNode)) {
                    printStream.println(map.get(nextNode));
                    return;
                } else {
                    map.put(nextNode, count++);
                    printStream.println(count - 1);
                    printCFG(nextNode, 0, printStream);
                }
            }

        }
        if (node instanceof ConditionGraphNode) {
//            for (int i = 0; i < space; i++) {
//                System.out.print("  ");
//            }
            printStream.print(map.get(node) + " -> ");

            if (((ConditionGraphNode) node).getTrueNextNode() == null) {
                printStream.println("end" + " (true)");
            } else {
                GraphNode<ExprContext> nextTrueNode = ((ConditionGraphNode) node).getTrueNextNode();
                if (nextTrueNode.getNodeValue() == null) {
                    nextTrueNode = nextTrueNode.getNextNode();
                }
                if (nextTrueNode == null) {
                    printStream.println("end" + " (true)");
                } else {

                    if (map.containsKey(nextTrueNode)) {
                        printStream.println(map.get(nextTrueNode) + " (true)");
                    } else {
                        map.put(nextTrueNode, count++);
                        printStream.println(count - 1 + " (true)");
                        printCFG(nextTrueNode, 0, printStream);
                    }
                }
            }

//            for (int i = 0; i < space; i++) {
//                System.out.print("  ");
//            }
            printStream.print(map.get(node) + " -> ");
            if (((ConditionGraphNode) node).getFalseNextNode() == null) {
                printStream.println("end" + " (false)");
            } else {
                GraphNode<ExprContext> nextFalseNode = ((ConditionGraphNode) node).getFalseNextNode();
                if (nextFalseNode.getNodeValue() == null) {
                    nextFalseNode = nextFalseNode.getNextNode();
                }
                if (nextFalseNode == null) {
                    printStream.println("end" + " (false)");
                } else {
                    if (map.containsKey(nextFalseNode)) {
                        printStream.println(map.get(nextFalseNode) + " (false)");
                    } else {
                        map.put(nextFalseNode, count++);
                        printStream.println(count - 1 + " (false)");
                        printCFG(nextFalseNode, 0, printStream);
                    }
                }
            }
        }
    }

    public void printFuncCFG(String outPath, Map<FuncDefContext, Set<String>> cfg) throws FileNotFoundException {
        try (PrintStream printStream = new PrintStream(new FileOutputStream(outPath))) {
            for (FuncDefContext ctx : cfg.keySet()) {
                String funcName = ctx.funcSignature().identifier().getText();

                for (String func : cfg.get(ctx)) {
                    printStream.println(funcName + "->" + func);
                }

            }
        } catch (FileNotFoundException e) {
            throw e;
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

    public Map<FuncDefContext, Set<String>> getFuncCFG(Set<SourceContext> sources) throws Exception {
        Map<FuncDefContext, Set<String>> funcCFG = new HashMap<>();
        for (SourceContext ctx : sources) {
            for (com.codeverification.Var3Parser.SourceItemContext item : ctx.sourceItem()) {
                CFGBetweenFuncsVisitor cfgVisitor = new CFGBetweenFuncsVisitor();
                cfgVisitor.visitFuncDef((FuncDefContext) item);
                funcCFG.put((FuncDefContext) item, cfgVisitor.getSet());
            }
        }
        checkFuncCGF(funcCFG);
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
