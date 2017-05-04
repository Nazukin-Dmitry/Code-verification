package com.codeverification.compiler;

import java.util.HashSet;
import java.util.Set;

import com.codeverification.Var3Parser.ExprContext;
import com.codeverification.Var3Parser.FuncDefContext;
import com.codeverification.Var3Parser.SourceContext;
import com.codeverification.Var3Parser.SourceItemContext;

/**
 * @author Dmitrii Nazukin
 */
public class Zadanie3 {
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
                    GraphNode<ExprContext> graph = parserFacade.createControlFlowGraph((FuncDefContext) itemContext);
                    CodeGenerationVisitor codeGenerationVisitor = new CodeGenerationVisitor();
                    codeGenerationVisitor.visit(itemContext);
                    parserFacade.printCFG(graph,codeGenerationVisitor, outputPath + "\\" + ((FuncDefContext) itemContext).funcSignature().funcName.getText() + ".txt");

                }
            }
            parserFacade.printFuncCFG(outputPath + "\\" + "funcsCFG.txt", parserFacade.getFuncCFG(sources));

        } catch (Exception e) {
            throw e;
//            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }
}
