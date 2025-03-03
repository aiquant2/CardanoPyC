package org.intellij.sdk.language.wallet;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class WalletLoginDialog extends DialogWrapper {
    private JPasswordField passwordField;

    public WalletLoginDialog() {
        super(true); // Modal dialog
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(350, 150));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel instructionLabel = new JLabel("<html><b>Enter your wallet password:</b></html>", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(instructionLabel, BorderLayout.NORTH);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        passwordPanel.add(passwordField);
        panel.add(passwordPanel, BorderLayout.CENTER);

       JButton forgotPasswordButton = new JButton("Forgot Password?");
        forgotPasswordButton.setFont(new Font("Arial", Font.PLAIN, 12));
        forgotPasswordButton.setForeground(JBColor.namedColor("Button.foreground", JBColor.BLUE));
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setFocusPainted(false);
        forgotPasswordButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotPasswordButton.addActionListener(e -> onForgotPassword());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(forgotPasswordButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }


    @Override
    protected void doOKAction() {
        String enteredPassword = new String(passwordField.getPassword());
        String storedPassword = SecureStorageUtil.retrieveCredential("wallet_password");

        if (enteredPassword.equals(storedPassword)) {
            close(OK_EXIT_CODE);
            new WalletActionsDialog().show();
        } else {
            JOptionPane.showMessageDialog(null, "❌ Incorrect Password!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onForgotPassword() {
        close(OK_EXIT_CODE);
       WalletOptionsDialog walletOptionsDialog = new WalletOptionsDialog();
       walletOptionsDialog.show();


    }
}
