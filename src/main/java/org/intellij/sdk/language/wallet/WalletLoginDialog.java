

package org.intellij.sdk.language.wallet;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class WalletLoginDialog extends DialogWrapper {

    protected JTextField usernameField;
    protected JPasswordField passwordField;
    protected JButton loginButton;

    public WalletLoginDialog() {
        super(true);
        setTitle("Login to Existing Wallet");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Enter your username:");
        usernameField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Enter your password:");
        passwordField = new JPasswordField(20);

        loginButton = new JButton("Login");
        styleLoginButton(loginButton);
        loginButton.addActionListener(e -> onLogin());

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(usernameLabel, gbc);
        gbc.gridy++;
        panel.add(usernameField, gbc);

        gbc.gridy++;
        panel.add(passwordLabel, gbc);
        gbc.gridy++;
        panel.add(passwordField, gbc);

        gbc.gridy++;
        panel.add(loginButton, gbc);

        return panel;
    }

    private void styleLoginButton(JButton button) {
        button.setBackground(new Color(33, 150, 243)); // Blue
        button.setForeground(Color.WHITE); // White text
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }

    private void onLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Both fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ✅ Save credentials securely
        SecureStorageUtil.storeCredential("wallet_username", username);
        SecureStorageUtil.storeCredential("wallet_password", password);

        // ✅ Show wallet page after successful login
        close(OK_EXIT_CODE);
        WalletActionsDialog walletActionsDialog = new WalletActionsDialog();
        walletActionsDialog.show();
    }

    @Override
    protected Action @NotNull [] createActions() {
        // ✅ Prevents default OK/Cancel from showing
        return new Action[0];
    }
}

