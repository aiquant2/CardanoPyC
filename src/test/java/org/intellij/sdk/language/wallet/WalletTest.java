package org.intellij.sdk.language.wallet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class WalletTest {

    // BlockfrostApiValidatorTest
    // ⚠️ Replace with your real Blockfrost API key for actual network tests
    private static final String VALID_API_KEY = "preprod8Z6zi3DPfkbN32xpZPmzBUGaMLobSEU0";
    private static final String INVALID_API_KEY = "fchlmndsvdbnm";

    @Test
    public void testValidApiKeyOnPreprod() {
        boolean result = BlockfrostApiValidator.validate(VALID_API_KEY, "preprod");
        assertTrue("Expected validation to succeed with a valid API key on preprod", result);
    }

    @Test
    public void testInvalidApiKeyOnPreprod() {
        boolean result = BlockfrostApiValidator.validate(INVALID_API_KEY, "preprod");
        assertFalse("Expected validation to fail with an invalid API key on preprod", result);
    }

    @Test
    public void testUnsupportedNetwork() {
        boolean result = BlockfrostApiValidator.validate(VALID_API_KEY, "testnet123");
        assertFalse("Expected validation to fail for unsupported network", result);
    }

    @Test
    public void testNullApiKey() {
        boolean result = BlockfrostApiValidator.validate(null, "preprod");
        assertFalse("Expected validation to fail when API key is null", result);
    }

    @Test
    public void testEmptyApiKey() {
        boolean result = BlockfrostApiValidator.validate("", "preprod");
        assertFalse("Expected validation to fail when API key is empty", result);
    }

    // SecureStorageUtilTest
    @Before
    public void setUp() {
        // Clean any previous test data
        SecureStorageUtil.removeCredential("wallet_username");
        SecureStorageUtil.removeCredential("wallet_password");
    }

    @After
    public void tearDown() {
        // Cleanup to avoid leaking into other tests
        SecureStorageUtil.removeCredential("wallet_username");
        SecureStorageUtil.removeCredential("wallet_password");
    }

    @Test
    public void testStoreAndRetrieveCredential() {
        SecureStorageUtil.storeCredential("wallet_username", "alice");

        String retrieved = SecureStorageUtil.retrieveCredential("wallet_username");
        assertEquals("alice", retrieved);
    }

    @Test
    public void testOverwriteCredential() {
        SecureStorageUtil.storeCredential("wallet_username", "alice");
        SecureStorageUtil.storeCredential("wallet_username", "bob");

        String retrieved = SecureStorageUtil.retrieveCredential("wallet_username");
        assertEquals("bob", retrieved);
    }

    @Test
    public void testRetrieveMissingCredential() {
        String result = SecureStorageUtil.retrieveCredential("non_existent_key");
        assertNull(result);
    }

    @Test
    public void testRemoveCredential() {
        SecureStorageUtil.storeCredential("wallet_password", "secret123");
        assertEquals("secret123", SecureStorageUtil.retrieveCredential("wallet_password"));

        SecureStorageUtil.removeCredential("wallet_password");
        assertNull(SecureStorageUtil.retrieveCredential("wallet_password"));
    }
}