package org.intellij.sdk.language.psi;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.intellij.sdk.language.HaskellFileType;
import org.intellij.sdk.language.HaskellLanguage;
import org.jetbrains.annotations.NotNull;

public class HaskellFile extends PsiFileBase {

    public HaskellFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, HaskellLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return HaskellFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Haskell File";
    }

}