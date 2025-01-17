import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.codeInsight.completion.CompletionType;
import java.util.List;

public class HaskellCompletionContributorTest extends BasePlatformTestCase {



    public void testModuleImportCompletions() {
        myFixture.configureByText("Test.hs", "import ");
        myFixture.complete(CompletionType.BASIC);
        List<String> completions = myFixture.getLookupElementStrings();

        assertNotNull("Completions should not be null", completions);
        assertTrue("Completions should contain 'Data.List'", completions.contains("Data.List"));
        assertTrue("Completions should contain 'Plutus.V2.Ledger.Contexts'", completions.contains("Plutus.V2.Ledger.Contexts"));
    }



    public void testTypeCompletions() {
        myFixture.configureByText("Test.hs", "Int");
        myFixture.complete(CompletionType.BASIC);
        List<String> completions = myFixture.getLookupElementStrings();

        assertNotNull("Completions should not be null", completions);
        assertTrue("Completions should contain 'Int'", completions.contains("Int"));
        assertTrue("Completions should contain 'Maybe'", completions.contains("Maybe"));
    }

    public void testTypeClassCompletions() {
        myFixture.configureByText("Test.hs", "Functor");
        myFixture.complete(CompletionType.BASIC);
        List<String> completions = myFixture.getLookupElementStrings();

        assertNotNull("Completions should not be null", completions);
        assertTrue("Completions should contain 'Functor'", completions.contains("Functor"));
        assertTrue("Completions should contain 'Monad'", completions.contains("Monad"));
    }
}
