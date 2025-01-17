package org.intellij.sdk.language.cardanoApi.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.intellij.sdk.language.cardanoApi.CardanoScanFetcher;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CardanoGovernanceColdKeyAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // Get the current project
        Project project = e.getProject();
        if (project == null) {
            Messages.showErrorDialog("No active project found.", "Error");
            return;
        }

        // Create a fetcher instance
        CardanoScanFetcher fetcher = new CardanoScanFetcher(project);

        // Ensure API key
        String apiKey = fetcher.ensureApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            Messages.showErrorDialog(project, "API key is missing. Please enter a valid API key.", "API Key Missing");
            return;
        }

        // Prompt the user for the Cold Hex input
        String coldHex = promptForColdHex(project);
        if (coldHex == null || coldHex.trim().isEmpty()) {
            Messages.showErrorDialog(project, "Please provide a valid Cold Hex string.", "Invalid Cold Hex");
            return;
        }

        // Fetch governance cold key details in the background
        new Task.Backgroundable(project, "Fetching Governance Cold Key Details") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setText("Fetching governance cold key details...");
                try {
                    fetcher.fetchGovernanceColdKeyInfo(coldHex);
                } catch (Exception ex) {
                    throw new RuntimeException("Error fetching governance cold key details: " + ex.getMessage(), ex);
                }
            }

            @Override
            public void onSuccess() {
                Messages.showInfoMessage("Governance cold key details fetched successfully.", "Success");
            }

            @Override
            public void onThrowable(@NotNull Throwable error) {
                Messages.showErrorDialog("Error fetching governance cold key details: " + error.getMessage(), "Error");
            }
        }.queue();
    }

    // Method to prompt the user for a Cold Hex string
    private String promptForColdHex(Project project) {
        JTextField coldHexField = new JTextField();
        Object[] dialogMessage = {"Enter Cold Hex:", coldHexField};

        int option = JOptionPane.showConfirmDialog(null, dialogMessage, "Cold Hex Input", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            return coldHexField.getText().trim(); // Return the entered Cold Hex string
        }
        return null; // Return null if canceled
    }
}
