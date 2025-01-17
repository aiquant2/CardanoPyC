package org.intellij.sdk.language.psi;

import com.intellij.psi.tree.TokenSet;

public interface HaskellTokenSets{

    TokenSet COMMENTS = TokenSet.create(HaskellTypes.HS_COMMENT,HaskellTypes.HS_NCOMMENT);
    TokenSet FUNCTION_DECLARATION=TokenSet.create(HaskellTypes.HS_FUNCTION_DECLARATION);
}