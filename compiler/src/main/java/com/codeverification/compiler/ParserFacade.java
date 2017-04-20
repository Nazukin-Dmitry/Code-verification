package com.codeverification.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import com.codeverification.Var3Parser.BinOpContext;
import com.codeverification.Var3Parser.BuiltinContext;
import com.codeverification.Var3Parser.DoStatementContext;
import com.codeverification.Var3Parser.ExprContext;
import com.codeverification.Var3Parser.FuncDefContext;
import com.codeverification.Var3Parser.FuncSignatureContext;
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

    private int count = 0;

    public com.codeverification.Var3Parser.SourceContext parse(String filePath) throws Exception {
        try {
            lexer = new com.codeverification.Var3Lexer(CharStreams.fromFileName(filePath));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            parser = new com.codeverification.Var3Parser(tokens);

            SourceContext source = parser.source();
            if (parser.getNumberOfSyntaxErrors() != 0) {
                throw new Exception("Parse error!");
            }
            return source;
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
            explore(((IfStatementContext)ctx).expr(), indentation + 1, printStream);
            for (int i = 0; i < indentation + 1; i++) {
                printStream.print("  ");
            }
            printStream.println("trueStmts");
            for (StatementContext stmt : ((IfStatementContext)ctx).trueSts) {
                explore((RuleContext)stmt, indentation + 2, printStream);
            }
            for (int i = 0; i < indentation + 1; i++) {
                printStream.print("  ");
            }
            printStream.println("falseStmts");
            for (StatementContext stmt : ((IfStatementContext)ctx).falseSts) {
                explore((RuleContext)stmt, indentation + 2, printStream);
            }
        } else if (ctx instanceof DoStatementContext) {
            printStream.println(ctx.getClass().getSimpleName().replaceAll("Context", ""));
            for (int i = 0; i < indentation + 1; i++) {
                printStream.print("  ");
            }
            printStream.println("Type: " + ((DoStatementContext)ctx).type.getText());
            for (int i = 0; i < ctx.getChildCount(); i++) {
                ParseTree element = ctx.getChild(i);
                if (element instanceof RuleContext) {
                    explore((RuleContext)element, indentation + 1, printStream);
                }

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

    public GraphNode<ExprContext> createControlFlowGraph(FuncDefContext ctx) {
        return new ControlFlowGraphVisitor().visitFuncDef(ctx);
    }

    public void printCFG(GraphNode<ExprContext> node, CodeGenerationVisitor codeGenerationVisitor, String outputPath) throws FileNotFoundException {
        File file = new File(outputPath);
        file.getParentFile().mkdirs();
        try (PrintStream printStream = new PrintStream(new FileOutputStream(file))) {
            map = new HashMap<>();
            count = 0;

            map.put(node, count);
            count++;
            printStream.println("start" + " -> " + (count - 1));
            printCFG(node, 0, printStream);
            printStream.println();
            map.entrySet().stream().sorted(Comparator.comparingInt(Entry::getValue))
                    .forEach(s -> printStream.println(s.getValue() + ":" + s.getKey().getNodeValue().getText()));

            printStream.println();
            codeGenerationVisitor.print(printStream);
        } catch (Exception e) {
            throw e;
        }
    }

    public void printCFG(GraphNode<ExprContext> node, String outputPath) throws FileNotFoundException {
        File file = new File(outputPath);
        file.getParentFile().mkdirs();
        try (PrintStream printStream = new PrintStream(new FileOutputStream(file))) {
            map = new HashMap<>();
            count = 0;

            map.put(node, count);
            count++;
            printStream.println("start" + " -> " + (count - 1));
            printCFG(node, 0, printStream);
            printStream.println();
            map.entrySet().stream().sorted(Comparator.comparingInt(Entry::getValue))
                    .forEach(s -> printStream.println(s.getValue() + ":" + s.getKey().getNodeValue().getText()));
        } catch (Exception e) {
            throw e;
        }
    }

    public void printCFG4(List<GraphNode<ExprContext>> graphs, Map<MethodSignature,  Set<MethodSignature>> funcCFG, String outputPath) throws FileNotFoundException {
        File file = new File(outputPath);
        file.getParentFile().mkdirs();
        try (PrintStream printStream = new PrintStream(new FileOutputStream(file))) {
            for (GraphNode<ExprContext> graph : graphs) {
                map = new HashMap<>();
                count = 0;

                map.put(graph, count);
                count++;
                printStream.println("start" + " -> " + (count - 1));
                printCFG(graph, 0, printStream);
                printStream.println();
                map.entrySet().stream().sorted(Comparator.comparingInt(Entry::getValue))
                        .forEach(s -> printStream.println(s.getValue() + ":" + s.getKey().getNodeValue().getText()));
                printStream.println("---------------------------------------");
            }
            printStream.println("Functions CFG");
            printFuncCFG4(funcCFG, printStream);
        } catch (Exception e) {
            throw e;
        }
    }

    public void printCFG(GraphNode<ExprContext> node, int space, PrintStream printStream) {

        if (node instanceof OrdinaryGraphNode) {
            if (!(node.getNodeValue() == null)) {
                printStream.print(map.get(node) + " -> ");
            }

            if (node.getNextNode() == null) {
                printStream.println("end");
                return;
            } else {
                GraphNode<ExprContext> nextNode = node.getNextNode();
                if (nextNode.getNodeValue() == null) {
                    printCFG(nextNode, 0, printStream);
                    return;
                    // nextNode = nextNode.getNextNode();
                    // if (nextNode == null) {
                    // printStream.println("end");
                    // return;
                    // }
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
            printStream.print(map.get(node) + " t-> ");

            if (((ConditionGraphNode)node).getTrueNextNode() == null) {
                printStream.println("end");
            } else {
                GraphNode<ExprContext> nextTrueNode = ((ConditionGraphNode)node).getTrueNextNode();
                if (nextTrueNode.getNodeValue() == null) {
                    printCFG(nextTrueNode, 0, printStream);
                } else {
                    if (map.containsKey(nextTrueNode)) {
                        printStream.println(map.get(nextTrueNode));
                    } else {
                        map.put(nextTrueNode, count++);
                        printStream.println(count - 1);
                        printCFG(nextTrueNode, 0, printStream);
                    }
                }
            }

            printStream.print(map.get(node) + " f-> ");
            if (((ConditionGraphNode)node).getFalseNextNode() == null) {
                printStream.println("end");
            } else {
                GraphNode<ExprContext> nextFalseNode = ((ConditionGraphNode)node).getFalseNextNode();
                if (nextFalseNode.getNodeValue() == null) {
                    printCFG(nextFalseNode, 0, printStream);
                } else {
                    if (map.containsKey(nextFalseNode)) {
                        printStream.println(map.get(nextFalseNode));
                    } else {
                        map.put(nextFalseNode, count++);
                        printStream.println(count - 1);
                        printCFG(nextFalseNode, 0, printStream);
                    }
                }
            }
        }
    }

    private void printIfStatement(GraphNode<ExprContext> node, PrintStream printStream) {

    }

    public void printFuncCFG(String outPath, Map<MethodSignature, Set<MethodSignature>> cfg) throws FileNotFoundException {
        File file = new File(outPath);
        file.getParentFile().mkdirs();
        try (PrintStream printStream = new PrintStream(new FileOutputStream(file))) {
            for (MethodSignature ctx : cfg.keySet()) {
                String funcName = ctx.getFuncName();

                for (MethodSignature func : cfg.get(ctx)) {
                    printStream.println(funcName + "->" + func.getFuncName());
                }

            }
        } catch (FileNotFoundException e) {
            throw e;
        }
    }

    public void printFuncCFG4(Map<MethodSignature, Set<MethodSignature>> cfg, PrintStream printStream)
            throws FileNotFoundException {
        for (MethodSignature ctx : cfg.keySet()) {
            String funcName = ctx.getFuncName();

            for (MethodSignature func : cfg.get(ctx)) {
                printStream.println(funcName + "->" + func.getFuncName());
            }

        }
    }

    public void checkFuncCGF(Map<MethodSignature, Set<MethodSignature>> cfg)
            throws Exception {
        Map<String, Integer> funcs = cfg.keySet().stream().collect(
                Collectors.toMap(MethodSignature::getFuncName, func -> func.getArgCount()));
        for (MethodSignature ctx : cfg.keySet()) {
            for (MethodSignature funcSign : cfg.get(ctx)) {
                if (!funcs.containsKey(funcSign.getFuncName())) {
                    throw new Exception("Function with name \" " + funcSign.getFuncName() + " \" doesn't exist!");
                } else {
                    if (funcs.get(funcSign.getFuncName()) != funcSign.getArgCount()) {
                        throw new Exception("Function with name \" "
                                + funcSign.getFuncName()
                                + " \" and "
                                + funcSign.getArgCount()
                                + " arguments doesn't exist!");
                    }
                }
            }
        }
    }

    public Map<MethodSignature, Set<MethodSignature>> getFuncCFG(
            Set<SourceContext> sources) throws Exception {
        Map<MethodSignature, Set<MethodSignature>> funcCFG = new HashMap<>();
        for (SourceContext ctx : sources) {
            for (com.codeverification.Var3Parser.SourceItemContext item : ctx.sourceItem()) {
                CFGBetweenFuncsVisitor cfgVisitor = new CFGBetweenFuncsVisitor();
                cfgVisitor.visitFuncDef((FuncDefContext)item);
                MethodSignature methodSignature = new MethodSignature(
                        ((FuncDefContext)item).funcSignature().identifier().IDENTIFIER().getText(),
                        ((FuncDefContext)item).funcSignature().listArgDef().argDef().size());
                if (funcCFG.containsKey(methodSignature)) {
                    throw new Exception("Several functions with name "
                            + ((FuncDefContext)item).funcSignature().identifier().getText());
                }
                funcCFG.put(methodSignature, cfgVisitor.getSet());
            }
        }
        checkFuncCGF(funcCFG);
        return funcCFG;
    }

    public void printMnemCodes(List<CodeGenerationVisitor> visitors, String outputPath) throws FileNotFoundException {
        File file = new File(outputPath);
        file.getParentFile().mkdirs();
        try (PrintStream printStream = new PrintStream(new FileOutputStream(file))) {
            for (CodeGenerationVisitor visitor : visitors) {
                visitor.print(printStream);
                printStream.println("--------------------");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void printBinCodes(Map<String, MethodDefinition> binFuncs, String outputPath) throws IOException {
        File file = new File(outputPath);
        file.getParentFile().mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(binFuncs);
            oos.flush();
        } catch (Exception e) {
            throw e;
        }
    }

    public Map<String, MethodDefinition> readBinCodes(String path) throws IOException, ClassNotFoundException {
        try (ObjectInputStream oin = new ObjectInputStream(new FileInputStream(path))) {
            Map<String, MethodDefinition> methods = (Map<String, MethodDefinition>) oin.readObject();
            return methods;
        } catch (Exception e) {
            throw e;
        }
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
