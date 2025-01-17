package org.intellij.sdk.language;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.apache.commons.lang3.tuple.Pair;
import org.intellij.sdk.language.psi.HaskellTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class HaskellCompletionContributor extends CompletionContributor {
    public HaskellCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(HaskellTypes.HS_VAR_ID).withLanguage(HaskellLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet resultSet) {
                        addFunctionCompletions(resultSet);
                        //addCustomIdentifierCompletions(resultSet);
                    }
                }
        );

        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(HaskellTypes.HS_CON_ID).withLanguage(HaskellLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet resultSet) {
                        addModuleImportCompletions(resultSet);
                    }
                }
        );
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(HaskellTypes.HS_CON_ID).withLanguage(HaskellLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet resultSet) {
                        addPlutusIdentifierCompletions(resultSet);
                    }
                }
        );
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(HaskellTypes.HS_CON_ID).withLanguage(HaskellLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet resultSet) {
                        addTypeClassCompletions(resultSet);
                    }
                }
        );


        // Completion for pragmas
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(HaskellTypes.HS_ONE_PRAGMA).withLanguage(HaskellLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet resultSet) {
                        addPragmaCompletions(resultSet);
                    }
                }
        );

        // Completion for keywords
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(HaskellTypes.HS_VAR_ID).withLanguage(HaskellLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet resultSet) {
                        addKeywordCompletions(resultSet);
                    }
                }
        );

        // Completion for types
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(HaskellTypes.HS_CON_ID).withLanguage(HaskellLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet resultSet) {
                        addTypeCompletions(resultSet);
                    }
                }
        );
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(HaskellTypes.HS_VARSYM_ID).withLanguage(HaskellLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet resultSet) {
                        addOperatorCompletions(resultSet);
                    }
                }
        );
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(HaskellLanguage.INSTANCE)
                        .and(PlatformPatterns.psiElement().afterLeaf("{-#")),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet resultSet) {
                        addClosingPragmaCompletion(resultSet);
                    }
                }
        );
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(HaskellTypes.HS_VAR_ID).withLanguage(HaskellLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet resultSet) {
                        addFunctionCompletions(resultSet);
                        addSnippetCompletions(resultSet);
                    }
                }
        );
    }

    private void addSnippetCompletions(@NotNull CompletionResultSet resultSet) {
        List<Pair<String, String>> snippets = Arrays.asList(
                Pair.of("main", " :: IO ()\nmain = \t"),
                Pair.of("case", " expression of\n    pattern1 -> result1\n    _ -> defaultResult"),
                Pair.of("data", " TypeName = Constructor1 | Constructor2"),
                Pair.of("newtype", " TypeName = Constructor Type"),
                Pair.of("type", " TypeName = ExistingType"),
                Pair.of("class", " ClassName a where\n    method :: a -> Type"),
                Pair.of("instance", " ClassName TypeName where\n    method = implementation"),
                Pair.of("if", " condition\n    then expression1\n    else expression2"),
                Pair.of("let", " variable = expression\nin expression"),
                Pair.of("where", " expression\n    where\n        variable = expression"),
                Pair.of("module", " ModuleName where\n    import Module"),
                Pair.of("import", " Module"),
                Pair.of("forall", "  a. a -> a"),
                Pair.of("guard", " condition = if condition then return () else mzero")

        );
        for (Pair<String, String> snippet : snippets) {
            resultSet.addElement(LookupElementBuilder.create(snippet.getLeft())
                    .withTypeText("Snippet")
                    .withInsertHandler((context, item) -> {
                        TemplateManager templateManager = TemplateManager.getInstance(context.getProject());
                        Template template = templateManager.createTemplate("", "", snippet.getRight());
                        templateManager.startTemplate(context.getEditor(), template);
                    }));
        }

    }


    private void addClosingPragmaCompletion(@NotNull CompletionResultSet resultSet) {
        resultSet.addElement(LookupElementBuilder.create("#-}")
                .withPresentableText("#-}")
                .withTypeText("Close pragma"));
    }

    private void addKeywordCompletions(CompletionResultSet resultSet) {
        List<String> keywords = Arrays.asList(
                "data", "type", "where", "module", "let", "in",
                "case", "of", "import", "instance", "newtype", "deriving",
                "if", "then", "else", "do", "as", "qualified", "hiding",
                "forall", "infix", "infixl", "infixr",
                "class", "instance", "default", "foreign", "inline", "noinline",
                "typeClass", "typefamily", "let", "in", "if", "then", "else",
                "submitTx","all","abs","accum","alreadyInUseErrorType","alreadyExistsErrorType","acos",
                "accumArray","annotateIOError","array","asTypeOf","partition","permissionErrorType","permutations","phase",
                "pi","polar","posixTimeFromIso8601","print","printDataToJSON","product","program","properFraction",
                "pure","putChar","putStr","bit","bitSize","bounds","break","calculate","catch","catMaybes","ceiling","clearBit",
                "compare","complement","concat","concatMap","const","curry","cycle","writePolicyToFile","writeCodeToFile",
                "toBuiltinData","writeValidatorToFile","unstableMakeIsData","traceIfFalse","unless","untypedValidator",
                "displayError","from","deadline","getTxId","mkValidator", "txSignedBy","decodeFloat","delete","deleteBy",
                "deleteFirstBy","digitToInt","div","divMod","doesNotExistErrorType","drop","dropWhile","elemIndex",
                "encodeFloat","enumFrom","enumFromThen","enumFromTo","eofErrorType","error","exitFailure","exitSuccess","exitWith",
                "exp","exponent","fail","filterM","find","findIndex","fixIO","flip","floatDigits","floatRadix","floatRange",
                "floor","foldM","foreign","forM","fromEnum","fromIntegral","fromJust","fromMaybe","fromRat","fromRational",
                "fullErrorType","genericIndex","genericLength","genericReplicate","getArgs","getContents",
                "getEnv","getProgName","guard","groupBy","inRange","insertBy","interact","intersect","intersectBy","intersperse",
                "intToDigit","ioeGetErrorString","ioeGetFileName","ioError","isAlpha","isAlreadyExistsError",
                "isAlreadyInUseError","isAscii","isControl","isDigit","isFullError","isHexDigit","isLetter","isLower","isMark",
                "isNegativeZero","isNothing","isNumber","isOctDigit","isSpace","isUpper","isSymbol","join","last","lcm","length",
                "lex","lexDigits","letLitChar","lines","list","listArray","log","lookup","magnitude","max","maxBound","maximum",
                "mkIOError","mkPolar","mod","minimum","min","max","not","numerator","odd","openFile","otherwise","partition",
                "pi","pkh","plutus","printVestingDatumJSON","product","qualified","range","raedHex","readInt","readIO",
                "readList","readOct","repeat","readSigned","return","rotate","tail","take","time","toInteger","toEnum","then",
                "toInteger","try","txInfoValidRange","txSignedBy","union","unionBy","unless","unstableMakeIsData",
                "unzip","unzip3","unzip4","useError","useErrorType","validator","vesting","void","vestingAddressBech32","validatorAddressBech32",
                "wrapValidator","writeFile","writeValidatorToFile","xor","zip3","zip4"
        );
        for (String keyword : keywords) {
            resultSet.addElement(LookupElementBuilder.create(keyword));
        }
    }

    private void addFunctionCompletions(CompletionResultSet resultSet) {
        List<Pair<String, String>> functions = Arrays.asList(
                Pair.of("map", "Applies a function to each element of a list."),
                Pair.of("filter", "Filters a list based on a predicate."),
                Pair.of("foldl", "Left fold."),
                Pair.of("foldr", "Right fold."),
                Pair.of("fold", " fold."),
                Pair.of("length", "Returns the length of a list."),
                Pair.of("sum", "Calculates the sum of a list of numbers."),
                Pair.of("txSignedBy", "Checks if a transaction is signed by a given public key."),
                Pair.of("concat", "Concaten ates a list of lists."),
                Pair.of("zip", "Combines two lists into a list of pairs."),
                Pair.of("take", "Takes the first n elements from a list."),
                Pair.of("drop", "Drops the first n elements from a list."),
                Pair.of("reverse", "Reverses a list."),
                Pair.of("elem", "Checks if an element is in a list."),
                Pair.of("not", "Logical negation."),
                Pair.of("&&", "Logical conjunction."),
                Pair.of("||", "Logical disjunction."),
                Pair.of("mapM", "Maps a monadic function over a list."),
                Pair.of("sequence", "Transforms a list of actions into an action that produces a list."),
                Pair.of("liftM", "Lifts a function to a monadic context."),
                Pair.of("join", "Flattens a monadic value."),
                Pair.of("putStrLn","Print"),
                Pair.of("getLine","Input")
        );
        for (Pair<String, String> function : functions) {
            resultSet.addElement(LookupElementBuilder.create(function.getLeft()).withTypeText(function.getRight()));
        }
    }

    private void addModuleImportCompletions(CompletionResultSet resultSet) {
        System.out.println("Adding module import completions");
        List<String> moduleImports = Arrays.asList(
                "Data.List", "Control.Monad", "Prelude",
                "Plutus.Contract", "Plutus.V2.Ledger.Contexts",
                "Data.Maybe", "Data.Either", "Control.Applicative",
                "Data.Functor", "Data.Tuple", "Control.Concurrent",
                "System.IO", "Data.Text", "Data.Map",
                "Data.Set", "Control.Monad.Trans", "Control.Monad.State","Ledger.Value",
                "Plutus.V2.Ledger.Api","Data.ByteString.Char8",
                "PlutusTx.Builtins.Internal"
        );
        for (String moduleImport : moduleImports) {
            resultSet.addElement(LookupElementBuilder.create(moduleImport));
        }
    }

    private void addPragmaCompletions(CompletionResultSet resultSet) {
        List<String> pragmas = Arrays.asList(
                "LANGUAGE ", "WARNING ", "DEPRECATED ",
                "INLINE ", "NOINLINE ", "INLINABLE ",
                "RULES ", "ANN ", "LINE ", "SPECIALIZE ",
                "UNPACK ", "SOURCE ", "SCC ",
                "LANGUAGE GADTs", "LANGUAGE TypeFamilies", "LANGUAGE MultiParamTypeClasses","#-}",
                "Prelude"
        );
        for (String pragma : pragmas) {
            resultSet.addElement(LookupElementBuilder.create(pragma));
        }
    }


    private void addOperatorCompletions(CompletionResultSet resultSet) {
        List<String> operators = Arrays.asList(
                "+", "-", "*", "/", "==", "<", ">", "<=", ">="
        );
        for (String operator : operators) {
            resultSet.addElement(LookupElementBuilder.create(operator));
        }
    }


    private void addPlutusIdentifierCompletions(CompletionResultSet resultSet) {
        List<String> plutusIdentifiers = Arrays.asList(
                "Ledger", "PlutusTx", "Contract","BuiltinByteString","Validator",
                "Redeemer","Datum","TxOutRef","ScriptContext","PubKeyHash","Address","TxInfo","FileError",
                "POSIXTime","Beneficiary","BuiltinByteString","TxOutRef","UTXO","TypeFamilies","ScopedTypeVariables","TypeApplications",
                "ParagraphSeparator","PrivateUse","AbsoluteSeek","AppendMode","Applicative","Array","Bits","BlockBuffering",
                "Bounded","BufferMode","BuiltinData","Builtins","Script","Reached","MintingPolicy","BuiltinByteString",
                "TemplateHaskell", "NoImplicitPrelude","DataKinds","Policy","Signed","TypeFamilies","TypeApplications","ScopedTypeVariables",
                "MultiParamTypeClasses","ImportQualifiedPost","DeriveAnyClass","DeriveGeneric","ScopedTypeVariables","TypeApplications",
                "DerivingStrategies","MultiParamTypeClasses","Utils","ByteString","Directory","FilePath","Complex",
                "ConnectorPunctuation","Complex","CHAIN","Contexts","Data","DataKinds","DecimalNumber","ExitCode","ExitFailure",
                "ExitSuccess","False","Functor","Format","GT","Handle","HELPER","Int8","Int16","Int32","Int64","Interval",
                "IOError","Ix","IOMode","Just","Left","LT","LetterNumber","LowercaseLetter","MathSymbol","Nothing","NoImplicitPrelude",
                "NoBuffering","NotAssigned","ON","Ratio","Real","Right","ScriptContext","TemplateHaskell","ReadWriteMode","Word"

        );
        for (String identifier : plutusIdentifiers) {
            resultSet.addElement(LookupElementBuilder.create(identifier));
        }
    }

    private void addTypeClassCompletions(CompletionResultSet resultSet) {
        List<String> typeclasses = Arrays.asList(
                "Eq", "Ord", "Show", "Functor", "Monad","Semigroup"
        );
        for (String typeclass : typeclasses) {
            resultSet.addElement(LookupElementBuilder.create(typeclass));
        }
    }


    private void addTypeCompletions(CompletionResultSet resultSet) {
        List<String> types = Arrays.asList(
                "Int", "Bool", "Char", "String", "Float", "Double",
                "Maybe", "List", "Tuple", "Either", "IO",
                "Function", "Map", "Set", "CustomType","DataKinds",
                "Applicative"," Utils","DeriveGeneric","OverloadedStrings"
        );
        for (String type : types) {
            resultSet.addElement(LookupElementBuilder.create(type));
        }
    }
}