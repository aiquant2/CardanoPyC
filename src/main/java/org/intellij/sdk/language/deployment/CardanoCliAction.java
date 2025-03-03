package org.intellij.sdk.language.deployment;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.wm.RegisterToolWindowTask;
import com.intellij.util.io.BaseOutputReader;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.application.ApplicationManager;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class CardanoCliAction extends AnAction {
    private OSProcessHandler processHandler;
    public BufferedWriter processInput;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        // Prompt user for the CARDANO_NODE_SOCKET_PATH
        JBPanel<?> panel;
        JBTextField nodeSocket;
        int result;
        String socketPath;

        do {
            panel = new JBPanel<>(new GridLayout(0, 1));
            nodeSocket = new JBTextField();
            panel.add(new JLabel("Node socket filepath:"));
            panel.add(nodeSocket);

            result = JOptionPane.showConfirmDialog(null, panel, "Socket Path Input", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            socketPath = nodeSocket.getText().trim();

            if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                return;
            }

            if (socketPath.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Socket path is required.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } while (socketPath.isEmpty());
        final String path = socketPath;

        // Proceed with the valid socketPath
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Cardano CLI Terminal");
        if (toolWindow == null) {
            toolWindow = ToolWindowManager.getInstance(project).registerToolWindow(
                    new RegisterToolWindowTask(
                            "Cardano CLI Terminal",
                            ToolWindowAnchor.BOTTOM,
                            null,
                            false,
                            true,
                            true,
                            true,
                            null,
                            null,
                            null
                    )
            );
        }

        ToolWindow finalToolWindow = toolWindow;
        toolWindow.show(() -> {
            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                processInput = new BufferedWriter(new OutputStreamWriter(processHandler.getProcessInput()));

                ApplicationManager.getApplication().invokeLater(() -> {
                    ConsoleView consoleView = new ConsoleViewImpl(project, true);
                    JBPanel<?> terminalPanel = createTerminalPanel(consoleView);

                    Content content = ContentFactory.getInstance().createContent(
                            terminalPanel,
                            "Cardano CLI Terminal",
                            false
                    );
                    finalToolWindow.getContentManager().addContent(content);

                    consoleView.clear();
                    consoleView.attachToProcess(processHandler);
                    processHandler.startNotify();

                    Disposer.register(content, consoleView);
                });

            });
        });

        // Ensure proper cleanup
        Disposable disposable = Disposer.newDisposable();
        Disposer.register(disposable, () -> {
            if (processHandler != null) {
                try {
                    processHandler.destroyProcess();
                } catch (Exception ex) {
                    System.err.println("Failed to destroy process: " + ex.getMessage());
                }
            }

            if (processInput != null) {
                try {
                    processInput.close();
                } catch (Exception ex) {
                    System.err.println("Failed to close process input: " + ex.getMessage());
                }
            }
        });

        Disposer.dispose(disposable);
    }

    public JBPanel<?> createTerminalPanel(ConsoleView consoleView) {
        JBPanel<?> terminalPanel = new JBPanel<>(new BorderLayout());
        terminalPanel.add(consoleView.getComponent(), BorderLayout.CENTER);

        JBTextField inputField = new JBTextField();
        inputField.addActionListener(event -> sendCommand(inputField, consoleView));
        terminalPanel.add(inputField, BorderLayout.SOUTH);

        JBPanel<?> buttonPanel = new JBPanel<>(new FlowLayout());
        JBPanel<?> runAddressCommandButton = createRunAddressCommandButton(consoleView);
        buttonPanel.add(runAddressCommandButton);

        terminalPanel.add(buttonPanel, BorderLayout.NORTH);

        return terminalPanel;
    }

    private JBPanel<?> createRunAddressCommandButton(ConsoleView consoleView) {
        JBPanel<?> buttonPanel = new JBPanel<>(new FlowLayout());

        JButton buildAddressButton = getJButton(consoleView);
        buttonPanel.add(buildAddressButton);

        JButton runScriptInfoButton = getButton(consoleView);
        buttonPanel.add(runScriptInfoButton);

        JButton runTransactionBuildButton = getRunTransactionBuildButton(consoleView);
        buttonPanel.add(runTransactionBuildButton);

        JButton runTransactionWithdrawButton = new JButton("Withdraw Transaction");
        runTransactionWithdrawButton.addActionListener(e -> {
            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.setPreferredSize(new Dimension(500, 600));
            JBTextField txInField = new JBTextField();
            JBTextField txPlutus= new JBTextField();
            JBTextField changeAddrField = new JBTextField();
            JBTextField signingKeyFileField = new JBTextField();
            JBTextField collateral= new JBTextField();
            JBTextField redeemerField= new JBTextField();
            ComboBox<String> network;
            JBTextField datumPath= new JBTextField();
            ComboBox<String> dautumField;
            JBTextField others=new JBTextField();

            panel.add(new JLabel("Transaction Input UTXO add index:"));
            panel.add(txInField);
            panel.add(new JLabel("Plutus  File Path :"));
            panel.add(txPlutus);
            panel.add(new JLabel("Change Address File Path :"));
            panel.add(changeAddrField);
            panel.add(new JLabel("Signing Key File Path :"));
            panel.add(signingKeyFileField);
            panel.add(new JLabel("Collateral UTXO and index:"));
            panel.add(collateral);
            panel.add(new JLabel("Redeemer File Path:"));
            panel.add(redeemerField);
            panel.add(new JLabel("Network Type:"));
            network = new ComboBox<>(new String[]{"preview", "preprod", "mainnet"});
            panel.add(network);
            panel.add(new JLabel("Datum Type:"));
             dautumField= new ComboBox<>(new String[]{"Inline", "DatumFilePath", "DatumHash"});
            panel.add(dautumField);
            panel.add(new JLabel("Datum Path:"));
            panel.add(datumPath);
            panel.add(new JLabel("If other command is needed, please enter here:"));
            panel.add(others);
            String datumType = Objects.requireNonNull(dautumField.getSelectedItem()).toString();
            String networkType = Objects.requireNonNull(network.getSelectedItem()).toString();
            String networkFlag;
            String datumFlag;

            switch (networkType) {
                case "preview":
                    networkFlag = "--testnet-magic 2";
                    break;
                case "preprod":
                    networkFlag = "--testnet-magic 1";
                    break;
                case "mainnet":
                    networkFlag = "--mainnet-magic 764824073";
                    break;
                default:
                    consoleView.print("Error: Unknown network type selected.\n", ConsoleViewContentType.ERROR_OUTPUT);
                    return;
            }
            switch (datumType){
                case "Inline":
                    datumFlag="--tx-in-inline-datum-present";
                    break;
                case "DatumFilePath":
                    datumFlag="--tx-in-datum-file" + datumPath;
                    break;
                case "DatumHash":
                    datumFlag="--tx-in-datum-hash" + datumPath;
                    break;
                default:
                    consoleView.print("Error: Unknown datum type selected.\n", ConsoleViewContentType.ERROR_OUTPUT);
                    return;
            }

            int result = JOptionPane.showConfirmDialog(null, panel, "Enter Transaction Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) {
                consoleView.print("Transaction build canceled by user.\n", ConsoleViewContentType.ERROR_OUTPUT);
                return;
            }

            String txIn = txInField.getText().trim();
            String txScript = txPlutus.getText().trim();
            String changeAddr = changeAddrField.getText().trim();
            String signingKeyFile = signingKeyFileField.getText().trim();
            String collaterals = collateral.getText().trim();
            String redeemer= redeemerField.getText().trim();
            String othersCommand = others.getText().trim();

            if (txIn.isEmpty() || txScript.isEmpty() || changeAddr.isEmpty() || signingKeyFile.isEmpty() || collaterals.isEmpty() || redeemer.isEmpty()) {
                consoleView.print("Error: All fields must be filled out.\n", ConsoleViewContentType.ERROR_OUTPUT);
                return;
            }
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String buildTransactionCommand = String.join(" ",
                    "cardano-cli conway transaction build",
                    networkFlag,
                    "--tx-in " + txIn,
                    "--tx-in-script-file"+ " " + txScript,
                    datumFlag,
                    "--tx-in-redeemer-file"+ " " + redeemer,
                    "--tx-in-collateral " + collaterals,
                    "--change-address $(< " + changeAddr + ")",
                    "--out-file tx_" + timestamp + ".raw"
            );
            if (!othersCommand.isEmpty()) {
                buildTransactionCommand += " " + othersCommand;
            }
            if (executeCommandWithCheck(buildTransactionCommand, consoleView)) {
                consoleView.print("Transaction build failed. Stopping execution.\n", ConsoleViewContentType.ERROR_OUTPUT);
                return;
            }

            String signTransactionCommand = String.join(" ",
                    "cardano-cli conway transaction sign",
                    "--tx-body-file tx_" + timestamp + ".raw",
                    "--signing-key-file " + signingKeyFile,
                    networkFlag,
                    "--out-file tx_" + timestamp + ".signed"
            );
            if (executeCommandWithCheck(signTransactionCommand, consoleView)) {
                consoleView.print("Transaction signing failed. Stopping execution.\n", ConsoleViewContentType.ERROR_OUTPUT);
                return;
            }

            // Step 3: Submit Transaction
            String submitTransactionCommand = String.join(" ",
                    "cardano-cli conway transaction submit",
                    networkFlag,
                    "--tx-file tx_" + timestamp + ".signed"
            );
            if (executeCommandWithCheck(submitTransactionCommand, consoleView)) {
                consoleView.print("Transaction submission failed. Stopping execution.\n", ConsoleViewContentType.ERROR_OUTPUT);
            }
        });
        buttonPanel.add(runTransactionWithdrawButton);

        return buttonPanel;
    }

    private @NotNull JButton getRunTransactionBuildButton(ConsoleView consoleView) {
        JButton runTransactionBuildButton = new JButton("Build Transaction");
        runTransactionBuildButton.addActionListener(e -> {
            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.setPreferredSize(new Dimension(500, 600));
            JBTextField txInField = new JBTextField();
            JBTextField txOutAddrField = new JBTextField();
            JBTextField txOutAmountField = new JBTextField();
            JBTextField datumTypeField = new JBTextField();
            JBTextField changeAddrField = new JBTextField();
            JBTextField signingKeyFileField = new JBTextField();
            JBTextField othersField = new JBTextField();
            ComboBox<String> network;

            panel.add(new JLabel("Transaction Input UTXO and index:"));
            panel.add(txInField);
            panel.add(new JLabel("Transaction Output Address File Path :"));
            panel.add(txOutAddrField);
            panel.add(new JLabel("Transaction Output Amount :"));
            panel.add(txOutAmountField);
            panel.add(new JLabel("Datum File Path :"));
            panel.add(datumTypeField);
            panel.add(new JLabel("Change Address File Path :"));
            panel.add(changeAddrField);
            panel.add(new JLabel("Signing Key File Path :"));
            panel.add(signingKeyFileField);
            panel.add(new JLabel("Network Type:"));
            network = new ComboBox<>(new String[]{"preview", "preprod", "mainnet"});
            panel.add(network);
            String networkType = Objects.requireNonNull(network.getSelectedItem()).toString();
            String networkFlag;
            panel.add(new JLabel("If other command is needed, please enter here:"));
            panel.add(othersField);


            switch (networkType) {
                case "preview":
                    networkFlag = "--testnet-magic 2";
                    break;
                case "preprod":
                    networkFlag = "--testnet-magic 1";
                    break;
                case "mainnet":
                    networkFlag = "--mainnet-magic 764824073";
                    break;
                default:
                    consoleView.print("Error: Unknown network type selected.\n", ConsoleViewContentType.ERROR_OUTPUT);
                    return;
            }

            int result = JOptionPane.showConfirmDialog(null, panel, "Enter Transaction Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) {
                consoleView.print("Transaction build canceled by user.\n", ConsoleViewContentType.ERROR_OUTPUT);
                return;
            }

            String txIn = txInField.getText().trim();
            String txOutAddr = txOutAddrField.getText().trim();
            String txOutAmount = txOutAmountField.getText().trim();
            String datumType = datumTypeField.getText().trim();
            String changeAddr = changeAddrField.getText().trim();
            String signingKeyFile = signingKeyFileField.getText().trim();
            String othersCommand = othersField.getText().trim();
            String autoGenString = UUID.randomUUID().toString();

            if (txIn.isEmpty() || txOutAddr.isEmpty() || txOutAmount.isEmpty() ||  changeAddr.isEmpty() || signingKeyFile.isEmpty()) {
                consoleView.print("Error: All fields must be filled out.\n", ConsoleViewContentType.ERROR_OUTPUT);
                return;
            }

            // Step 1: Build Transaction
            String buildTransactionCommand = String.join(" ",
                    "cardano-cli conway transaction build",
                    networkFlag,
                    "--tx-in " + txIn,
                    "--tx-out $(< " + txOutAddr + ")+" + txOutAmount,
                    "--change-address $(< " + changeAddr + ")",
                    "--out-file "+autoGenString+".raw"
            );
            if (!othersCommand.isEmpty()) {
                buildTransactionCommand += " " + othersCommand;
            }
            if (executeCommandWithCheck(buildTransactionCommand, consoleView)) {
                consoleView.print("Transaction build failed. Stopping execution.\n", ConsoleViewContentType.ERROR_OUTPUT);
                return;
            }

            String signTransactionCommand = String.join(" ",
                    "cardano-cli conway transaction sign",
                    "--tx-body-file "+autoGenString+".raw",
                    networkFlag,
                    "--out-file "+autoGenString+".signed"
            );
            if (executeCommandWithCheck(signTransactionCommand, consoleView)) {
                consoleView.print("Transaction signing failed. Stopping execution.\n", ConsoleViewContentType.ERROR_OUTPUT);
                return;
            }

            String submitTransactionCommand = String.join(" ",
                    "cardano-cli conway transaction submit",
                    networkFlag,
                    "--tx-file "+autoGenString+".signed"
            );
            if (executeCommandWithCheck(submitTransactionCommand, consoleView)) {
                consoleView.print("Transaction submission failed. Stopping execution.\n", ConsoleViewContentType.ERROR_OUTPUT);
            }
        });
        return runTransactionBuildButton;
    }

    private @NotNull JButton getButton(ConsoleView consoleView) {
        JButton runScriptInfoButton = new JButton("Utxo Info");
        runScriptInfoButton.addActionListener(e -> {
            JPanel panel = new JPanel(new GridLayout(0, 1));
            JTextField addrField = new JTextField();
            JComboBox<String> networkComboBox = new JComboBox<>(new String[]{"preview", "preprod", "mainnet"});

            panel.add(new JLabel("Enter the address file path:"));
            panel.add(addrField);
            panel.add(new JLabel("Select Network:"));
            panel.add(networkComboBox);

            int result = JOptionPane.showConfirmDialog(null, panel, "Address and Network Input", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Address and network selection are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String addr = addrField.getText().trim();
            String networkType = (String) networkComboBox.getSelectedItem();
            String networkFlag;

            switch (Objects.requireNonNull(networkType)) {
                case "preview":
                    networkFlag = "--testnet-magic 2";
                    break;
                case "preprod":
                    networkFlag = "--testnet-magic 1";
                    break;
                case "mainnet":
                    networkFlag = "--mainnet-magic 764824073";
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Unknown network type selected.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
            }

            if (addr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Enter the address file path.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String predefinedCommand = "cardano-cli query utxo --address $(< " + addr + ") " + networkFlag;
            sendPredefinedCommand(predefinedCommand, consoleView);
        });
        return runScriptInfoButton;
    }

    private @NotNull JButton getJButton(ConsoleView consoleView) {
        JButton buildAddressButton = new JButton("Build Plutus Address");
        buildAddressButton.addActionListener(e -> {
            JPanel panel = new JPanel(new GridLayout(0, 1));
            JBTextField plutusFilePathField = new JBTextField();
            JComboBox<String> networkComboBox = new JComboBox<>(new String[]{"preview", "preprod", "mainnet"});

            panel.add(new JLabel("Enter the path to your .plutus file (ex: script):"));
            panel.add(plutusFilePathField);
            panel.add(new JLabel("Select Network:"));
            panel.add(networkComboBox);

            int result = JOptionPane.showConfirmDialog(null, panel, "Plutus File Path and Network Input", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                String plutusFilePath = plutusFilePathField.getText().trim();
                String networkType = (String) networkComboBox.getSelectedItem();
                String networkFlag;

                switch (Objects.requireNonNull(networkType)) {
                    case "preview":
                        networkFlag = "--testnet-magic 2";
                        break;
                    case "preprod":
                        networkFlag = "--testnet-magic 1";
                        break;
                    case "mainnet":
                        networkFlag = "--mainnet-magic 764824073";
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Unknown network type selected.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                }

                if (plutusFilePath.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No .plutus file path provided.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String predefinedCommand = String.join(" ",
                        "cardano-cli conway address build",
                        "--payment-script-file " + plutusFilePath + ".plutus",
                        "--out-file " + plutusFilePath + ".addr"
                );
                sendPredefinedCommand(predefinedCommand, consoleView);
            }
        });
        return buildAddressButton;
    }

    public boolean executeCommandWithCheck(String command, ConsoleView consoleView) {
        try {
            if (processInput != null) {
                processInput.write(command + "\n");
                processInput.flush();
            }

            consoleView.print( command + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
            Thread.sleep(500);
            boolean isSuccess = Math.random() > 0.1;

            if (!isSuccess) {
                throw new RuntimeException("Simulated failure for command: " + command);
            }

            return false; // Command succeeded
        } catch (Exception ex) {
            consoleView.print("Error: " + ex.getMessage() + "\n", ConsoleViewContentType.ERROR_OUTPUT);
            return true; // Command failed
        }
    }

    private void sendCommand(JBTextField inputField, ConsoleView consoleView) {
        String userInput = inputField.getText();
        inputField.setText(""); // Clear input field after enter
        if (processInput != null) {
            try {
                processInput.write(userInput + "\n");
                processInput.flush();
            } catch (Exception ex) {
                consoleView.print("Error: " + ex.getMessage() + "\n", ConsoleViewContentType.ERROR_OUTPUT);
            }
        }
    }

    private void sendPredefinedCommand(String predefinedCommand, ConsoleView consoleView) {
        try {
            if (processInput != null) {
                processInput.write(predefinedCommand + "\n");
                processInput.flush();
            }

            // After command execution, print a success message
            consoleView.print("Command executed successfully.\n", ConsoleViewContentType.NORMAL_OUTPUT);
        } catch (Exception ex) {
            consoleView.print("Error: " + ex.getMessage() + "\n", ConsoleViewContentType.ERROR_OUTPUT);
        }
    }
}