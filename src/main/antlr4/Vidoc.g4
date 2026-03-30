grammar Vidoc;

prog
    : statement+ EOF
    ;

statement
    : comment
    | command
    ;

comment
    : COMMENT
    ;

command
    : gotoAction
    | clickAction
    | typeAction
    | clearAction
    | hoverAction
    | scrollToAction
    | scrollUpAction
    | scrollDownAction
    | dragAndDropAction
    | selectAction
    | pressAction
    | keyDownAction
    | keyUpAction
    | waitAction
    | waitForAction
    | assertAction
    | assertVisibleAction
    | assertHiddenAction
    | assertUrlAction
    | screenshotAction
    | highlightAction
    | setAction
    ;

// ─────────────────────────────────────────────
//  NAVIGATION
// ─────────────────────────────────────────────

gotoAction
    : 'goto' stringVal
    ;

// ─────────────────────────────────────────────
//  INTERACTION
// ─────────────────────────────────────────────

clickAction
    : 'click' selectorVal
    ;

typeAction
    : 'type' selectorVal ':' stringOrVar
    ;

clearAction
    : 'clear' selectorVal
    ;

hoverAction
    : 'hover' selectorVal
    ;

scrollToAction
    : 'scrollTo' selectorVal
    ;

scrollUpAction
    : 'scrollUp' INT
    ;

scrollDownAction
    : 'scrollDown' INT
    ;

dragAndDropAction
    : 'dragAndDrop' selectorVal ':' selectorVal
    ;

selectAction
    : 'select' selectorVal ':' stringVal
    ;

// ─────────────────────────────────────────────
//  KEYBOARD
// ─────────────────────────────────────────────

pressAction
    : 'press' stringVal
    ;

keyDownAction
    : 'keyDown' stringVal
    ;

keyUpAction
    : 'keyUp' stringVal
    ;

// ─────────────────────────────────────────────
//  WAITING
// ─────────────────────────────────────────────

waitAction
    : 'wait' selectorVal
    ;

waitForAction
    : 'waitFor' INT
    ;

// ─────────────────────────────────────────────
//  ASSERTIONS
// ─────────────────────────────────────────────

assertAction
    : 'assert' selectorVal ':' stringVal
    ;

assertVisibleAction
    : 'assertVisible' selectorVal
    ;

assertHiddenAction
    : 'assertHidden' selectorVal
    ;

assertUrlAction
    : 'assertUrl' stringVal
    ;

// ─────────────────────────────────────────────
//  DOCUMENTATION
// ─────────────────────────────────────────────

screenshotAction
    : 'screenshot' selectorVal?
    ;

highlightAction
    : 'highlight' selectorVal
    ;

// ─────────────────────────────────────────────
//  VARIABLES
// ─────────────────────────────────────────────

setAction
    : 'set' VAR ':' stringVal
    ;

// ─────────────────────────────────────────────
//  VALUE RULES
// ─────────────────────────────────────────────

selectorVal
    : STRING
    ;

stringVal
    : STRING
    ;

stringOrVar
    : STRING
    | VAR
    ;

// ─────────────────────────────────────────────
//  LEXER RULES
// ─────────────────────────────────────────────

COMMENT
    : '#' ~[\r\n]* '\r'? '\n'
    ;

STRING
    : '"' (~["\r\n])* '"'
    ;

VAR
    : '$' [a-zA-Z_][a-zA-Z0-9_]*
    ;

INT
    : [0-9]+
    ;

WS
    : [ \t\r\n]+ -> skip
    ;