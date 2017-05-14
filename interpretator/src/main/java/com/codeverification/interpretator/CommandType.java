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

    OR,
    AND,

    UNADD,
    UNMINUS,

    PUSHVAR,
    PUSHCLASSVAR,
    LOADVAR,
    LOADCLASSVAR,
    PUSHCONST,

    JMPFALSE,
    JMPTRUE,
    JMP,

    CALL,
    CALLOBJECTFUN,

    INITIALIZE,
    LOADOBJECTFIELD,
    PUSHOBJECTFIELD
    ;
}
