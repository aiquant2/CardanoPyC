//package org.intellij.sdk.language.wallet;
//
//import org.junit.Test;
//
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//
//public class BlockfrostApiValidatorTest {
//
//    // ⚠️ Replace with your real Blockfrost API key for actual network tests
//    private static final String VALID_API_KEY = "preprod8Z6zi3DPfkbN32xpZPmzBUGaMLobSEU0";
//    private static final String INVALID_API_KEY = "fchlmndsvdbnm";
//
//    @Test
//    public void testValidApiKeyOnPreprod() {
//        boolean result = BlockfrostApiValidator.validate(VALID_API_KEY, "preprod");
//        assertTrue("Expected validation to succeed with a valid API key on preprod", result);
//    }
//
//    @Test
//    public void testInvalidApiKeyOnPreprod() {
//        boolean result = BlockfrostApiValidator.validate(INVALID_API_KEY, "preprod");
//        assertFalse("Expected validation to fail with an invalid API key on preprod", result);
//    }
//
//    @Test
//    public void testUnsupportedNetwork() {
//        boolean result = BlockfrostApiValidator.validate(VALID_API_KEY, "testnet123");
//        assertFalse("Expected validation to fail for unsupported network", result);
//    }
//
//    @Test
//    public void testNullApiKey() {
//        boolean result = BlockfrostApiValidator.validate(null, "preprod");
//        assertFalse("Expected validation to fail when API key is null", result);
//    }
//
//    @Test
//    public void testEmptyApiKey() {
//        boolean result = BlockfrostApiValidator.validate("", "preprod");
//        assertFalse("Expected validation to fail when API key is empty", result);
//    }
//}
