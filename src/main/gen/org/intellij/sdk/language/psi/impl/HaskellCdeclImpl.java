// This is a generated file. Not intended for manual editing.
package org.intellij.sdk.language.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.sdk.language.psi.HaskellTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import org.intellij.sdk.language.psi.*;

public class HaskellCdeclImpl extends ASTWrapperPsiElement implements HaskellCdecl {

  public HaskellCdeclImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull HaskellVisitor visitor) {
    visitor.visitCdecl(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof HaskellVisitor) accept((HaskellVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public HaskellCdeclDataDeclaration getCdeclDataDeclaration() {
    return findChildByClass(HaskellCdeclDataDeclaration.class);
  }

  @Override
  @Nullable
  public HaskellCidecl getCidecl() {
    return findChildByClass(HaskellCidecl.class);
  }

  @Override
  @Nullable
  public HaskellTypeSignature getTypeSignature() {
    return findChildByClass(HaskellTypeSignature.class);
  }

}
