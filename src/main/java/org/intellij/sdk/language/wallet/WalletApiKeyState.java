package org.intellij.sdk.language.wallet;

public class WalletApiKeyState {
    private static WalletApiKeyState instance;
    private String apiKey;
    private String network;

    // Private constructor (Singleton Pattern)
    private WalletApiKeyState() {}

    // Get Singleton Instance
    public static synchronized WalletApiKeyState getInstance() {
        if (instance == null) {
            instance = new WalletApiKeyState();
        }
        return instance;
    }

    // Store API Key
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    // Retrieve API Key
    public String getApiKey() {
        return apiKey;
    }

    // Store Network Type
    public void setNetwork(String network) {
        this.network = network;
    }

    // Retrieve Network Type
    public String getNetwork() {
        return network;
    }
}