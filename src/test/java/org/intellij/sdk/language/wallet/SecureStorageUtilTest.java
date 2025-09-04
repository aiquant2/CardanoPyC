package org.intellij.sdk.language.wallet;


import org.junit.*;

import static org.junit.Assert.*;

public class SecureStorageUtilTest {

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
