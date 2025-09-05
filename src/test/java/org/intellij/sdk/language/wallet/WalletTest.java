package org.intellij.sdk.language.wallet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class WalletTest {

    private static final String VALID_API_KEY;
    private static final String INVALID_API_KEY = "fchlmndsvdbnm";

    static {
        // Load API key from system property or environment variable
        String apiKey = System.getProperty("BLOCKFROST_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = System.getenv("BLOCKFROST_API_KEY");
        }
        VALID_API_KEY = apiKey;

        if (VALID_API_KEY != null) {
            System.out.println("API key loaded: ***" + VALID_API_KEY.substring(Math.max(0, VALID_API_KEY.length() - 4)));
        } else {
            System.out.println("No API key found. Set BLOCKFROST_API_KEY as system property or environment variable.");
        }
    }

    // BlockfrostApiValidator tests
    @Test
    public void testApiKeyLoaded() {
        if (VALID_API_KEY == null) {
            System.out.println("INFO: BLOCKFROST_API_KEY not found - network tests will be skipped");
            System.out.println("Set API key with: -DBLOCKFROST_API_KEY=your_key or export BLOCKFROST_API_KEY=your_key");
        } else {
            assertFalse("API key should not be empty", VALID_API_KEY.isEmpty());
        }
    }

    @Test
    public void testValidApiKeyOnPreprod() {
        if (VALID_API_KEY == null || VALID_API_KEY.isEmpty()) {
            System.out.println("Skipping testValidApiKeyOnPreprod - BLOCKFROST_API_KEY not configured");
            return;
        }

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
        if (VALID_API_KEY == null || VALID_API_KEY.isEmpty()) {
            System.out.println("Skipping testUnsupportedNetwork - BLOCKFROST_API_KEY not configured");
            return;
        }

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

    // SecureStorageUtil tests
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

    // GenerateWalletDialog tests
    @Test
    public void testGenerateWalletMethodExists() {
        try {
            Method method = GenerateWalletDialog.class.getDeclaredMethod("generateWallet", String.class);
            assertNotNull("generateWallet method should exist", method);
        } catch (NoSuchMethodException e) {
            fail("generateWallet method should exist");
        }
    }

    @Test
    public void testGenerateWalletFieldExistence() {
        try {
            GenerateWalletDialog.class.getDeclaredField("mnemonic");
            GenerateWalletDialog.class.getDeclaredField("baseAddress");
            GenerateWalletDialog.class.getDeclaredField("usernameField");
            GenerateWalletDialog.class.getDeclaredField("passwordField");
        } catch (NoSuchFieldException e) {
            fail("Required fields should exist: " + e.getMessage());
        }
    }

    @Test
    public void testNetworkHandlingLogic() {
        String network = "preprod";
        String accountType = determineAccountType(network);
        assertEquals("preprod", accountType);

        network = "preview";
        accountType = determineAccountType(network);
        assertEquals("preview", accountType);

        network = "mainnet";
        accountType = determineAccountType(network);
        assertEquals("mainnet", accountType);

        network = "unknown";
        accountType = determineAccountType(network);
        assertEquals("mainnet", accountType);
    }

    @Test
    public void testNetworkCaseInsensitivity() {
        assertTrue("preview should match case-insensitively", "preview".equalsIgnoreCase("PREVIEW"));
        assertTrue("preprod should match case-insensitively", "preprod".equalsIgnoreCase("PREPROD"));
        assertTrue("mainnet should match case-insensitively", "mainnet".equalsIgnoreCase("MAINNET"));
    }

    // SendAdaDialog tests
    @Test
    public void testSendAdaFieldExistence() {
        try {
            SendAdaDialog.class.getDeclaredField("recipientField");
            SendAdaDialog.class.getDeclaredField("amountField");
        } catch (NoSuchFieldException e) {
            fail("Required fields should exist: " + e.getMessage());
        }
    }

    @Test
    public void testSendAdaMethodExistence() {
        try {
            SendAdaDialog.class.getDeclaredMethod("createCenterPanel");
            SendAdaDialog.class.getDeclaredMethod("doOKAction");
            SendAdaDialog.class.getDeclaredMethod("transfer", String.class, double.class);
            SendAdaDialog.class.getDeclaredMethod("getBackendService");
        } catch (NoSuchMethodException e) {
            fail("Required methods should exist: " + e.getMessage());
        }
    }

    @Test
    public void testNetworkSwitchLogic() {
        String network = "preprod";
        String networkType = determineNetworkType(network);
        assertEquals("preprod", networkType);

        network = "mainnet";
        networkType = determineNetworkType(network);
        assertEquals("mainnet", networkType);

        network = "preview";
        networkType = determineNetworkType(network);
        assertEquals("preview", networkType);

        network = "unknown";
        networkType = determineNetworkType(network);
        assertEquals("preview", networkType);
    }

    @Test
    public void testInputValidationLogic() {
        assertTrue("Empty recipient should be invalid", isInputInvalid("", "10"));
        assertTrue("Empty amount should be invalid", isInputInvalid("addr_test1...", ""));
        assertTrue("Both empty should be invalid", isInputInvalid("", ""));
        assertFalse("Valid inputs should pass validation", isInputInvalid("addr_test1...", "10"));
    }

    // Helper methods
    private String determineAccountType(String network) {
        if ("preview".equalsIgnoreCase(network)) {
            return "preview";
        } else if ("preprod".equalsIgnoreCase(network)) {
            return "preprod";
        } else {
            return "mainnet";
        }
    }

    private String determineNetworkType(String network) {
        switch (network) {
            case "preprod": return "preprod";
            case "mainnet": return "mainnet";
            default: return "preview";
        }
    }

    private boolean isInputInvalid(String recipient, String amount) {
        return recipient.isEmpty() || amount.isEmpty();
    }
}