package org.intellij.sdk.language.impl;

import org.intellij.sdk.language.psi.HaskellTypes;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.psi.TokenType;

public class HaskellParserUtil extends GeneratedParserUtilBase {
    public static boolean containsSpaces(PsiBuilder builder, int level) {
        return (builder.rawLookup(0) == HaskellTypes.HS_NEWLINE &&
                (builder.rawLookup(1) == TokenType.WHITE_SPACE ||
                        builder.rawLookup(1) == HaskellTypes.HS_COMMENT) ||
                builder.rawLookup(1) == HaskellTypes.HS_NCOMMENT ||
                builder.rawLookup(1) == HaskellTypes.HS_DIRECTIVE ||
                builder.rawLookup(1) == HaskellTypes.HS_HADDOCK ||
                builder.rawLookup(1) == HaskellTypes.HS_NHADDOCK) ||
                (builder.rawLookup(0) == HaskellTypes.HS_NEWLINE &&
                        builder.rawLookup(1) == HaskellTypes.HS_NEWLINE &&
                        builder.rawLookup(2) == TokenType.WHITE_SPACE) ||
                (builder.rawLookup(0) == HaskellTypes.HS_NEWLINE &&
                        builder.rawLookup(1) == HaskellTypes.HS_NEWLINE &&
                        builder.rawLookup(2) == HaskellTypes.HS_NEWLINE &&
                        builder.rawLookup(3) == TokenType.WHITE_SPACE) ||
                (builder.rawLookup(0) == HaskellTypes.HS_NEWLINE &&
                        builder.rawLookup(1) == HaskellTypes.HS_NEWLINE &&
                        builder.rawLookup(2) == HaskellTypes.HS_NEWLINE &&
                        builder.rawLookup(3) == HaskellTypes.HS_NEWLINE &&
                        builder.rawLookup(4) == TokenType.WHITE_SPACE) ||
                (builder.rawLookup(0) == HaskellTypes.HS_NEWLINE &&
                        builder.rawLookup(1) == HaskellTypes.HS_NEWLINE &&
                        builder.rawLookup(2) == HaskellTypes.HS_NEWLINE &&
                        builder.rawLookup(3) == HaskellTypes.HS_NEWLINE &&
                        builder.rawLookup(4) == HaskellTypes.HS_NEWLINE &&
                        builder.rawLookup(5) == TokenType.WHITE_SPACE) ||
                (builder.rawLookup(0) == HaskellTypes.HS_NEWLINE &&
                        builder.rawLookup(1) == HaskellTypes.HS_NEWLINE &&
                        builder.rawLookup(2) == HaskellTypes.HS_NEWLINE &&
                        builder.rawLookup(3) == HaskellTypes.HS_NEWLINE &&
                        builder.rawLookup(4) == HaskellTypes.HS_NEWLINE &&
                        builder.rawLookup(5) == HaskellTypes.HS_NEWLINE &&
                        builder.rawLookup(6) == TokenType.WHITE_SPACE) ||
                (builder.rawLookup(0) == HaskellTypes.HS_NEWLINE &&
                        builder.rawLookup(1) == HaskellTypes.HS_NEWLINE &&
                        builder.rawLookup(2) == HaskellTypes.HS_NEWLINE &&
                        builder.rawLookup(3) == HaskellTypes.HS_NEWLINE &&
                        builder.rawLookup(4) == HaskellTypes.HS_NEWLINE &&
                        builder.rawLookup(5) == HaskellTypes.HS_NEWLINE &&
                        builder.rawLookup(6) == HaskellTypes.HS_NEWLINE &&
                        builder.rawLookup(7) == TokenType.WHITE_SPACE);
    }

    public static boolean noSpaceAfterQualifier(PsiBuilder builder, int level) {
        return (builder.rawLookup(1) == HaskellTypes.HS_DOT);
    }
}
