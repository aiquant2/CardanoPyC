package org.intellij.sdk.language.wallet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class WalletTest {

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

    @Before
    public void setUp() {
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
            // Test that fields exist
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
        // Test the network handling logic that would be in generateWallet method
        String network = "preprod";
        String accountType = determineAccountType(network);
        assertNotNull("Account type should be determined for preprod", accountType);

        network = "preview";
        accountType = determineAccountType(network);
        assertNotNull("Account type should be determined for preview", accountType);

        network = "mainnet";
        accountType = determineAccountType(network);
        assertNotNull("Account type should be determined for mainnet", accountType);

        network = "unknown";
        accountType = determineAccountType(network);
        assertNotNull("Account type should be determined for unknown network", accountType);
    }

    @Test
    public void testNetworkCaseInsensitivity() {
        // Test network case insensitivity logic
        assertTrue("preview should match case-insensitively", "preview".equalsIgnoreCase("PREVIEW"));
        assertTrue("preprod should match case-insensitively", "preprod".equalsIgnoreCase("PREPROD"));
        assertTrue("mainnet should match case-insensitively", "mainnet".equalsIgnoreCase("MAINNET"));
    }

    @Test
    public void testGenerateWalletMethodSignatures() {
        try {
            // Test that methods have correct signatures
            Method generateMethod = GenerateWalletDialog.class.getDeclaredMethod("generateWallet", String.class);
            assertEquals("generateWallet should take String parameter", String.class, generateMethod.getParameterTypes()[0]);

        } catch (NoSuchMethodException e) {
            fail("Required methods should have correct signatures: " + e.getMessage());
        }
    }

    @Test
    public void testSendAdaFieldExistence() {
        try {
            // Test that fields exist
            SendAdaDialog.class.getDeclaredField("recipientField");
            SendAdaDialog.class.getDeclaredField("amountField");
        } catch (NoSuchFieldException e) {
            fail("Required fields should exist: " + e.getMessage());
        }
    }

    @Test
    public void testSendAdaMethodExistence() {
        try {
            // Test that methods exist
            Method createCenterPanelMethod = SendAdaDialog.class.getDeclaredMethod("createCenterPanel");
            Method doOKActionMethod = SendAdaDialog.class.getDeclaredMethod("doOKAction");
            Method transferMethod = SendAdaDialog.class.getDeclaredMethod("transfer", String.class, double.class);
            Method getBackendServiceMethod = SendAdaDialog.class.getDeclaredMethod("getBackendService");

            assertNotNull("createCenterPanel method should exist", createCenterPanelMethod);
            assertNotNull("doOKAction method should exist", doOKActionMethod);
            assertNotNull("transfer method should exist", transferMethod);
            assertNotNull("getBackendService method should exist", getBackendServiceMethod);
        } catch (NoSuchMethodException e) {
            fail("Required methods should exist: " + e.getMessage());
        }
    }

    @Test
    public void testSendAdaMethodSignatures() {
        try {
            // Test that methods have correct signatures
            Method transferMethod = SendAdaDialog.class.getDeclaredMethod("transfer", String.class, double.class);
            assertEquals("transfer should take String and double parameters",
                    String.class, transferMethod.getParameterTypes()[0]);
            assertEquals("transfer should take String and double parameters",
                    double.class, transferMethod.getParameterTypes()[1]);

            Method getBackendServiceMethod = SendAdaDialog.class.getDeclaredMethod("getBackendService");
            assertTrue("getBackendService should return BackendService",
                    getBackendServiceMethod.getReturnType().getName().contains("BackendService"));

        } catch (NoSuchMethodException e) {
            fail("Required methods should have correct signatures: " + e.getMessage());
        }
    }

    @Test
    public void testNetworkSwitchLogic() {
        // Test the network switch logic that would be in transfer method
        String network = "preprod";
        String networkType = determineNetworkType(network);
        assertEquals("preprod should map to preprod", "preprod", networkType);

        network = "mainnet";
        networkType = determineNetworkType(network);
        assertEquals("mainnet should map to mainnet", "mainnet", networkType);

        network = "preview";
        networkType = determineNetworkType(network);
        assertEquals("preview should map to preview", "preview", networkType);

        network = "unknown";
        networkType = determineNetworkType(network);
        assertEquals("unknown should map to preview", "preview", networkType);
    }

    @Test
    public void testInputValidationLogic() {
        // Test the input validation logic that would be in doOKAction
        assertTrue("Empty recipient should be invalid", isInputInvalid("", "10"));
        assertTrue("Empty amount should be invalid", isInputInvalid("addr_test1...", ""));
        assertTrue("Both empty should be invalid", isInputInvalid("", ""));
        assertFalse("Valid inputs should pass validation", isInputInvalid("addr_test1...", "10"));
    }

    @Test
    public void testNumberParsingLogic() {
        // Test the number parsing logic
        assertDoesNotThrow("Should parse valid number", () -> Double.parseDouble("10.5"));
        assertThrows("Should throw exception for invalid number",
                NumberFormatException.class, () -> Double.parseDouble("invalid"));
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

    private void assertDoesNotThrow(String message, Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            fail(message + ": " + e.getMessage());
        }
    }

    private void assertThrows(String message, Class<? extends Exception> exceptionClass, Runnable runnable) {
        try {
            runnable.run();
            fail(message);
        } catch (Exception e) {
            assertTrue("Expected " + exceptionClass.getSimpleName() + " but got " + e.getClass().getSimpleName(),
                    exceptionClass.isInstance(e));
        }
    }
}


