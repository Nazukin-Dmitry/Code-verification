package com.codeverification.interpretator;

/**
 * Created by 1 on 19.04.2017.
 */
public enum CommandType {
    ADD,
    MINUS,
    MULT,
    DIV,
    MOD,

    LESS,
    LARGER,
    EQUAL,

    UNADD,
    UNMINUS,

    PUSHVAR,
    LOADVAR,
    PUSHCONST,

    JMPFALSE,
    JMPTRUE,
    JMP,

    CALL;
}
