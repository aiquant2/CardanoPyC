package org.intellij.sdk.language.highlighter;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.intellij.sdk.language.icons.HaskellIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

final class HaskellColorSettingsPage implements ColorSettingsPage {

    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Keyword", HaskellSyntaxHighlighter.KEYWORD),
            new AttributesDescriptor("Number", HaskellSyntaxHighlighter.NUMBER),
            new AttributesDescriptor("Pragma", HaskellSyntaxHighlighter.PRAGMA),
            new AttributesDescriptor("Operator", HaskellSyntaxHighlighter.OPERATOR)
    };

    @Override
    public Icon getIcon() {
        return HaskellIcons.FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new HaskellSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return """
            {- Example Code -}
                 -- This is a sample Haskell code snippet
                 {-# LANGUAGE GADTs #-}
                 data Tree a = Leaf | Node a (Tree a) (Tree a) deriving (Show, Read, Eq, Ord)
                 type Path a = ([a], [a])
            
                 moveRight :: Path a -> Path a
                 moveRight (x:xs, ys) = (xs, x:ys)
            
                 moveLeft :: Path a -> String
                 moveLeft (xs, y:ys) = (y:xs, ys)
            
                 main = do
                    let exampleTree = Node 1 (Node 2 Leaf Leaf) (Node 3 Leaf Leaf)
                    print(moveRight (exampleTree, []))
                    print(moveLeft ([], [3, 2, 1]))
                     putStrLn (":User  " ++ userName ++ ", Age: " ++ show userAge)
                 validateTransaction :: TransactionData -> () -> ScriptContext -> Bool
                 validateTransaction txData () ctx = traceIfFalse "missing user signature" userSigned &&
                                                       traceIfFalse "transaction expired" transactionValid
                   where
                     contextInfo :: TxInfo
                     contextInfo = scriptContextTxInfo ctx
            
                     userSigned :: Bool
                     userSigned = txSignedBy contextInfo $ receiver txData
            
                     transactionValid :: Bool
                     transactionValid = contains (from $ expiration txData) $ txInfoValidRange contextInfo
            """;
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @Override
    public AttributesDescriptor @NotNull [] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public ColorDescriptor @NotNull [] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Haskell";
    }

}