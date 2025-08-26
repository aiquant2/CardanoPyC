package org.intellij.sdk.language.wallet;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

        import javax.swing.*;
        import java.awt.*;
        import java.awt.datatransfer.Clipboard;
        import java.awt.datatransfer.StringSelection;
        import java.awt.event.MouseAdapter;
        import java.awt.event.MouseEvent;

public class WalletActionsDialog extends DialogWrapper {

            public WalletActionsDialog() {
                super(true); // Modal dialog
                setTitle("Cardano Wallet");
                setResizable(false);
                init();
            }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new BackgroundImagePanel("/icons/background.jpg");
        panel.setPreferredSize(new Dimension(350, 400)); // Set fixed size
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Vertical layout
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setOpaque(false);

        String username = SecureStorageUtil.retrieveCredential("wallet_username");
        if (username == null || username.isEmpty()) {
            username = "Unknown User";
        }

        JLabel userLabel = new JLabel(username, SwingConstants.CENTER);
        userLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setOpaque(false);

        JButton sendButton = createStyledButton("Send ADA");
        JButton receiveButton = createStyledButton("Receive ADA");
        JButton balanceButton = createStyledButton("View Balance");
        JButton logoutButton = createStyledButton("Logout");

        sendButton.addActionListener(e -> onSend());
        receiveButton.addActionListener(e -> onReceive());
        balanceButton.addActionListener(e -> onViewSpecificBalance());
        logoutButton.addActionListener(e -> onLogout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0); // Vertical space between buttons

        buttonPanel.add(sendButton, gbc);
        buttonPanel.add(receiveButton, gbc);
        buttonPanel.add(balanceButton, gbc);
        buttonPanel.add(logoutButton, gbc);

        panel.add(userLabel);
        panel.add(Box.createVerticalStrut(20)); // Space between label and buttons
        panel.add(buttonPanel);

        return panel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(250, 45)); // Slightly larger buttons
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(JBColor.namedColor("Button.background", new Color(70, 130, 180))); // Stylish blue color
        button.setForeground(JBColor.namedColor("Button.foreground", JBColor.WHITE)); // White text for contrast
        button.setFocusPainted(false);
        button.setOpaque(true); // Ensure background is visible
        button.setContentAreaFilled(true); // Make sure the color fills the button
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(JBColor.namedColor("Button.borderColor", new Color(50, 90, 140)), 2),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(JBColor.namedColor("Button.hoverBackground", new Color(100, 160, 220))); // Lighter blue on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(JBColor.namedColor("Button.background", new Color(70, 130, 180))); // Restore original color
            }
        });
        return button;
    }

            private void onSend() {
                new SendAdaDialog().show();
            }

          private void onReceive() {
                      String baseAddress = SecureStorageUtil.retrieveCredential("wallet_baseAddress");

                      if (baseAddress != null) {
                          JPanel panel = new JPanel(new BorderLayout());
                          JTextField textField = new JTextField(baseAddress);
                          textField.setEditable(false); // Prevent user from modifying the address
                          panel.add(textField, BorderLayout.CENTER);

                          JButton copyButton = new JButton("Copy");
                          copyButton.addActionListener(e -> {
                              StringSelection selection = new StringSelection(baseAddress);
                              Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                              clipboard.setContents(selection, selection);
                              copyButton.setText("Copied"); // Change button text to "Copied"
                          });
                          panel.add(copyButton, BorderLayout.EAST);

                          JOptionPane.showMessageDialog(null, panel, "Wallet Address", JOptionPane.INFORMATION_MESSAGE);
                      } else {
                          JOptionPane.showMessageDialog(null, "No wallet found. Please create or restore a wallet.", "Error", JOptionPane.ERROR_MESSAGE);
                      }
                  }

            private void onViewSpecificBalance() {

                ViewSpecificBalanceDialog viewSpecificDialog = new ViewSpecificBalanceDialog();
                 viewSpecificDialog.show();
                close(OK_EXIT_CODE);
            }

            private void onLogout() {
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to log out and clear all saved data?", "Logout Confirmation", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    clearSavedData();
                    JOptionPane.showMessageDialog(null, "Logged out successfully. All data has been cleared.", "Logged Out", JOptionPane.INFORMATION_MESSAGE);
                    close(OK_EXIT_CODE); // Optionally close the dialog after logout
                }
            }

            private void clearSavedData() {
                SecureStorageUtil.removeCredential("wallet_mnemonic");
                SecureStorageUtil.removeCredential("wallet_address");
                SecureStorageUtil.removeCredential("wallet_name");
                SecureStorageUtil.removeCredential("wallet_password");
                SecureStorageUtil.removeCredential("wallet_username");
            }

            @Override
            protected Action @NotNull [] createActions() {
                return new Action[0];
            }

            @Override
            protected void doOKAction() {
                close(OK_EXIT_CODE);
            }

        }