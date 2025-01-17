package org.intellij.sdk.language.cardanoApi.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.intellij.sdk.language.cardanoApi.CardanoScanFetcher;
import org.jetbrains.annotations.NotNull;

public class CardanoGovernanceDRepAction extends AnAction {

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

        // Prompt the user for a dRep ID
        String dRepId = promptForDRepId(project);
        if (dRepId == null || dRepId.trim().isEmpty()) {
            Messages.showErrorDialog("Please provide a valid dRep ID.", "Invalid dRep ID");
            return;
        }

        // Fetch dRep details in the background
        new Task.Backgroundable(project, "Fetching dRep Information") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setText("Fetching information for dRep ID...");
                try {
                    fetcher.fetchGovernanceDRepInfo(dRepId);
                } catch (Exception ex) {
                    throw new RuntimeException("Error fetching dRep information: " + ex.getMessage(), ex);
                }
            }

            @Override
            public void onSuccess() {
                Messages.showInfoMessage("DRep information fetched successfully.", "Success");
            }

            @Override
            public void onThrowable(@NotNull Throwable error) {
                Messages.showErrorDialog("Error fetching dRep information: " + error.getMessage(), "Error");
            }
        }.queue();
    }

    private String promptForDRepId(Project project) {
        return Messages.showInputDialog(project, "Enter the dRep ID:", "DRep ID Input", Messages.getQuestionIcon());
    }
}
