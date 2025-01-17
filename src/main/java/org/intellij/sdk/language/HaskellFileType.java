package org.intellij.sdk.language;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.intellij.sdk.language.icons.HaskellIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public final class HaskellFileType extends LanguageFileType {

    public static final HaskellFileType INSTANCE = new HaskellFileType();

    private HaskellFileType() {
        super(HaskellLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Haskell File";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Haskell language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "hs";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return HaskellIcons.FILE;
    }

}