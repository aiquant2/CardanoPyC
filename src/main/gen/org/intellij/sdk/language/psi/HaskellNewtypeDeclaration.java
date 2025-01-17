// This is a generated file. Not intended for manual editing.
package org.intellij.sdk.language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface HaskellNewtypeDeclaration extends PsiElement {

  @Nullable
  HaskellCcontext getCcontext();

  @NotNull
  List<HaskellDerivingVia> getDerivingViaList();

  @NotNull
  HaskellNewconstr getNewconstr();

  @NotNull
  List<HaskellPragma> getPragmaList();

  @NotNull
  List<HaskellQName> getQNameList();

  @NotNull
  HaskellSimpletype getSimpletype();

  @Nullable
  HaskellTextLiteral getTextLiteral();

  @NotNull
  List<HaskellTtype> getTtypeList();

  @NotNull
  List<HaskellTypeSignature> getTypeSignatureList();

}
