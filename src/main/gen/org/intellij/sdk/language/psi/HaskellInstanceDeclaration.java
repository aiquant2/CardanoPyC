// This is a generated file. Not intended for manual editing.
package org.intellij.sdk.language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface HaskellInstanceDeclaration extends PsiElement {

  @Nullable
  HaskellCidecls getCidecls();

  @Nullable
  HaskellInst getInst();

  @NotNull
  List<HaskellPragma> getPragmaList();

  @Nullable
  HaskellQName getQName();

  @Nullable
  HaskellScontext getScontext();

  @Nullable
  HaskellTypeEquality getTypeEquality();

  @NotNull
  List<HaskellVarid> getVaridList();

}
