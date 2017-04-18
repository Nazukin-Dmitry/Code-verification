package com.codeverification.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.codeverification.Var3Parser.ExprContext;
import com.codeverification.Var3Parser.FuncDefContext;
import com.codeverification.Var3Parser.FuncSignatureContext;
import com.codeverification.Var3Parser.SourceContext;
import com.codeverification.Var3Parser.SourceItemContext;

/**
 * @author Dmitrii Nazukin
 */
public class Zadanie4 {
    public static void main(String[] args) throws Exception {
        List<String> inDocs = new ArrayList<>();
        String outputBinPath = null;
        String outputMnemPath = null;
        String outputGraphPath = null;

        boolean startIn = false;
        for (int i = 0; i < args.length-1; i++) {
            String arg = args[i];
            if (arg.equals("-in")) {
                startIn = true;
                continue;
            }

            if (arg.equals("-outGraph")) {
                outputGraphPath = args[i+1];
                startIn = false;
            }
            if (arg.equals("-outMnem")) {
                outputMnemPath = args[i+1];
                startIn = false;
            }
            if (arg.equals("-outBin")) {
                outputBinPath = args[i+1];
                startIn = false;
            }

            if (startIn) {
                inDocs.add(arg);
            }

        }

        List<GraphNode<ExprContext>> graphs = new ArrayList<>();
        Map<FuncSignatureContext,  Set<MethodSignature>> funcCFG = new HashMap<>();
        List<CodeGenerationVisitor> mnemFuncs = new ArrayList<>();
        Map<String, MethodDefinition> binFuncs = new LinkedHashMap<>();

        try {
            ParserFacade parserFacade = new ParserFacade();
            Set<SourceContext> sources = new LinkedHashSet<>();
            for (String inDoc : inDocs) {
                SourceContext sourceContext = parserFacade.parse(inDoc);
                sources.add(sourceContext);
                for (SourceItemContext itemContext : sourceContext.sourceItem()) {
                    GraphNode<ExprContext> graph = parserFacade.createControlFlowGraph((FuncDefContext) itemContext);
                    graphs.add(graph);
                    CodeGenerationVisitor codeGenerationVisitor = new CodeGenerationVisitor();
                    codeGenerationVisitor.visit(itemContext);
                    mnemFuncs.add(codeGenerationVisitor);
                    MethodDefinition methodDefinition = codeGenerationVisitor.map2MethodDefinition();
                    binFuncs.put(methodDefinition.getMethodSignature().getFuncName(), methodDefinition);
                }
            }
            funcCFG = parserFacade.getFuncCFG(sources);
            if (outputGraphPath != null) {
                parserFacade.printCFG4(graphs, funcCFG, outputGraphPath);
            }
            if (outputMnemPath != null) {
                parserFacade.printMnemCodes(mnemFuncs, outputMnemPath);
            }
            if (outputBinPath != null) {
                parserFacade.printBinCodes(binFuncs, outputBinPath);
            }

            Map<String, MethodDefinition> methodDefinitions = parserFacade.readBinCodes(outputBinPath);
            System.out.println("fd");

        } catch (Exception e) {
            throw e;
//            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }
}
