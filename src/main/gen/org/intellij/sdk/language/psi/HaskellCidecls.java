// This is a generated file. Not intended for manual editing.
package org.intellij.sdk.language.psi;

import java.util.List;
import org.jetbrains.annotations.Nullable;

import org.jetbrains.annotations.NotNull;


import com.intellij.psi.PsiElement;

public interface HaskellCidecls extends PsiElement {

  @NotNull
  List<HaskellCidecl> getCideclList();

  @NotNull
  List<HaskellPragma> getPragmaList();

}
