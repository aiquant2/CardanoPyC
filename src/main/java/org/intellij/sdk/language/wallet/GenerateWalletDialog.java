package org.intellij.sdk.language.wallet;

            import com.bloxbean.cardano.client.account.Account;
            import com.bloxbean.cardano.client.common.model.Networks;
            import com.intellij.openapi.ui.DialogWrapper;
            import org.jetbrains.annotations.Nullable;

            import javax.swing.*;
            import java.awt.*;

            public class GenerateWalletDialog extends DialogWrapper {
                public String mnemonic;
                public String baseAddress;

                public JTextField usernameField;
                public JPasswordField passwordField;

                public GenerateWalletDialog(String network) {
                    super(true); // Modal dialog
                    setTitle("Save Your Seed Phrase & Credentials");
                    generateWallet(network);
                    init();
                }

                public void generateWallet(String network) {
                    Account account;
                    if ("preview".equalsIgnoreCase(network)) {
                        account = new Account(Networks.preview());
                    } else if ("preprod".equalsIgnoreCase(network)) {
                        account = new Account(Networks.preprod());
                    } else {
                        account = new Account(Networks.mainnet());
                    }

                    this.mnemonic = account.mnemonic();
                    this.baseAddress = account.baseAddress();
                }

                @Override
                protected @Nullable JComponent createCenterPanel() {
                    JPanel panel = new JPanel(new BorderLayout());
                    panel.setPreferredSize(new Dimension(350, 400));
                    JLabel instructionLabel = new JLabel("<html><b>Note:</b> Write down your seed phrase.<br>(This is the only way to recover your wallet)</html>");
                    instructionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
                    panel.add(instructionLabel, BorderLayout.NORTH);

                    JTextArea seedPhraseArea = new JTextArea(mnemonic);
                    seedPhraseArea.setLineWrap(true);
                    seedPhraseArea.setWrapStyleWord(true);
                    seedPhraseArea.setEditable(false);
                    seedPhraseArea.setFont(new Font("Monospaced", Font.BOLD, 14));
                    seedPhraseArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                    panel.add(new JScrollPane(seedPhraseArea), BorderLayout.CENTER);

                    // Username & Password Panel
                    JPanel credentialsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
                    credentialsPanel.setBorder(BorderFactory.createTitledBorder("Set Username & Password"));

                    credentialsPanel.add(new JLabel("Username:"));
                    usernameField = new JTextField();
                    credentialsPanel.add(usernameField);

                    credentialsPanel.add(new JLabel("Password:"));
                    passwordField = new JPasswordField();
                    credentialsPanel.add(passwordField);

                    panel.add(credentialsPanel, BorderLayout.SOUTH);

                    return panel;
                }

                @Override
                public void doOKAction() {
                    String username = usernameField.getText();
                    String password = new String(passwordField.getPassword());

                    if (username.isEmpty() || password.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Username and Password cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if(SecureStorageUtil.retrieveCredential("wallet_mnemonic") != null) {
                        SecureStorageUtil.removeCredential("wallet_mnemonic");
                        SecureStorageUtil.removeCredential("wallet_baseAddress");
                        SecureStorageUtil.removeCredential("wallet_username");
                        SecureStorageUtil.removeCredential("wallet_password");
                    }

                    SecureStorageUtil.storeCredential("wallet_mnemonic", mnemonic);
                    SecureStorageUtil.storeCredential("wallet_baseAddress", baseAddress);
                    SecureStorageUtil.storeCredential("wallet_username", username);
                    SecureStorageUtil.storeCredential("wallet_password", password);

                    // Inform user of successful wallet creation
                    JOptionPane.showMessageDialog(null, "Wallet successfully created & stored!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    close(OK_EXIT_CODE);
                    new WalletActionsDialog().show();close(OK_EXIT_CODE);
                }
            }