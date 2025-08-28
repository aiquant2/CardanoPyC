
package org.intellij.sdk.language.deployment;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.wm.RegisterToolWindowTask;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CardanoCliAction extends AnAction {
    private OSProcessHandler processHandler;
    private BufferedWriter processInput;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        // Prompt user for CARDANO_NODE_SOCKET_PATH
        String socketPath = promptForSocketPath();
        if (socketPath == null) return; // User cancelled

        // Initialize bash process
        try {
            GeneralCommandLine commandLine = new GeneralCommandLine("bash");
            commandLine.withWorkDirectory(project.getBasePath());

            processHandler = new OSProcessHandler(commandLine);
            processInput = new BufferedWriter(new OutputStreamWriter(processHandler.getProcessInput()));
        } catch (ExecutionException ex) {
            JOptionPane.showMessageDialog(null, "Failed to start process: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create or get tool window
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

        // Create console view and content
        ConsoleView consoleView = new ConsoleViewImpl(project, true);
        JBPanel<?> terminalPanel = createTerminalPanel(consoleView);

        Content content = ContentFactory.getInstance().createContent(
                terminalPanel,
                "Cardano CLI Terminal",
                false
        );
        toolWindow.getContentManager().addContent(content);
        Disposer.register(content, consoleView);

        // Attach process to console
        consoleView.clear();
        consoleView.attachToProcess(processHandler);
        processHandler.startNotify();

        // Show tool window
        toolWindow.show(() -> {});
    }

    private String promptForSocketPath() {
        String socketPath;
        int result;
        do {
            JBPanel<?> panel = new JBPanel<>(new GridLayout(0, 1));
            JBTextField nodeSocket = new JBTextField();
            panel.add(new JLabel("Node socket filepath:"));
            panel.add(nodeSocket);

            result = JOptionPane.showConfirmDialog(null, panel, "Socket Path Input",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            socketPath = nodeSocket.getText().trim();

            if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                return null;
            }

            if (socketPath.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Socket path is required.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } while (socketPath.isEmpty());

        return socketPath;
    }

    private JBPanel<?> createTerminalPanel(ConsoleView consoleView) {
        JBPanel<?> terminalPanel = new JBPanel<>(new BorderLayout());
        terminalPanel.add(consoleView.getComponent(), BorderLayout.CENTER);

        JBTextField inputField = new JBTextField();
        inputField.addActionListener(event -> sendCommand(inputField, consoleView));
        terminalPanel.add(inputField, BorderLayout.SOUTH);

        JBPanel<?> buttonPanel = new JBPanel<>(new FlowLayout());
        buttonPanel.add(getButton(consoleView));   // UTxO Info button
        buttonPanel.add(getJButton(consoleView));  // Build Address button
        terminalPanel.add(buttonPanel, BorderLayout.NORTH);

        return terminalPanel;
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

            int result = JOptionPane.showConfirmDialog(null, panel, "Address and Network Input",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) return;

            String addr = addrField.getText().trim();
            String networkType = (String) networkComboBox.getSelectedItem();
            String networkFlag = getNetworkFlag(networkType);

            if (networkFlag == null || addr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Address and network selection are required.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String predefinedCommand = "cardano-cli query utxo --address $(< " + addr + ") " + networkFlag;
            sendPredefinedCommand(predefinedCommand, consoleView);
        });
        return runScriptInfoButton;
    }

    private String getNetworkFlag(String networkType) {
        if (networkType == null) return null;
        switch (networkType) {
            case "preview":
                return "--testnet-magic 2";
            case "preprod":
                return "--testnet-magic 1";
            case "mainnet":
                return "--mainnet";
            default:
                return null;
        }
    }

    // ✅ Updated method: build script address + wait for CLI process
    private @NotNull JButton getJButton(ConsoleView consoleView) {
        JButton buildAddressButton = new JButton("Build Plutus Address");

        buildAddressButton.addActionListener(e -> {
            // Input form
            JPanel panel = new JPanel(new GridLayout(0, 1));
            JBTextField plutusFilePathField = new JBTextField();
            JComboBox<String> networkComboBox = new JComboBox<>(new String[]{"preview", "preprod", "mainnet"});

            panel.add(new JLabel("Enter the path to your Plutus script file (without extension):"));
            panel.add(plutusFilePathField);
            panel.add(new JLabel("Select Network:"));
            panel.add(networkComboBox);

            int result = JOptionPane.showConfirmDialog(
                    null, panel, "Plutus File Path and Network Input",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
            );

            if (result != JOptionPane.OK_OPTION) return;

            String plutusFilePath = plutusFilePathField.getText().trim();
            String networkType = (String) networkComboBox.getSelectedItem();
            String networkFlag = getNetworkFlag(networkType);

            if (networkFlag == null || plutusFilePath.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Plutus file path and network selection are required.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Ensure extensions
            String plutusFile = plutusFilePath.endsWith(".plutus") ? plutusFilePath : plutusFilePath + ".plutus";
            String addrFile = plutusFile.replace(".plutus", ".addr");

            try {
                // Build & run process

                ProcessBuilder pb;
                if (networkFlag.contains(" ")) {
                    String[] parts = networkFlag.split(" ");
                    pb = new ProcessBuilder(
                            "cardano-cli", "address", "build",
                            "--payment-script-file", plutusFile,
                            parts[0], parts[1],
                            "--out-file", addrFile
                    );
                } else {
                    pb = new ProcessBuilder(
                            "cardano-cli", "address", "build",
                            "--payment-script-file", plutusFile,
                            networkFlag,
                            "--out-file", addrFile
                    );
                }


                pb.redirectErrorStream(true);
                Process process = pb.start();

                // Wait until finished
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    Path addrPath = Paths.get(addrFile);
                    if (Files.exists(addrPath)) {
                        String scriptAddress = Files.readString(addrPath).trim();
                        consoleView.print("\nGenerated Script Address:\n" + scriptAddress + "\n",
                                ConsoleViewContentType.NORMAL_OUTPUT);
                    } else {
                        consoleView.print("\n[Error] Address file not found: " + addrFile + "\n",
                                ConsoleViewContentType.ERROR_OUTPUT);
                    }
                } else {
                    consoleView.print("\n[Error] cardano-cli failed with exit code: " + exitCode + "\n",
                            ConsoleViewContentType.ERROR_OUTPUT);
                }
            } catch (Exception ex) {
                consoleView.print("Error running cardano-cli: " + ex.getMessage() + "\n",
                        ConsoleViewContentType.ERROR_OUTPUT);
            }
        });

        return buildAddressButton;
    }

    private void sendCommand(JBTextField inputField, ConsoleView consoleView) {
        String userInput = inputField.getText();
        inputField.setText(""); // Clear input field after enter
        sendPredefinedCommand(userInput, consoleView);
    }

    private void sendPredefinedCommand(String predefinedCommand, ConsoleView consoleView) {
        if (processInput == null) {
            consoleView.print("Process input is not initialized\n", ConsoleViewContentType.ERROR_OUTPUT);
            return;
        }
        try {
            consoleView.print(predefinedCommand + "\n", ConsoleViewContentType.USER_INPUT);
            processInput.write(predefinedCommand + "\n");
            processInput.flush();
        } catch (Exception ex) {
            consoleView.print("Error: " + ex.getMessage() + "\n", ConsoleViewContentType.ERROR_OUTPUT);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(e.getProject() != null);
    }
}
