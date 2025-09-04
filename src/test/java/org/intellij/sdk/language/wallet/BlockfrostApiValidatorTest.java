package org.intellij.sdk.language.wallet;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BlockfrostApiValidatorTest {

    // ⚠️ Replace with your real Blockfrost API key for actual network tests
    private static final String VALID_API_KEY = "preprod8Z6zi3DPfkbN32xpZPmzBUGaMLobSEU0";
    private static final String INVALID_API_KEY = "fchlmndsvdbnm";

    @Test
    void testValidApiKeyOnPreprod() {
        boolean result = BlockfrostApiValidator.validate(VALID_API_KEY, "preprod");
        assertTrue(result, "Expected validation to succeed with a valid API key on preprod");
    }

    @Test
    void testInvalidApiKeyOnPreprod() {
        boolean result = BlockfrostApiValidator.validate(INVALID_API_KEY, "preprod");
        assertFalse(result, "Expected validation to fail with an invalid API key on preprod");
    }

    @Test
    void testUnsupportedNetwork() {
        boolean result = BlockfrostApiValidator.validate(VALID_API_KEY, "testnet123");
        assertFalse(result, "Expected validation to fail for unsupported network");
    }

    @Test
    void testNullApiKey() {
        boolean result = BlockfrostApiValidator.validate(null, "preprod");
        assertFalse(result, "Expected validation to fail when API key is null");
    }

    @Test
    void testEmptyApiKey() {
        boolean result = BlockfrostApiValidator.validate("", "preprod");
        assertFalse(result, "Expected validation to fail when API key is empty");
    }
}
