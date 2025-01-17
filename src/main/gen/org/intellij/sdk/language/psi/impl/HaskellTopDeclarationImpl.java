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

public class HaskellTopDeclarationImpl extends ASTWrapperPsiElement implements HaskellTopDeclaration {

  public HaskellTopDeclarationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull HaskellVisitor visitor) {
    visitor.visitTopDeclaration(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof HaskellVisitor) accept((HaskellVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public HaskellClassDeclaration getClassDeclaration() {
    return findChildByClass(HaskellClassDeclaration.class);
  }

  @Override
  @Nullable
  public HaskellDataDeclaration getDataDeclaration() {
    return findChildByClass(HaskellDataDeclaration.class);
  }

  @Override
  @Nullable
  public HaskellDefaultDeclaration getDefaultDeclaration() {
    return findChildByClass(HaskellDefaultDeclaration.class);
  }

  @Override
  @Nullable
  public HaskellDerivingDeclaration getDerivingDeclaration() {
    return findChildByClass(HaskellDerivingDeclaration.class);
  }

  @Override
  @Nullable
  public HaskellExpression getExpression() {
    return findChildByClass(HaskellExpression.class);
  }

  @Override
  @Nullable
  public HaskellFixityDeclaration getFixityDeclaration() {
    return findChildByClass(HaskellFixityDeclaration.class);
  }

  @Override
  @Nullable
  public HaskellForeignDeclaration getForeignDeclaration() {
    return findChildByClass(HaskellForeignDeclaration.class);
  }

  @Override
  @Nullable
  public HaskellInstanceDeclaration getInstanceDeclaration() {
    return findChildByClass(HaskellInstanceDeclaration.class);
  }

  @Override
  @Nullable
  public HaskellNewtypeDeclaration getNewtypeDeclaration() {
    return findChildByClass(HaskellNewtypeDeclaration.class);
  }

  @Override
  @Nullable
  public HaskellPragma getPragma() {
    return findChildByClass(HaskellPragma.class);
  }

  @Override
  @Nullable
  public HaskellTypeDeclaration getTypeDeclaration() {
    return findChildByClass(HaskellTypeDeclaration.class);
  }

  @Override
  @Nullable
  public HaskellTypeFamilyDeclaration getTypeFamilyDeclaration() {
    return findChildByClass(HaskellTypeFamilyDeclaration.class);
  }

  @Override
  @Nullable
  public HaskellTypeInstanceDeclaration getTypeInstanceDeclaration() {
    return findChildByClass(HaskellTypeInstanceDeclaration.class);
  }

  @Override
  @Nullable
  public HaskellTypeSignature getTypeSignature() {
    return findChildByClass(HaskellTypeSignature.class);
  }

}
