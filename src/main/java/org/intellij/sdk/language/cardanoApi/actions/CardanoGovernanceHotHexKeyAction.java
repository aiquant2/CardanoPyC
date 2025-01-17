package org.intellij.sdk.language.cardanoApi.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.intellij.sdk.language.cardanoApi.CardanoScanFetcher;
import org.jetbrains.annotations.NotNull;

public class CardanoGovernanceHotHexKeyAction extends AnAction {

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
            Messages.showErrorDialog("API key is missing. Please enter a valid API key.", "API Key Missing");
            return;
        }

        // Prompt the user for a Hot Hex string
        String hotHex = promptForHotHex(project);
        if (hotHex == null || hotHex.trim().isEmpty()) {
            Messages.showErrorDialog("Please provide a valid Hot Hex string.", "Invalid Hot Hex");
            return;
        }

        // Fetch Hot Hex key details in the background
        new Task.Backgroundable(project, "Fetching Hot Hex Key Information") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setText("Fetching details for Hot Hex key...");
                try {
                    fetcher.fetchGovernanceHotKeyInfo(hotHex);
                } catch (Exception ex) {
                    throw new RuntimeException("Error fetching Hot Hex key information: " + ex.getMessage(), ex);
                }
            }

            @Override
            public void onSuccess() {
                Messages.showInfoMessage("Hot Hex key details fetched successfully.", "Success");
            }

            @Override
            public void onThrowable(@NotNull Throwable error) {
                Messages.showErrorDialog("Error fetching Hot Hex key information: " + error.getMessage(), "Error");
            }
        }.queue();
    }

    private String promptForHotHex(Project project) {
        return Messages.showInputDialog(project, "Enter Hot Hex:", "Hot Hex Input", Messages.getQuestionIcon());
    }
}
