package com.codeverification.compiler;

import com.codeverification.Var3Parser.SourceContext;

/**
 * Created by 1 on 08.04.2017.
 */
public class Verificator {
    public static void main(String[] args) {
        try {
            ParserFacade parserFacade = new ParserFacade();
            SourceContext source = parserFacade.parse("F:\\study\\codeerification\\compiler\\src\\main\\java\\com\\codeverification\\compiler\\1.txt");
            parserFacade.printAST(source, "F:\\study\\codeerification\\compiler\\src\\main\\java\\com\\codeverification\\compiler\\1out.txt");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
//        GraphNode<com.codeverification.Var3Parser.ExprContext> graph = parserFacade.createControlFlowGraph(source);
//        parserFacade.printCFG(graph);
//        System.out.println("hello");
//        System.out.println(graph.getNodeValue().getText());
    }




}
