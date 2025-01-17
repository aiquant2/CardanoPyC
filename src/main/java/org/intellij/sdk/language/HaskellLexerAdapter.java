package org.intellij.sdk.language;

import com.intellij.lexer.FlexAdapter;
import org.intellij.sdk.language.grammars.HaskellLexer;

public class HaskellLexerAdapter extends FlexAdapter {
    public HaskellLexerAdapter() {
        super(new HaskellLexer(null));
    }
}
