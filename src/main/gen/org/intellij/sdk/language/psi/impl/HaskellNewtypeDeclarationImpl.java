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

public class HaskellNewtypeDeclarationImpl extends ASTWrapperPsiElement implements HaskellNewtypeDeclaration {

  public HaskellNewtypeDeclarationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull HaskellVisitor visitor) {
    visitor.visitNewtypeDeclaration(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof HaskellVisitor) accept((HaskellVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public HaskellCcontext getCcontext() {
    return findChildByClass(HaskellCcontext.class);
  }

  @Override
  @NotNull
  public List<HaskellDerivingVia> getDerivingViaList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, HaskellDerivingVia.class);
  }

  @Override
  @NotNull
  public HaskellNewconstr getNewconstr() {
    return findNotNullChildByClass(HaskellNewconstr.class);
  }

  @Override
  @NotNull
  public List<HaskellPragma> getPragmaList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, HaskellPragma.class);
  }

  @Override
  @NotNull
  public List<HaskellQName> getQNameList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, HaskellQName.class);
  }

  @Override
  @NotNull
  public HaskellSimpletype getSimpletype() {
    return findNotNullChildByClass(HaskellSimpletype.class);
  }

  @Override
  @Nullable
  public HaskellTextLiteral getTextLiteral() {
    return findChildByClass(HaskellTextLiteral.class);
  }

  @Override
  @NotNull
  public List<HaskellTtype> getTtypeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, HaskellTtype.class);
  }

  @Override
  @NotNull
  public List<HaskellTypeSignature> getTypeSignatureList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, HaskellTypeSignature.class);
  }

}
