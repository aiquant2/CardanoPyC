
package org.intellij.sdk.language.deployment;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CardanoCliAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        // First check dependencies
        if (!isCardanoInstalled("cardano-node") || !isCardanoInstalled("cardano-cli")) {
            showErrorNotification("⚠️ Cardano is not installed. Please install `cardano-node` and `cardano-cli` before generating addresses.", project);
            return;
        }

        // Proceed with build address UI
        showBuildAddressDialog(project);
    }

    private void showBuildAddressDialog(Project project) {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JTextField plutusFilePathField = new JTextField();
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
            showErrorNotification("Plutus file path and network selection are required.", project);
            return;
        }

        new Thread(() -> buildPlutusAddress(plutusFilePath, networkFlag, project)).start();
    }

    private void buildPlutusAddress(String plutusFilePath, String networkFlag, Project project) {
        try {
            String plutusFile = plutusFilePath.endsWith(".plutus") ? plutusFilePath : plutusFilePath + ".plutus";
            String addrFile = plutusFile.replace(".plutus", ".addr");

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

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                Path addrPath = Paths.get(addrFile);
                if (Files.exists(addrPath)) {
                    String scriptAddress = Files.readString(addrPath).trim();
                    showSuccessNotificationWithFile(
                            "Plutus Address Generated Successfully!",
                            "Your script address:",
                            scriptAddress,
                            addrFile,
                            project
                    );
                } else {
                    showErrorNotification("Address file not found: " + addrFile, project);
                }
            } else {
                showErrorNotification("cardano-cli failed with exit code: " + exitCode, project);
            }
        } catch (Exception ex) {
            showErrorNotification("Error running cardano-cli: " + ex.getMessage(), project);
        }
    }

    String getNetworkFlag(String networkType) {
        if (networkType == null) return null;
        switch (networkType) {
            case "preview": return "--testnet-magic 2";
            case "preprod": return "--testnet-magic 1";
            case "mainnet": return "--mainnet";
            default: return null;
        }
    }

    boolean isCardanoInstalled(String command) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command, "--version");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String version = reader.readLine();
                    return version != null && !version.isEmpty();
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    private void showSuccessNotificationWithFile(String title, String mainContent, String address, String filePath, Project project) {
        String message = "<html>" +
                "<body>" +
                "<div style='padding:8px;'>" +
                "<div style='font-weight:bold;color:#2E7D32;'>✅ " + title + "</div>" +
                "<div>" + mainContent + "</div>" +
                "<div style='margin:6px 0; font-family:monospace;'>" + address + "</div>" +
                "<div>📁 Saved to: " + filePath + "</div>" +
                "</div>" +
                "</body>" +
                "</html>";

        Notification notification = com.intellij.notification.NotificationGroupManager.getInstance()
                .getNotificationGroup("Cardano CLI Notifications")
                .createNotification(message, NotificationType.INFORMATION);
        Notifications.Bus.notify(notification, project);
    }

    void showErrorNotification(String message, Project project) {
        String htmlMessage = message;

        Notification notification = com.intellij.notification.NotificationGroupManager.getInstance()
                .getNotificationGroup("Cardano CLI Notifications")
                .createNotification(htmlMessage, NotificationType.ERROR);
        Notifications.Bus.notify(notification, project);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(e.getProject() != null);
    }
}
