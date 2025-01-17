// This is a generated file. Not intended for manual editing.
package org.intellij.sdk.language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface HaskellTypeSignature extends PsiElement {

  @NotNull
  List<HaskellCcontext> getCcontextList();

  @NotNull
  List<HaskellPragma> getPragmaList();

  @NotNull
  List<HaskellQNames> getQNamesList();

  @NotNull
  HaskellTtype getTtype();

}
