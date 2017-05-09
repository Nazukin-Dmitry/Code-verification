package com.codeverification.compiler;

import com.codeverification.Var3Parser.ExprContext;
import com.codeverification.Var3Parser.SourceContext;
import com.codeverification.Var3Parser.SourceItemContext;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by 1 on 11.04.2017.
 */
public class Zadanie2 {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Enter at least 2 arguments!!!");
            return;
        }
        try {
            String outputPath = args[args.length - 1];

            ParserFacade parserFacade = new ParserFacade();
            Set<SourceContext> sources = new HashSet<>();
            for (int i = 0; i < args.length - 1; i++) {
                SourceContext sourceContext = parserFacade.parse(args[i]);
                sources.add(sourceContext);
                for (SourceItemContext itemContext : sourceContext.sourceItem()) {
                    GraphNode<ExprContext> graph = parserFacade.createControlFlowGraph(itemContext.funcDef());
                    parserFacade.printCFG(graph, outputPath + "\\" + itemContext.funcDef().funcSignature().funcName.getText() + ".txt");
                }
            }

            parserFacade.printFuncCFG(outputPath + "\\" + "funcsCFG.txt", parserFacade.getFuncCFG(sources));
        } catch (Exception e) {
            throw e;
//            System.out.println(Arrays.toString(e.getStackTrace()));
        }

//        parserFacade.printAST(source, "F:\\учеба\\codeerification\\src\\main\\java\\com\\codeverification\\1out.txt");
//        GraphNode<com.codeverification.Var3Parser.ExprContext> graph = parserFacade.createControlFlowGraph(source);
//        parserFacade.printCFG(graph);
//        System.out.println("hello");
//        System.out.println(graph.getNodeValue().getText());
    }
}
