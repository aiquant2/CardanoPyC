package org.intellij.sdk.language.cardanoApi.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.intellij.sdk.language.cardanoApi.CardanoScanFetcher;
import org.jetbrains.annotations.NotNull;

public class CardanoRewardAccountInfoAction extends AnAction {

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

        // Prompt the user for a reward address
        String rewardAddress = promptForRewardAddress(project);
        if (rewardAddress == null || rewardAddress.trim().isEmpty()) {
            Messages.showErrorDialog("Please provide a valid reward address.", "Invalid Reward Address");
            return;
        }

        // Fetch reward account info in the background
        new Task.Backgroundable(project, "Fetching reward account info") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setText("Fetching details for reward address...");
                try {
                    fetcher.fetchRewardAccountInfo(rewardAddress);
                } catch (Exception ex) {
                    throw new RuntimeException("Error fetching reward account info: " + ex.getMessage(), ex);
                }
            }

            @Override
            public void onSuccess() {
                Messages.showInfoMessage("Reward account info fetched successfully.", "Success");
            }

            @Override
            public void onThrowable(@NotNull Throwable error) {
                Messages.showErrorDialog("Error fetching reward account info: " + error.getMessage(), "Error");
            }
        }.queue();
    }

    private String promptForRewardAddress(Project project) {
        return Messages.showInputDialog(project, "Enter the Cardano Reward Address:", "Reward Address Input", Messages.getQuestionIcon());
    }
}
