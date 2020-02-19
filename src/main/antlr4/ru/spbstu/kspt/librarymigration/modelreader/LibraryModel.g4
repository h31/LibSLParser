grammar LibraryModel;

start
   :   'library' libraryName '{' description '}' EOF
   ;

description
   :   (importSection
          |   includeSection
          |   typesSection
          |   convertersSection
          |   automatonDescription
          |   funDecl)+
   ;

//section
//   :   (typesSection
//   |   convertersSection
//   |   automatonDescription
//   |   funDecl)
//   ;

includeSection
   :   'includes' '{' includedStatement+ '}'
   ;

includedStatement
   :   includedName ';'
   ;


includedName
   :   Identifier
   ;

importSection
   :   'imports' '{' importedStatement+ '}'
   ;

importedStatement
   :   importedName ';'
   ;


importedName
   :   (Identifier|'.'|'/'|'\\')+
   ;

typesSection
   :   'types' '{' typeDecl+ '}'
   ;

typeDecl
   :   semanticType '(' codeType ')' ';'
   ;

semanticType
   :   semanticType '<' semanticType '>'
   |   semanticType arrayIdentifier
   |   semanticType pointerIdentifier
   |   pureSemanticType
   ;

arrayIdentifier
   :
   '[]'
   ;

pointerIdentifier
   :
   '*'
   ;

pureSemanticType
   : Identifier
   ;

codeType
   :   codeType '<' codeType '>'
   |   codeType arrayIdentifier
   |   codeType pointerIdentifier
   |   typeUnit
   ;

typeUnit
   :   prefixedSemanticType
   |   complexType
   ;

complexType
   :   Identifier ('.' complexType)?
   ;

prefixedSemanticType
   :   'sem:' pureSemanticType
   ;

convertersSection
   :   'converters' '{' converter+ '}'
   ;

converter
   :   destEntity '<-' converterExpression ';'
   ;

destEntity
   :   semanticType
   ;

converterExpression
   :   (Identifier|'('|')'|'"'|'<'|'>'|'.'|','|'/'|'\\'|'-')*
   ;

automatonDescription
   :   'automaton' automatonName '{' (stateDecl|finishstateDecl|shiftDecl|extendableFlag)* '}'
   ;

stateDecl
   :   'state' stateName (',' stateName)* ';'
   ;

finishstateDecl
   :   'finishstate' stateName ';'
   ;

stateName
   :   Identifier
   ;

shiftDecl
   :   'shift' srcState '->' dstState '(' funName (',' funName)* ')' ';'
   ;

srcState
   :   Identifier
   ;

dstState
   :   Identifier
   ;

automatonName
   :   semanticType
   ;

extendableFlag
   :   'extendable;'
   ;

funDecl
   :   'fun' entityName '.' funName '(' funArgs? ')' (':' funReturnType)? (';' | '{' funProperties* '}')
   ;

funProperties
   :   actionDecl
   |   'requires' ';'
   |   'when' ';'
   |   propertyDecl
   |   staticDecl
   ;

actionDecl
   :   'action' actionName '(' (','? Identifier)* ')' ';'
   ;

propertyDecl
   :   'property' '"' propertyKey '"' '=' '"' propertyValue '"' ';'
   ;

propertyKey
   :   Identifier
   ;

propertyValue
   :   Identifier
   ;

staticDecl
   :   'static' ('"' staticName '"')? ';'
   ;

staticName
   :   Identifier
   ;

actionName
   :   Identifier
   ;

entityName
   :   semanticType
   ;

funName
   :   Identifier
   ;


funArgs
   :   funArg (',' funArg)*
   ;

funArg
   :   argName ':' argType
   ;

argName
   : Identifier
   ;

argType
   :   semanticType
   ;

funReturnType
   :   semanticType
   ;

containsTreeType
   :   Identifier;

isTreeType
   :   Identifier;

variableType
   :   Identifier;

libraryName
   :   Identifier;

Identifier
   :   JavaLetter JavaLetterOrDigit*
   ;

//numberDesc
//    : orderType? DecNumbers
//    ;
//
//orderType
//    : ('>'|'<'|'<='|'>=')
//    ;
//
//DecNumbers
//    :   ('0'..'9')+
//    ;

fragment
JavaLetter
   :   [a-zA-Z$_] // these are the "java letters" below 0xFF
   |   // covers all characters above 0xFF which are not a surrogate
       ~[\u0000-\u00FF\uD800-\uDBFF]
       {Character.isJavaIdentifierStart(_input.LA(-1))}?
   |   // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
       [\uD800-\uDBFF] [\uDC00-\uDFFF]
       {Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
   ;

fragment
JavaLetterOrDigit
   :   [a-zA-Z0-9$_] // these are the "java letters or digits" below 0xFF
   |   // covers all characters above 0xFF which are not a surrogate
       ~[\u0000-\u00FF\uD800-\uDBFF]
       {Character.isJavaIdentifierPart(_input.LA(-1))}?
   |   // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
       [\uD800-\uDBFF] [\uDC00-\uDFFF]
       {Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
   ;

fragment
NEWLINE
  : '\r' '\n' | '\n' | '\r'
  ;

//
// Whitespace and comments
//

WS
   :   [ \t]+ -> skip
   ;

BR
   :   [\r\n\u000C]+ -> skip
   ;

COMMENT
   :   '/*' .*? '*/' -> skip
   ;

LINE_COMMENT
   :   '//' ~[\r\n]* -> skip
   ;

