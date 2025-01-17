// This is a generated file. Not intended for manual editing.
package org.intellij.sdk.language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface HaskellDerivingDeclaration extends PsiElement {

  @NotNull
  HaskellInst getInst();

  @NotNull
  List<HaskellPragma> getPragmaList();

  @NotNull
  HaskellQName getQName();

  @Nullable
  HaskellScontext getScontext();

}
