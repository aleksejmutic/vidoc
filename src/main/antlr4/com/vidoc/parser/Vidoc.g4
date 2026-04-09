grammar Vidoc;

program
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
    | screenshotModeAction
    ;

// ─────────────────────────────────────────────
//  EXPLICIT TOKENS FOR MODES OF SCREENSHOTS
// ─────────────────────────────────────────────
SCREENSHOT_ALL    : '"all"' ;
SCREENSHOT_MANUAL : '"manual"' ;

// ─────────────────────────────────────────────
//  NAVIGATION
// ─────────────────────────────────────────────

gotoAction
    : 'goto' stringValue
    ;

// ─────────────────────────────────────────────
//  INTERACTION
// ─────────────────────────────────────────────

clickAction
    : 'click' selectorValue
    ;

typeAction
    : 'type' selectorValue ':' stringOrVariable
    ;

clearAction
    : 'clear' selectorValue
    ;

hoverAction
    : 'hover' selectorValue
    ;

scrollToAction
    : 'scrollTo' selectorValue
    ;

scrollUpAction
    : 'scrollUp' INT
    ;

scrollDownAction
    : 'scrollDown' INT
    ;

dragAndDropAction
    : 'dragAndDrop' selectorValue ':' selectorValue
    ;

selectAction
    : 'select' selectorValue ':' stringValue
    ;

// ─────────────────────────────────────────────
//  KEYBOARD
// ─────────────────────────────────────────────

pressAction
    : 'press' stringValue
    ;

keyDownAction
    : 'keyDown' stringValue
    ;

keyUpAction
    : 'keyUp' stringValue
    ;

// ─────────────────────────────────────────────
//  WAITING
// ─────────────────────────────────────────────

waitAction
    : 'wait' selectorValue
    ;

waitForAction
    : 'waitFor' INT
    ;

// ─────────────────────────────────────────────
//  ASSERTIONS
// ─────────────────────────────────────────────

assertAction
    : 'assert' selectorValue ':' stringValue
    ;

assertVisibleAction
    : 'assertVisible' selectorValue
    ;

assertHiddenAction
    : 'assertHidden' selectorValue
    ;

assertUrlAction
    : 'assertUrl' stringValue
    ;

// ─────────────────────────────────────────────
//  DOCUMENTATION
// ─────────────────────────────────────────────

screenshotAction
    : 'screenshot' selectorValue?
    ;

highlightAction
    : 'highlight' selectorValue
    ;

screenshotModeAction
    : 'screenshotmode' (SCREENSHOT_ALL | SCREENSHOT_MANUAL)
    ;
// ─────────────────────────────────────────────
//  VARIABLES
// ─────────────────────────────────────────────

setAction
    : 'set' VAR ':' stringValue
    ;

// ─────────────────────────────────────────────
//  VALUE RULES
// ─────────────────────────────────────────────

selectorValue
    : STRING
    ;

stringValue
    : STRING
    ;

stringOrVariable
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