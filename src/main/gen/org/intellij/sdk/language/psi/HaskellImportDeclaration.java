// This is a generated file. Not intended for manual editing.
package org.intellij.sdk.language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface HaskellImportDeclaration extends PsiElement {

  @Nullable
  HaskellImportPackageName getImportPackageName();

  @Nullable
  HaskellImportQualified getImportQualified();

  @Nullable
  HaskellImportQualifiedAs getImportQualifiedAs();

  @Nullable
  HaskellImportSpec getImportSpec();

  @Nullable
  HaskellModid getModid();

  @NotNull
  List<HaskellPragma> getPragmaList();

  //WARNING: getModuleName(...) is skipped
  //matching getModuleName(HaskellImportDeclaration, ...)
  //methods are not found in null

}
