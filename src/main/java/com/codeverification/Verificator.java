package com.codeverification;

import com.codeverification.Var3Parser.SourceContext;

/**
 * Created by 1 on 08.04.2017.
 */
public class Verificator {
    public static void main(String[] args) {
        SourceContext source = ParserFacade.parse("F:\\учеба\\codeerification\\src\\main\\java\\com\\codeverification\\1out.txt");
        ParserFacade.printAST(source, "F:\\учеба\\codeerification\\src\\main\\java\\com\\codeverification\\1.txt");
    }


}
