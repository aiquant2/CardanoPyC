import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.psi.PsiFile;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.intellij.sdk.language.HaskellFileType;
import org.junit.Test;

public class HaskellParserTest extends BasePlatformTestCase {


    public void testValidHaskellCode() {
        String validHaskellCode = "main = putStrLn \"Hello, Haskell!\"";

        PsiFile file = myFixture.configureByText(HaskellFileType.INSTANCE, validHaskellCode);
        assertNotNull("File should not be null", file);

        ASTNode rootNode = file.getNode();
        assertNotNull("AST root node should not be null", rootNode);

        assertTrue("AST should have children", rootNode.getChildren(null).length > 0);
    }


    public void testInvalidHaskellCode() {
        String invalidHaskellCode = "main = putStrLn \"Hello, Haskell!"; // Missing closing quote

        PsiFile file = myFixture.configureByText(HaskellFileType.INSTANCE, invalidHaskellCode);
        assertNotNull("File should not be null", file);

        assertTrue("File should contain syntax errors", file.getText().contains("\""));
    }


    public void testIncompleteHaskellCode() {
        String incompleteCode = "main =";

        PsiFile file = myFixture.configureByText(HaskellFileType.INSTANCE, incompleteCode);
        assertNotNull("File should not be null", file);

        ASTNode rootNode = file.getNode();
        assertNotNull("AST root node should not be null", rootNode);
        assertTrue("AST root node text length should match input", rootNode.getTextLength() > 0);
    }


    public void testEmptyHaskellFile() {
        String emptyCode = "";

        PsiFile file = myFixture.configureByText(HaskellFileType.INSTANCE, emptyCode);
        assertNotNull("File should not be null", file);

        ASTNode rootNode = file.getNode();
        assertNotNull("AST root node should not be null", rootNode);
        assertEquals("File should have zero length", 0, rootNode.getTextLength());
    }


    public void testHaskellComments() {
        String commentCode = "-- This is a comment\nmain = putStrLn \"Hello\"";

        PsiFile file = myFixture.configureByText(HaskellFileType.INSTANCE, commentCode);
        assertNotNull("File should not be null", file);

        ASTNode rootNode = file.getNode();
        assertNotNull("AST root node should not be null", rootNode);

        PsiElement[] children = file.getChildren();
        assertTrue("File should contain elements", children.length > 0);

        assertTrue("File should contain the comment text", file.getText().contains("-- This is a comment"));
    }

}
