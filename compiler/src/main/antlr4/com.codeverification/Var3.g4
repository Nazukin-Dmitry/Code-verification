grammar Var3;


source:
sourceItem* EOF
;

sourceItem
:'function' funcSignature statement* 'end' 'function' #funcDef
|'function' funcSignature 'from' library=STR #nativeFunc
;

funcSignature
:identifier '(' listArgDef ')' ('as' typeRef)?
;

argDef
:identifier ('as' typeRef)?
;


typeRef
:('bool'|'byte'|'int'|'uint'|'long'|'ulong'|'char'|'string') #builtin
 |identifier  #custom
;

statement
: 'if' expr 'then' trueSts += statement* ('else' falseSts += statement*)? 'end' 'if' #ifStatement
 |'while' expr statement* 'wend'  #whileStatement
 |'do' statement* 'loop' type=('while'|'until') expr  #doStatement
 |'break'  #breakStatement
 |expr ';'   #expressionStatement
;

expr
:'(' expr ')' #bracesExpr
 | expr '(' listExpr ')' #callExpr
 |  expr binOp expr #binaryExpr
 | unOp expr #unaryExpr
 | expr '=' expr #assignExpr
 | identifier #placeExpr
 | value = (BOOL|STR|CHAR|HEX|BITS|DEC) #literalExpr
;

listArgDef
:(argDef (',' argDef)*)?
;

listExpr
:(expr (',' expr)*)?
;

identifier: IDENTIFIER;

binOp
    : '+' | '-' | '*' | '/' | '%' | '>' | '<' | '==' | '&&' | '||'
    ;
unOp
    : '-'| '+'
    ;

WHILE: 'while';
UNTIL: 'until';

BOOL: 'true'|'false';
IDENTIFIER
 : [a-zA-Z_] [a-zA-Z_0-9]*
;
STR: '"' ( '\\"' | ~('\n'|'\r') )*? '"'; ///
CHAR: '\'' ~['] '\''; // одиночный символ в одинарных кавычках
HEX: '0'[xX][0-9A-Fa-f]+; // шестнадцатеричный литерал
BITS: '0' [bB][01]+; // битовый литерал
DEC: [0-9]+; // десятичный литерал

WS : [ \t\n\r]+->skip;