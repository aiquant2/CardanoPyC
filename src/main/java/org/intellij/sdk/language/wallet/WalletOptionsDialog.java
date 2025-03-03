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
        panel.setPreferredSize(new Dimension(350, 400)); // Adjusted size for a balanced layout
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Use BorderLayout for better positioning
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Consistent padding

        JLabel titleLabel = new JLabel("Wallet Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setOpaque(false); // 2 rows, 1 column with spacing

        JButton createWalletButton = createStyledButton("Create Wallet");
        JButton restoreWalletButton = createStyledButton("Restore Wallet");

        createWalletButton.addActionListener(e -> onCreateWallet());
        restoreWalletButton.addActionListener(e -> onRestoreWallet());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0); // Vertical space between buttons

        buttonPanel.add(createWalletButton, gbc);
        buttonPanel.add(restoreWalletButton,gbc);

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
                BorderFactory.createLineBorder(JBColor.namedColor("Button.borderColor", new Color(93, 147, 201)), 2),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(JBColor.namedColor("Button.hoverBackground", new Color(93, 147, 201))); // Lighter blue on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(JBColor.namedColor("Button.background", new Color(70, 130, 180))); // Restore original color
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
}