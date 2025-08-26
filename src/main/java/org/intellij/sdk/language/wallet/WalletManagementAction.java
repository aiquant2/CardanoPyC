

package org.intellij.sdk.language.wallet;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import org.jetbrains.annotations.NotNull;

public class WalletManagementAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        String apiKey = SecureStorageUtil.retrieveCredential("wallet_apikey");
        String storedMnemonic = SecureStorageUtil.retrieveCredential("wallet_mnemonic");

        if (apiKey == null || apiKey.isEmpty()) {
            WalletApiKeyDialog apiKeyDialog = new WalletApiKeyDialog();
            apiKeyDialog.show();
            if (apiKeyDialog.isCanceled()) {
                return;
            }
        }

        if (apiKey != null) {
//            // Show wallet login dialog
//            WalletLoginDialog walletLoginDialog = new WalletLoginDialog();
//            walletLoginDialog.show();


            // Open the WalletOptionsDialog directly
            WalletOptionsDialog dialog = new WalletOptionsDialog();
            dialog.show();


            String selectedNetwork= SecureStorageUtil.retrieveCredential("wallet_network");

            WalletApiKeyState.getInstance().setNetwork(selectedNetwork);

            // Refresh the status bar widget
            StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
            if (statusBar != null) {
                statusBar.updateWidget("CardanoNetworkStatus");
            }

        } else {
            WalletOptionsDialog walletOptionsDialog = new WalletOptionsDialog();
            walletOptionsDialog.show();
        }
    }
}


