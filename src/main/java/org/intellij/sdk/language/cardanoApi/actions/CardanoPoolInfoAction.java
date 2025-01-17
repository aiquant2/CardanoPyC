package org.intellij.sdk.language.cardanoApi.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.intellij.sdk.language.cardanoApi.CardanoScanFetcher;
import org.jetbrains.annotations.NotNull;

public class CardanoPoolInfoAction extends AnAction {

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

        // Prompt the user for a pool ID
        String poolId = promptForPoolId(project);
        if (poolId == null || poolId.trim().isEmpty()) {
            Messages.showErrorDialog("Please provide a valid pool ID.", "Invalid Pool ID");
            return;
        }

        // Fetch pool info in the background
        new Task.Backgroundable(project, "Fetching pool info") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setText("Fetching information for pool ID...");
                try {
                    fetcher.fetchPoolInfo(poolId);
                } catch (Exception ex) {
                    throw new RuntimeException("Error fetching pool info: " + ex.getMessage(), ex);
                }
            }

            @Override
            public void onSuccess() {
                Messages.showInfoMessage("Pool info fetched successfully.", "Success");
            }

            @Override
            public void onThrowable(@NotNull Throwable error) {
                Messages.showErrorDialog("Error fetching pool info: " + error.getMessage(), "Error");
            }
        }.queue();
    }

    private String promptForPoolId(Project project) {
        return Messages.showInputDialog(project, "Enter the Cardano Pool ID:", "Pool ID Input", Messages.getQuestionIcon());
    }
}
