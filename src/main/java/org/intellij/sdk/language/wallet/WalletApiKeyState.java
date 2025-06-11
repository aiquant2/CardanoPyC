
package org.intellij.sdk.language.wallet;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;

public class WalletApiKeyState {

    private static WalletApiKeyState instance;
    private String apiKey;
    private String network;
    private Project currentProject;

    private WalletApiKeyState() {}

    public static synchronized WalletApiKeyState getInstance() {
        if (instance == null) {
            instance = new WalletApiKeyState();
        }
        return instance;
    }

    public void setProject(Project project) {
        this.currentProject = project;
    }

    public void setNetwork(String network) {
        this.network = network;
        SecureStorageUtil.storeCredential("wallet_network",network);

        updateStatusBar();
    }

    public String getNetwork() {
        return network;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        SecureStorageUtil.storeCredential("wallet_apikey",apiKey);
    }

    public String getApiKey() {
        return apiKey;
    }

    private void updateStatusBar() {
        if (currentProject == null) return;
        ApplicationManager.getApplication().invokeLater(() -> {
            StatusBar statusBar = WindowManager.getInstance().getStatusBar(currentProject);
            if (statusBar != null) {
                statusBar.updateWidget("CardanoNetworkStatus");
            }
        });
    }
}
