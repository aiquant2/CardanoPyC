package org.intellij.sdk.language.cardanoApi.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import org.intellij.sdk.language.cardanoApi.CardanoScanFetcher;

import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CardanoExpiringPoolListAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            Messages.showErrorDialog("Project is not available.", "Error");
            return;
        }

        CardanoScanFetcher fetcher = new CardanoScanFetcher(project);

        // Ensure API key
        String apiKey = fetcher.ensureApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            Messages.showErrorDialog(project, "API key is missing. Please enter a valid API key.", "API Key Missing");
            return;
        }

        // Show the query parameter dialog
        QueryParameterDialog dialog = new QueryParameterDialog();
        if (dialog.showAndGet()) {
            Map<String, String> queryParams = dialog.getQueryParameters();

            if (!queryParams.isEmpty()) {
                // Build query string dynamically
                StringBuilder queryString = new StringBuilder();
                queryParams.forEach((key, value) -> queryString.append(key).append("=").append(value).append("&"));

                // Remove the trailing '&'
                if (!queryString.isEmpty()) {
                    queryString.setLength(queryString.length() - 1);
                }

                // Fetch data using the constructed query string
                fetcher.fetchExpiringPoolList(queryString.toString());
            } else {
                Messages.showErrorDialog(project, "Please provide valid query parameters.", "Invalid Parameters");
            }
        }
    }

    private static class QueryParameterDialog extends DialogWrapper {
        private JTextField pageNoField;
        private JTextField limitField;

        protected QueryParameterDialog() {
            super(true); // Use current window as parent
            setTitle("Enter Query Parameters");
            init();
        }

        @Override
        protected @Nullable JComponent createCenterPanel() {
            JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));

            // Input fields for page number and limit
            pageNoField = new JTextField();
            limitField = new JTextField();

            panel.add(new JLabel("Page Number (1-10000):"));
            panel.add(pageNoField);
            panel.add(new JLabel("Limit (1-50, Default: 20):"));
            panel.add(limitField);

            return panel;
        }

        public Map<String, String> getQueryParameters() {
            Map<String, String> params = new HashMap<>();

            try {
                // Page number validation
                String pageNo = pageNoField.getText().trim();
                if (!pageNo.isEmpty()) {
                    int pageNumber = Integer.parseInt(pageNo);
                    if (pageNumber >= 1 && pageNumber <= 10000) {
                        params.put("pageNo", pageNo);
                    } else {
                        showError("Page number must be between 1 and 10000.");
                        return new HashMap<>();
                    }
                } else {
                    showError("Page number is required.");
                    return new HashMap<>();
                }

                // Limit validation
                String limit = limitField.getText().trim();
                if (!limit.isEmpty()) {
                    int limitValue = Integer.parseInt(limit);
                    if (limitValue >= 1 && limitValue <= 50) {
                        params.put("limit", limit);
                    } else {
                        showError("Limit must be between 1 and 50.");
                        return new HashMap<>();
                    }
                } else {
                    params.put("limit", "20"); // Default limit if not provided
                }
            } catch (NumberFormatException e) {
                showError("Invalid numeric input.");
                return new HashMap<>();
            }

            return params;
        }

        private void showError(String message) {
            Messages.showErrorDialog(message, "Error");
        }
    }
}
