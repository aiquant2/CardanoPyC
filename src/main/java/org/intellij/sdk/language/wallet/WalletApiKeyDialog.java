package org.intellij.sdk.language.wallet;

import com.intellij.openapi.ui.DialogWrapper;

import javax.swing.*;
import java.awt.*;

public class WalletApiKeyDialog extends DialogWrapper {
    private JTextField apiKeyField;
    private boolean canceled = false;

    public WalletApiKeyDialog() {
        super(true);
        setTitle("Enter BlockFrost API Key");
        init();
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Enter your BlockFrost API key:"), BorderLayout.NORTH);
        apiKeyField = new JTextField();
        panel.add(apiKeyField, BorderLayout.CENTER);
        return panel;
    }

    @Override
    protected void doOKAction() {
        String apiKey = apiKeyField.getText().trim();
        if (apiKey.isEmpty()) {
            JOptionPane.showMessageDialog(null, "API Key cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String network;
        if (apiKey.startsWith("preview")) {
            network = "preview";
        } else if (apiKey.startsWith("preprod")) {
            network = "preprod";
        } else if (apiKey.startsWith("mainnet")) {
            network = "mainnet";
        } else {
            JOptionPane.showMessageDialog(null, "Invalid API Key! Please enter a valid BlockFrost API key.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        WalletApiKeyState.getInstance().setNetwork(network);
        WalletApiKeyState.getInstance().setApiKey(apiKey);

        close(OK_EXIT_CODE);
        super.doOKAction();
    }

    @Override
    public void doCancelAction() {
        canceled = true;
        close(CANCEL_EXIT_CODE);
    }

    public boolean isCanceled() {
        return canceled;
    }
}