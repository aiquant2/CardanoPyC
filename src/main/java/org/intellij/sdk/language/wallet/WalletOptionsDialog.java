

package org.intellij.sdk.language.wallet;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WalletOptionsDialog extends DialogWrapper {

    public WalletOptionsDialog() {
        super(true);
        setResizable(false);// This means it's a modal dialog
        init();
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new BackgroundImagePanel("/icons/background.jpg");
        panel.setPreferredSize(new Dimension(350, 450)); // increased height
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Wallet Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setOpaque(false);

        JButton createWalletButton = createStyledButton("Create Wallet");
        JButton restoreWalletButton = createStyledButton("Restore Wallet");
        JButton viewBalanceButton = createStyledButton("View Balance");
        JButton loginWalletButton = createStyledButton("Log into Existing Wallet"); // NEW BUTTON

        createWalletButton.addActionListener(e -> onCreateWallet());
        restoreWalletButton.addActionListener(e -> onRestoreWallet());
        viewBalanceButton.addActionListener(e -> onViewBalance());
        loginWalletButton.addActionListener(e -> onLoginWallet()); // action

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        buttonPanel.add(createWalletButton, gbc);
        buttonPanel.add(restoreWalletButton, gbc);
        buttonPanel.add(viewBalanceButton, gbc);
        buttonPanel.add(loginWalletButton, gbc); // added button

        panel.add(buttonPanel);

        return panel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(250, 45));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(JBColor.namedColor("Button.background", new Color(70, 130, 180)));
        button.setForeground(JBColor.namedColor("Button.foreground", JBColor.WHITE));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(JBColor.namedColor("Button.borderColor", new Color(93, 147, 201)), 2),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(JBColor.namedColor("Button.hoverBackground", new Color(93, 147, 201)));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(JBColor.namedColor("Button.background", new Color(70, 130, 180)));
            }
        });
        return button;
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[0];
    }

    private void onCreateWallet() {
        close(OK_EXIT_CODE);
        String network = WalletApiKeyState.getInstance().getNetwork();
        GenerateWalletDialog generateWalletDialog = new GenerateWalletDialog(network);
        generateWalletDialog.show();
    }

    private void onRestoreWallet() {
        close(OK_EXIT_CODE);
        WalletRestoreDialog restoreDialog = new WalletRestoreDialog();
        restoreDialog.show();
    }

    private void onViewBalance() {
        close(OK_EXIT_CODE);
        ViewBalanceDialog viewDialog = new ViewBalanceDialog();
        viewDialog.show();
    }

    private void onLoginWallet() {
        close(OK_EXIT_CODE);
        WalletLoginDialog loginDialog = new WalletLoginDialog();
        loginDialog.show();
    }
}
