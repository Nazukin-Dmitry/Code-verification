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

/**
 * Created by 1 on 08.04.2017.
 */
public class ParserFacade {
    private com.codeverification.Var3Parser parser;
    private com.codeverification.Var3Lexer lexer;

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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        explore(ctx, 0, printStream);
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

    private Map<GraphNode<ExprContext>, Integer> map = new HashMap<>();
    private int count = 0;

    public void printCFG(GraphNode<ExprContext> node) {
            map.put(node, count);
            count++;
            System.out.println("start" +" -> " + (count-1));
            printCFG(node, 0);
        map.entrySet().stream().sorted((o1, o2) -> Integer.compare(o1.getValue(), o2.getValue())).forEach(s -> System.out.println(s.getValue() + ":" + s.getKey().getNodeValue().getText()));
    }

    public void printCFG(GraphNode<ExprContext> node, int space) {

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
            for (int i = 0; i < space; i++) {
                System.out.print("  ");
            }
            System.out.print(map.get(node)+" -> ");

            if (node.getNextNode() == null) {
                System.out.println("end");
                return;
            } else {
                GraphNode<ExprContext> nextNode = node.getNextNode();
                if (nextNode.getNodeValue() == null) {
                    nextNode = nextNode.getNextNode();
                    if (nextNode == null) {
                        System.out.println("end");
                        return;
                    }
                }

                if (map.containsKey(nextNode)) {
                    System.out.println(map.get(nextNode));
                    return;
                } else {
                    map.put(nextNode, count++);
                    System.out.println(count-1);
                    printCFG(nextNode, space);
                }
            }

        }
        if (node instanceof ConditionGraphNode) {
            for (int i = 0; i < space; i++) {
                System.out.print("  ");
            }
            System.out.print(map.get(node)+" -> ");

            if (((ConditionGraphNode) node).getTrueNextNode() == null) {
                System.out.println("end"+ " (true)");
            } else {
                GraphNode<ExprContext> nextTrueNode = ((ConditionGraphNode) node).getTrueNextNode();
                if (nextTrueNode.getNodeValue() == null) {
                    nextTrueNode = nextTrueNode.getNextNode();
                }
                if (nextTrueNode == null) {
                    System.out.println("end"+ " (true)");
                } else {

                    if (map.containsKey(nextTrueNode)) {
                        System.out.println(map.get(nextTrueNode) + " (true)");
                    } else {
                        map.put(nextTrueNode, count++);
                        System.out.println(count - 1 + " (true)");
                        printCFG(nextTrueNode, space + 1);
                    }
                }
            }

            for (int i = 0; i < space; i++) {
                System.out.print("  ");
            }
            System.out.print(map.get(node)+" -> ");
            if (((ConditionGraphNode) node).getFalseNextNode() == null) {
                System.out.println("end"+ " (false)");
            } else {
                GraphNode<ExprContext> nextFalseNode = ((ConditionGraphNode) node).getFalseNextNode();
                if (nextFalseNode.getNodeValue() == null) {
                    nextFalseNode = nextFalseNode.getNextNode();
                }
                if (nextFalseNode == null) {
                    System.out.println("end"+ " (false)");
                }else {
                    if (map.containsKey(nextFalseNode)) {
                        System.out.println(map.get(nextFalseNode) + " (false)");
                    } else {
                        map.put(nextFalseNode, count++);
                        System.out.println(count - 1 + " (false)");
                        printCFG(nextFalseNode, space + 1);
                    }
                }
            }
        }
    }

//    private void printCFG(GraphNode<ExprContext> node, int incr) {
//        for (int i = 0; i < incr; i++) {
//            System.out.println("  ");
//        }
//        if (node instanceof OrdinaryGraphNode) {
//            System.out.println(node.getNodeValue().getText() + "->" + node.getNextNode().getNodeValue().getText());
//            if (!map.containsKey(node)) {
//                printCFG(node.getNextNode(), incr++);
//            }
//        }
//        if (node instanceof ConditionGraphNode) {
//            System.out.println("Success:" + node.getNodeValue().getText() + "->" + ((ConditionGraphNode<ExprContext>) node).getTrueNextNode().getNodeValue().getText());
//            System.out.println("Success:" + node.getNodeValue().getText() + "->" + ((ConditionGraphNode<ExprContext>) node).getFalseNextNode().getNodeValue().getText());
//            incr++;
//            printCFG(((ConditionGraphNode<ExprContext>) node).getTrueNextNode(), incr);
//            printCFG(((ConditionGraphNode<ExprContext>) node).getFalseNextNode(), incr);
//        }
//    }


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
