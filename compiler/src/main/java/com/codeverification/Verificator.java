package com.codeverification;

import com.codeverification.Var3Parser.SourceContext;

/**
 * Created by 1 on 08.04.2017.
 */
public class Verificator {
    public static void main(String[] args) {
        try {
            ParserFacade parserFacade = new ParserFacade();
            SourceContext source = parserFacade.parse("C:\\Users\\dnazukin\\code_verification\\src\\main\\java\\com\\codeverification\\1all.txt");
            parserFacade.printAST(source, "C:\\Users\\dnazukin\\code_verification\\src\\main\\java\\com\\codeverification\\1out.txt");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
//        GraphNode<com.codeverification.Var3Parser.ExprContext> graph = parserFacade.createControlFlowGraph(source);
//        parserFacade.printCFG(graph);
//        System.out.println("hello");
//        System.out.println(graph.getNodeValue().getText());
    }




}
