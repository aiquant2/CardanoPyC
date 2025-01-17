package org.intellij.sdk.language.highlighter;



import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.intellij.sdk.language.HaskellLexerAdapter;
import org.intellij.sdk.language.psi.HaskellTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;




    public class HaskellSyntaxHighlighter extends SyntaxHighlighterBase {

        public static final TextAttributesKey ILLEGAL =
                createTextAttributesKey("HS_ILLEGAL", DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE);
        public static final TextAttributesKey COMMENT =
                createTextAttributesKey("HS_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
        public static final TextAttributesKey DOC_COMMENT =
                createTextAttributesKey("HS_HADDOCK", DefaultLanguageHighlighterColors.DOC_COMMENT);
        /*public static final TextAttributesKey BLOCK_DOC_COMMENT =
                createTextAttributesKey("HS_NHADDOCK", DefaultLanguageHighlighterColors.DOC_COMMENT);*/
        public static final TextAttributesKey STRING =
                createTextAttributesKey("HS_STRING", DefaultLanguageHighlighterColors.STRING);
        public static final TextAttributesKey NUMBER =
                createTextAttributesKey("HS_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
        public static final TextAttributesKey KEYWORD =
                createTextAttributesKey("HS_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
        public static final TextAttributesKey FUNCTION_NAME =
                createTextAttributesKey("HS_FUNCTION_NAME", DefaultLanguageHighlighterColors.FUNCTION_CALL);
        public static final TextAttributesKey PARENTHESES =
                createTextAttributesKey("HS_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES);
        public static final TextAttributesKey BRACE =
                createTextAttributesKey("HSL_BRACE", DefaultLanguageHighlighterColors.BRACES);
        public static final TextAttributesKey BRACKET =
                createTextAttributesKey("HS_BRACKET", DefaultLanguageHighlighterColors.BRACKETS);
        public static final TextAttributesKey VARIABLE =
                createTextAttributesKey("HS_VARIABLE", DefaultLanguageHighlighterColors.LOCAL_VARIABLE);
        public static final TextAttributesKey PRAGMA_CONTENT =
                createTextAttributesKey("HS_PRAGMA_CONTENT", DefaultLanguageHighlighterColors.METADATA);
        public static final TextAttributesKey CONSTRUCTOR =
                createTextAttributesKey("HS_CONSTRUCTOR", DefaultLanguageHighlighterColors.INSTANCE_FIELD);

        public static final TextAttributesKey RESERVED_SYMBOL =
                createTextAttributesKey("HS_SYMBOL", DefaultLanguageHighlighterColors.PREDEFINED_SYMBOL);
        public static final TextAttributesKey PRAGMA =
                createTextAttributesKey("HS_PRAGMA", DefaultLanguageHighlighterColors.METADATA);
        public static final TextAttributesKey QUASIQUOTE =
                createTextAttributesKey("HS_QUASI_QUOTES", DefaultLanguageHighlighterColors.METADATA);
        public static final TextAttributesKey DEFAULT =
                createTextAttributesKey("HS_DEFAULT", DefaultLanguageHighlighterColors.LOCAL_VARIABLE);
        public static final TextAttributesKey DERIVING_KEYWORD =
                createTextAttributesKey("HS_DERIVING", DefaultLanguageHighlighterColors.KEYWORD);
        public static final TextAttributesKey TYPE =
                createTextAttributesKey("HS_CON_ID", DefaultLanguageHighlighterColors.CONSTANT);
        public static final TextAttributesKey  VARSYM=
                createTextAttributesKey("HS_VARSYM_ID", DefaultLanguageHighlighterColors.METADATA);
        public static final TextAttributesKey OPERATOR =
                createTextAttributesKey("HS_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);
        public static final TextAttributesKey FUNCTION_DECLARATION =
                createTextAttributesKey("HS_FUNCTION_DECLARATION", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);



        private static final TextAttributesKey[] ILLEGAL_KEYS = new TextAttributesKey[]{ILLEGAL};
        private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
        private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER};
        private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};
        private static final TextAttributesKey[] FUNCTION_NAME_KEYS = new TextAttributesKey[]{FUNCTION_NAME};
        private static final TextAttributesKey[] PARENTHESES_KEYS = new TextAttributesKey[]{PARENTHESES};
        private static final TextAttributesKey[] BRACE_KEYS = new TextAttributesKey[]{BRACE};
        private static final TextAttributesKey[] BRACKET_KEYS = new TextAttributesKey[]{BRACKET};
        private static final TextAttributesKey[] VARIABLE_KEYS = new TextAttributesKey[]{VARIABLE};
        private static final TextAttributesKey[] PRAGMA_CONTENT_KEYS = new TextAttributesKey[]{PRAGMA_CONTENT};
        private static final TextAttributesKey[] CONSTRUCTOR_KEYS = new TextAttributesKey[]{CONSTRUCTOR};
        private static final TextAttributesKey[] OPERATOR_KEYS = new TextAttributesKey[]{VARSYM};
        private static final TextAttributesKey[] RESERVED_SYMBOL_KEYS = new TextAttributesKey[]{RESERVED_SYMBOL};
        private static final TextAttributesKey[] PRAGMA_KEYS = new TextAttributesKey[]{PRAGMA};
        private static final TextAttributesKey[] QUASIQUOTE_KEYS = new TextAttributesKey[]{QUASIQUOTE};
        private static final TextAttributesKey[] DEFAULT_KEYS = new TextAttributesKey[]{DEFAULT};
        private static final TextAttributesKey[] DERIVING_KEYWORD_KEYS = new TextAttributesKey[]{DERIVING_KEYWORD};
        private static final TextAttributesKey[] TYPE_KEYS = new TextAttributesKey[]{TYPE};
        private static final TextAttributesKey[] COMMENT_KEY = new TextAttributesKey[]{COMMENT};
        private static final TextAttributesKey[] DOCCOMMENT_KEY = new TextAttributesKey[]{DOC_COMMENT};
        private static final TextAttributesKey[] FUNCTION_DECLARATION_KEY = new TextAttributesKey[]{FUNCTION_DECLARATION};
        @Override
        public Lexer getHighlightingLexer() {
            return new HaskellLexerAdapter();
        }

        @Override
        public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
            if (tokenType == TokenType.BAD_CHARACTER) {
                return ILLEGAL_KEYS;
            }

            if (tokenType.equals(HaskellTypes.HS_STRING_LITERAL) || tokenType.equals(HaskellTypes.HS_CHARACTER_LITERAL)
                    || tokenType.equals(HaskellTypes.HS_DOUBLE_QUOTES)) {
                return STRING_KEYS;
            }
            if (tokenType.equals(HaskellTypes.HS_FLOAT) || tokenType.equals(HaskellTypes.HS_DECIMAL)
                    || tokenType.equals(HaskellTypes.HS_HEXADECIMAL) || tokenType.equals(HaskellTypes.HS_OCTAL)) {
                return NUMBER_KEYS;
            }
            if (isKeyword(tokenType)) {
                return KEYWORD_KEYS;
            }
            if (tokenType.equals(HaskellTypes.HS_DERIVING)) {
                return DERIVING_KEYWORD_KEYS;
            }

            if (tokenType.equals(HaskellTypes.HS_Q_NAME) || tokenType.equals(HaskellTypes.HS_Q_NAMES)) {
                return FUNCTION_NAME_KEYS;
            }
            if (isConstructor(tokenType)) {
                return CONSTRUCTOR_KEYS;
            }
            if (tokenType.equals(HaskellTypes.HS_VARID) || tokenType.equals(HaskellTypes.HS_VAR_CON)) {
                return VARIABLE_KEYS;
            }
            if (isOperator(tokenType)) {
                return OPERATOR_KEYS;
            }
            if (isPragma(tokenType)) {
                return PRAGMA_KEYS;
            }
            if (tokenType.equals(HaskellTypes.HS_QUASI_QUOTE)) {
                return QUASIQUOTE_KEYS;
            }
            if (isDelimiter(tokenType)) {
                return getDelimiterKeys(tokenType);
            }
            if (isComment(tokenType)) {
                return COMMENT_KEY;
            }
            if (isDocComment(tokenType)) {
                return DOCCOMMENT_KEY;
            }
            if (tokenType.equals(HaskellTypes.HS_GENERAL_PRAGMA_CONTENT)) {
                return PRAGMA_CONTENT_KEYS;
            }
            if (tokenType.equals(HaskellTypes.HS_CON_ID)) { // Add highlighting for HS_CON_ID
                return TYPE_KEYS;
            }
            if (tokenType.equals(HaskellTypes.HS_FUNCTION_DECLARATION)) { // Add highlighting for HS_CON_ID
                return FUNCTION_DECLARATION_KEY;
            }
            // Add other specific token mappings as needed

            return DEFAULT_KEYS;
        }

        private boolean isKeyword(IElementType tokenType) {
            return tokenType.equals(HaskellTypes.HS_IMPORT) ||
                    tokenType.equals(HaskellTypes.HS_MODULE) ||
                    tokenType.equals(HaskellTypes.HS_WHERE) ||
                    tokenType.equals(HaskellTypes.HS_DO) ||
                    tokenType.equals(HaskellTypes.HS_LET) ||
                    tokenType.equals(HaskellTypes.HS_OF) ||
                    tokenType.equals(HaskellTypes.HS_THEN) ||
                    tokenType.equals(HaskellTypes.HS_ELSE) ||
                    tokenType.equals(HaskellTypes.HS_CASE) ||
                    tokenType.equals(HaskellTypes.HS_IF) ||
                    tokenType.equals(HaskellTypes.HS_DATA) ||
                    tokenType.equals(HaskellTypes.HS_TYPE) ||
                    tokenType.equals(HaskellTypes.HS_NEWTYPE) ||
                    tokenType.equals(HaskellTypes.HS_CLASS) ||
                    tokenType.equals(HaskellTypes.HS_INSTANCE) ||
                    tokenType.equals(HaskellTypes.HS_DEFAULT) ||
                    tokenType.equals(HaskellTypes.HS_EXPORT) ||
                    tokenType.equals(HaskellTypes.HS_FORALL) ||
                    tokenType.equals(HaskellTypes.HS_INFIX) ||
                    tokenType.equals(HaskellTypes.HS_INFIXL) ||
                    tokenType.equals(HaskellTypes.HS_INFIXR) ||
                    tokenType.equals(HaskellTypes.HS_CCONTEXT) ||
                    tokenType.equals(HaskellTypes.HS_CDECL) ||
                    tokenType.equals(HaskellTypes.HS_CDECLS) ||
                    tokenType.equals(HaskellTypes.HS_CDECL_DATA_DECLARATION) ||
                    tokenType.equals(HaskellTypes.HS_CIDECL) ||
                    tokenType.equals(HaskellTypes.HS_CIDECLS) ||
                    tokenType.equals(HaskellTypes.HS_CLASS_DECLARATION) ||
                    tokenType.equals(HaskellTypes.HS_CLAZZ) ||
                    tokenType.equals(HaskellTypes.HS_CONSTR) ||
                    tokenType.equals(HaskellTypes.HS_CONSTR_1) ||
                    tokenType.equals(HaskellTypes.HS_DATA_DECLARATION_DERIVING) ||
                    tokenType.equals(HaskellTypes.HS_DEFAULT_DECLARATION) ||
                    tokenType.equals(HaskellTypes.HS_DERIVING_DECLARATION) ||
                    tokenType.equals(HaskellTypes.HS_DERIVING_VIA) ||
                    tokenType.equals(HaskellTypes.HS_CONSTR_2) ||
                    tokenType.equals(HaskellTypes.HS_CONSTR_3) ||
                    tokenType.equals(HaskellTypes.HS_DATA_DECLARATION) ||
                    tokenType.equals(HaskellTypes.HS_EXPORTS) ||
                    tokenType.equals(HaskellTypes.HS_FIXITY_DECLARATION) ||
                    tokenType.equals(HaskellTypes.HS_FOREIGN_DECLARATION) ||
                    tokenType.equals(HaskellTypes.HS_IMPORT_QUALIFIED) ||
                    tokenType.equals(HaskellTypes.HS_IMPORT_QUALIFIED_AS) ||
                    tokenType.equals(HaskellTypes.HS_IMPORT_DECLARATION) ||
                    tokenType.equals(HaskellTypes.HS_IMPORT_DECLARATIONS) ||
                    tokenType.equals(HaskellTypes.HS_IMPORT_EMPTY_SPEC) ||
                    tokenType.equals(HaskellTypes.HS_IMPORT_HIDING) ||
                    tokenType.equals(HaskellTypes.HS_IMPORT_HIDING_SPEC) ||
                    tokenType.equals(HaskellTypes.HS_IMPORT_ID) ||
                    tokenType.equals(HaskellTypes.HS_IMPORT_IDS_SPEC) ||
                    tokenType.equals(HaskellTypes.HS_IMPORT_PACKAGE_NAME) ||
                    tokenType.equals(HaskellTypes.HS_INST) ||
                    tokenType.equals(HaskellTypes.HS_INSTANCE_DECLARATION) ||
                    tokenType.equals(HaskellTypes.HS_INSTVAR) ||
                    tokenType.equals(HaskellTypes.HS_KIND_SIGNATURE) ||
                    tokenType.equals(HaskellTypes.HS_LIST_TYPE) ||
                    tokenType.equals(HaskellTypes.HS_MODID) ||
                    tokenType.equals(HaskellTypes.HS_MODULE_DECLARATION) ||
                    tokenType.equals(HaskellTypes.HS_NEWTYPE_DECLARATION) ||
                    tokenType.equals(HaskellTypes.HS_QUALIFIER) ||
                    tokenType.equals(HaskellTypes.HS_SCONTEXT) ||
                    tokenType.equals(HaskellTypes.HS_SIMPLECLASS) ||
                    tokenType.equals(HaskellTypes.HS_SIMPLETYPE) ||
                    tokenType.equals(HaskellTypes.HS_TTYPE) ||
                    tokenType.equals(HaskellTypes.HS_TYPE_DECLARATION) ||
                    tokenType.equals(HaskellTypes.HS_TYPE_EQUALITY) ||
                    tokenType.equals(HaskellTypes.HS_TYPE_FAMILY_DECLARATION) ||
                    tokenType.equals(HaskellTypes.HS_TYPE_FAMILY_TYPE) ||
                    tokenType.equals(HaskellTypes.HS_TYPE_INSTANCE_DECLARATION) ||
                    tokenType.equals(HaskellTypes.HS_TYPE_SIGNATURE);


        }
        private boolean isComment(IElementType tokenType){
            return tokenType.equals(HaskellTypes.HS_COMMENT) ||
                    tokenType.equals(HaskellTypes.HS_NCOMMENT);
        }
        private boolean isDocComment(IElementType tokenType){
            return tokenType.equals(HaskellTypes.HS_HADDOCK) ||
                    tokenType.equals(HaskellTypes.HS_NHADDOCK);
        }

        private boolean isConstructor(IElementType tokenType) {
            return tokenType.equals(HaskellTypes.HS_CONID) ||
                    tokenType.equals(HaskellTypes.HS_Q_CON) ||
                    tokenType.equals(HaskellTypes.HS_Q_CON_QUALIFIER) ||
                    tokenType.equals(HaskellTypes.HS_Q_CON_QUALIFIER_1) ||
                    tokenType.equals(HaskellTypes.HS_Q_CON_QUALIFIER_2) ||
                    tokenType.equals(HaskellTypes.HS_Q_CON_QUALIFIER_3) ||
                    tokenType.equals(HaskellTypes.HS_Q_CON_QUALIFIER_4) ||
                    tokenType.equals(HaskellTypes.HS_GTYCON) ||
                    tokenType.equals(HaskellTypes.HS_NEWCONSTR) ||
                    tokenType.equals(HaskellTypes.HS_NEWCONSTR_FIELDDECL);
        }


        private boolean isOperator(IElementType tokenType) {
            return tokenType.equals(HaskellTypes.HS_CONSYM) ||
                    tokenType.equals(HaskellTypes.HS_VARSYM) ||
                    tokenType.equals(HaskellTypes.HS_AT) ||
                    tokenType.equals(HaskellTypes.HS_BACKQUOTE) ||
                    tokenType.equals(HaskellTypes.HS_BACKSLASH) ||
                    tokenType.equals(HaskellTypes.HS_COLON_COLON) ||
                    tokenType.equals(HaskellTypes.HS_CONSYM_ID) ||
                    tokenType.equals(HaskellTypes.HS_DASH) ||
                    tokenType.equals(HaskellTypes.HS_DOT) ||
                    tokenType.equals(HaskellTypes.HS_DOUBLE_RIGHT_ARROW) ||
                    tokenType.equals(HaskellTypes.HS_COMMA) ||
                    tokenType.equals(HaskellTypes.HS_HASH) ||
                    tokenType.equals(HaskellTypes.HS_LEFT_ARROW) ||
                    tokenType.equals(HaskellTypes.HS_QUOTE) ||
                    tokenType.equals(HaskellTypes.HS_RIGHT_ARROW) ||
                    tokenType.equals(HaskellTypes.HS_TILDE) ||
                    tokenType.equals(HaskellTypes.HS_VARSYM_ID) ||
                    tokenType.equals(HaskellTypes.HS_VERTICAL_BAR);
        }

        private boolean isPragma(IElementType tokenType) {
            return tokenType.equals(HaskellTypes.HS_PRAGMA) ||
                    tokenType.equals(HaskellTypes.HS_ONE_PRAGMA) ||
                    tokenType.equals(HaskellTypes.HS_PRAGMA_END) ||
                    tokenType.equals(HaskellTypes.HS_PRAGMA_SEP) ||
                    tokenType.equals(HaskellTypes.HS_PRAGMA_START);
        }

        private boolean isDelimiter(IElementType tokenType) {
            return tokenType.equals(HaskellTypes.HS_LEFT_PAREN) ||
                    tokenType.equals(HaskellTypes.HS_RIGHT_PAREN) ||
                    tokenType.equals(HaskellTypes.HS_LEFT_BRACE) ||
                    tokenType.equals(HaskellTypes.HS_RIGHT_BRACE) ||
                    tokenType.equals(HaskellTypes.HS_LEFT_BRACKET) ||
                    tokenType.equals(HaskellTypes.HS_RIGHT_BRACKET);
        }

        private TextAttributesKey[] getDelimiterKeys(IElementType tokenType) {
            if (tokenType.equals(HaskellTypes.HS_LEFT_PAREN) || tokenType.equals(HaskellTypes.HS_RIGHT_PAREN)) {
                return PARENTHESES_KEYS;
            }
            if (tokenType.equals(HaskellTypes.HS_LEFT_BRACE) || tokenType.equals(HaskellTypes.HS_RIGHT_BRACE)) {
                return BRACE_KEYS;
            }
            if (tokenType.equals(HaskellTypes.HS_LEFT_BRACKET) || tokenType.equals(HaskellTypes.HS_RIGHT_BRACKET)) {
                return BRACKET_KEYS;
            }
            return DEFAULT_KEYS;
        }
    }