package org.intellij.sdk.language.psi;

import com.intellij.psi.tree.IElementType;
import org.intellij.sdk.language.HaskellLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NonNls;

public class HaskellTokenType extends IElementType {

    public HaskellTokenType(@NotNull @NonNls String debugName) {
        super(debugName, HaskellLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "HaskellTokenType." + super.toString();
    }

}