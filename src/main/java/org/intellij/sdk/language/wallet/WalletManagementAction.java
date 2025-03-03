package org.intellij.sdk.language.wallet;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class WalletManagementAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String apiKey = WalletApiKeyState.getInstance().getApiKey();
        String storedMnemonic = SecureStorageUtil.retrieveCredential("wallet_mnemonic");
        if (apiKey == null || apiKey.isEmpty()) {
            WalletApiKeyDialog apiKeyDialog = new WalletApiKeyDialog();
            apiKeyDialog.show();
            if (apiKeyDialog.isCanceled()) {
                return;
            }
        }
        if (storedMnemonic != null) {
            WalletLoginDialog walletLoginDialog = new WalletLoginDialog();
            walletLoginDialog.show();
        } else {
            WalletOptionsDialog walletOptionsDialog = new WalletOptionsDialog();
            walletOptionsDialog.show();
        }
    }
}