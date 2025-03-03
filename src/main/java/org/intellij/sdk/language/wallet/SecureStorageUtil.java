package org.intellij.sdk.language.wallet;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;

public class SecureStorageUtil {


    private static CredentialAttributes createCredentialAttributes(String key) {
        return new CredentialAttributes(
                CredentialAttributesKt.generateServiceName("CardanoPy", key)
        );
    }

    public static void storeCredential(String key, String value) {
        CredentialAttributes attributes = createCredentialAttributes(key);
        Credentials credentials = new Credentials(key, value);
        PasswordSafe.getInstance().set(attributes, credentials);
    }

    public static String retrieveCredential(String key) {
        CredentialAttributes attributes = createCredentialAttributes(key);
        Credentials credentials = PasswordSafe.getInstance().get(attributes);
        return credentials != null ? credentials.getPasswordAsString() : null;
    }


    public static void removeCredential(String key) {
        CredentialAttributes attributes = createCredentialAttributes(key);
        PasswordSafe.getInstance().set(attributes, null);
    }
}
