//package org.intellij.sdk.language.wallet;
//
//
//import com.bloxbean.cardano.client.account.Account;
//import com.bloxbean.cardano.client.common.model.Networks;
//import com.intellij.openapi.ui.DialogWrapper;
//import com.intellij.ui.JBColor;
//import com.intellij.ui.components.JBScrollPane;
//import org.jetbrains.annotations.Nullable;
//
//import javax.swing.*;
//import java.awt.*;
//
//public class WalletRestoreDialog extends DialogWrapper {
//    private JTextArea seedPhraseField;
//    private JTextField usernameField;
//    private JPasswordField passwordField;
//
//    public WalletRestoreDialog() {
//        super(true);
//        setTitle("Restore Wallet");
//        init();
//    }
//
//    @Override
//    protected @Nullable JComponent createCenterPanel() {
//        JPanel panel = new JPanel();
//        panel.setPreferredSize(new Dimension(350, 400));
//        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//
//        JLabel seedLabel = new JLabel("Enter your 24-word seed phrase:");
//        seedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
//        panel.add(seedLabel);
//
//        seedPhraseField = new JTextArea(3, 20);
//        seedPhraseField.setLineWrap(true);
//        seedPhraseField.setWrapStyleWord(true);
//        seedPhraseField.setBorder(BorderFactory.createLineBorder(JBColor.GRAY));
//
//        JBScrollPane seedScroll = new JBScrollPane(seedPhraseField);
//        seedScroll.setPreferredSize(new Dimension(310, 60));
//        panel.add(seedScroll);
//
//        JLabel usernameLabel = new JLabel("Set Wallet Username:");
//        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
//        panel.add(usernameLabel);
//
//        usernameField = new JTextField();
//        usernameField.setMaximumSize(new Dimension(310, 30));
//        panel.add(usernameField);
//
//        JLabel passwordLabel = new JLabel("Set Wallet Password:");
//        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
//        panel.add(passwordLabel);
//
//        passwordField = new JPasswordField();
//        passwordField.setMaximumSize(new Dimension(310, 30));
//        panel.add(passwordField);
//
//        return panel;
//    }
//
//
//    @Override
//    protected void doOKAction() {
//        String seedPhrase = seedPhraseField.getText().trim();
//        String username = usernameField.getText().trim();
//        String password = new String(passwordField.getPassword());
//
//
//        if (seedPhrase.isEmpty() || username.isEmpty() || password.isEmpty()) {
//            JOptionPane.showMessageDialog(null, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        String[] words = seedPhrase.split("\\s+");
//        if (words.length != 24) {
//            JOptionPane.showMessageDialog(null, "Invalid Seed Phrase! It must contain exactly 24 words.", "Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//
//        Account account;
//        String selectedNetwork = WalletApiKeyState.getInstance().getNetwork();
//
//        account = switch (selectedNetwork.toLowerCase()) {
//            case "preprod" -> new Account(Networks.preprod(), seedPhrase);
//            case "preview" -> new Account(Networks.preview(), seedPhrase);
//            default -> new Account(Networks.mainnet(), seedPhrase);
//        };
//
//        String restoredAddress = account.baseAddress();
//
//
//        SecureStorageUtil.storeCredential("wallet_username", username);
//        SecureStorageUtil.storeCredential("wallet_password", password);
//        SecureStorageUtil.storeCredential("wallet_baseAddress", restoredAddress);
//        SecureStorageUtil.storeCredential("wallet_mnemonic",seedPhrase);
//
//
//        JOptionPane.showMessageDialog(null, "Wallet Restored Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
//
//        close(OK_EXIT_CODE);
//        WalletActionsDialog walletActionsDialog = new WalletActionsDialog();
//        walletActionsDialog.show();
//
//    }
//}

//
package org.intellij.sdk.language.wallet;

import com.bloxbean.cardano.client.account.Account;
import com.bloxbean.cardano.client.common.model.Networks;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class WalletRestoreDialog extends DialogWrapper {
    private JTextArea seedPhraseField;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public WalletRestoreDialog() {
        super(true);
        setTitle("Restore Wallet");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(350, 400));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Seed phrase
        JLabel seedLabel = new JLabel("Enter your 24-word seed phrase:");
        seedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(seedLabel);

        seedPhraseField = new JTextArea(3, 20);
        seedPhraseField.setLineWrap(true);
        seedPhraseField.setWrapStyleWord(true);
        seedPhraseField.setBorder(BorderFactory.createLineBorder(JBColor.GRAY));

        JBScrollPane seedScroll = new JBScrollPane(seedPhraseField);
        seedScroll.setPreferredSize(new Dimension(310, 60));
        panel.add(seedScroll);

        // Username
        JLabel usernameLabel = new JLabel("Set Wallet Username:");
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(310, 30));
        panel.add(usernameField);

        // Password
        JLabel passwordLabel = new JLabel("Set Wallet Password:");
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(310, 30));
        panel.add(passwordField);

        return panel;
    }

    @Override
    protected void doOKAction() {
        String seedPhrase = seedPhraseField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (seedPhrase.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "All fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] words = seedPhrase.split("\\s+");
        if (words.length != 24) {
            JOptionPane.showMessageDialog(null, "Invalid seed phrase! It must contain exactly 24 words.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String selectedNetwork = WalletApiKeyState.getInstance().getNetwork();
        System.out.println(selectedNetwork);
        if (selectedNetwork == null || selectedNetwork.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Network not selected! Please set the network in the Wallet API settings.", "Network Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 🔐 Restore account based on selected network
        Account account;
        try {
            account = switch (selectedNetwork.toLowerCase()) {
                case "preprod" -> new Account(Networks.preprod(), seedPhrase);
                case "preview" -> new Account(Networks.preview(), seedPhrase);
                default -> new Account(Networks.mainnet(), seedPhrase);
            };
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to restore wallet: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String restoredAddress = account.baseAddress();

        // 🔐 Save credentials securely
        SecureStorageUtil.storeCredential("wallet_username", username);
        SecureStorageUtil.storeCredential("wallet_password", password);
        SecureStorageUtil.storeCredential("wallet_baseAddress", restoredAddress);
        SecureStorageUtil.storeCredential("wallet_mnemonic", seedPhrase);

        JOptionPane.showMessageDialog(null, "✅ Wallet Restored Successfully!\nAddress: " + restoredAddress, "Success", JOptionPane.INFORMATION_MESSAGE);

        close(OK_EXIT_CODE);

        // 🎯 Launch wallet actions
        WalletActionsDialog walletActionsDialog = new WalletActionsDialog();
        walletActionsDialog.show();
    }
}
