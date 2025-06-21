
package org.intellij.sdk.language.deployment;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.RegisterToolWindowTask;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RunCardanoNodeAction extends AnAction implements Disposable {

    private OSProcessHandler processHandler;
    private BufferedWriter processInput;
    private JLabel statusLabel;
    private JButton stopButton;

    private static final Logger LOGGER = Logger.getLogger(RunCardanoNodeAction.class.getName());

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        JPanel panel = new JPanel(new GridLayout(0, 1));
        JBTextField topologyPathField = new JBTextField();
        JBTextField databasePathField = new JBTextField();
        JBTextField socketPathField = new JBTextField();
        JBTextField portField = new JBTextField("3001");
        JBTextField configPathField = new JBTextField();

        panel.add(new JLabel("Topology File Path:"));
        panel.add(topologyPathField);
        panel.add(new JLabel("Database Path:"));
        panel.add(databasePathField);
        panel.add(new JLabel("Socket Path:"));
        panel.add(socketPathField);
        panel.add(new JLabel("Port:"));
        panel.add(portField);
        panel.add(new JLabel("Config File Path:"));
        panel.add(configPathField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Cardano Node Configuration",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String topologyPath = topologyPathField.getText().trim();
        String databasePath = databasePathField.getText().trim();
        String socketPath = socketPathField.getText().trim();
        String port = portField.getText().trim();
        String configPath = configPathField.getText().trim();

        if (topologyPath.isEmpty() || databasePath.isEmpty() || socketPath.isEmpty()
                || port.isEmpty() || configPath.isEmpty()) {
            Messages.showErrorDialog("All fields are required.", "Error");
            return;
        }

        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Cardano Node Terminal");

        if (toolWindow == null) {
            toolWindow = ToolWindowManager.getInstance(project).registerToolWindow(
                    new RegisterToolWindowTask(
                            "Cardano Node Terminal",
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

        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            GeneralCommandLine commandLine = new GeneralCommandLine("cardano-node", "run",
                    "--topology", topologyPath,
                    "--database-path", databasePath,
                    "--socket-path", socketPath,
                    "--port", port,
                    "--config", configPath);
            commandLine.setWorkDirectory(project.getBasePath());

            try {
                processHandler = new OSProcessHandler(commandLine);
                processInput = new BufferedWriter(new OutputStreamWriter(processHandler.getProcessInput()));

                ApplicationManager.getApplication().invokeLater(() -> {
                    ConsoleView consoleView = new ConsoleViewImpl(project, true);
                    JBPanel<?> terminalPanel = createTerminalPanel(consoleView);

                    Content content = ContentFactory.getInstance().createContent(
                            terminalPanel,
                            "Cardano Node Terminal",
                            false
                    );
                    finalToolWindow.getContentManager().addContent(content);

                    consoleView.clear();
                    consoleView.attachToProcess(processHandler);
                    statusLabel.setText("Status: Running");
                    stopButton.setEnabled(true);
                });

                processHandler.startNotify();

                processHandler.addProcessListener(new com.intellij.execution.process.ProcessAdapter() {
                    @Override
                    public void processTerminated(@NotNull com.intellij.execution.process.ProcessEvent event) {
                        ApplicationManager.getApplication().invokeLater(() -> {
                            statusLabel.setText("Status: Stopped");
                            stopButton.setEnabled(false);
                        });
                    }
                });

            } catch (ExecutionException ex) {
                ApplicationManager.getApplication().invokeLater(() ->
                        Messages.showErrorDialog("Error starting Cardano Node: " + ex.getMessage(), "Error"));
            }
        });
    }

    public JBPanel<?> createTerminalPanel(ConsoleView consoleView) {
        JBPanel<?> terminalPanel = new JBPanel<>(new BorderLayout());
        terminalPanel.add(consoleView.getComponent(), BorderLayout.CENTER);

        // Bottom control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Status: Not Running");
        stopButton = new JButton("Stop Node");

        stopButton.setEnabled(false);
        stopButton.addActionListener(e -> stopNode());

        controlPanel.add(statusLabel);
        controlPanel.add(stopButton);

        terminalPanel.add(controlPanel, BorderLayout.SOUTH);
        return terminalPanel;
    }

    private void stopNode() {
        if (processHandler != null && !processHandler.isProcessTerminated()) {
            processHandler.destroyProcess();
            statusLabel.setText("Status: Stopping...");
            stopButton.setEnabled(false);
        }
    }

    @Override
    public void dispose() {
        try {
            if (processInput != null) {
                processInput.close();
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error closing process input", ex);
        }
        stopNode();
    }
}