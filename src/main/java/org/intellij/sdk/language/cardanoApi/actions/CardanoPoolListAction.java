package org.intellij.sdk.language.cardanoApi.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import org.intellij.sdk.language.cardanoApi.CardanoScanFetcher;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CardanoPoolListAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            Messages.showErrorDialog("Project is not available.", "Error");
            return;
        }

        // Initialize the fetcher
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
                StringBuilder queryString = new StringBuilder();
                queryParams.forEach((key, value) -> queryString.append(key).append("=").append(value).append("&"));

                // Remove the trailing ampersand
                if (!queryString.isEmpty()) {
                    queryString.setLength(queryString.length() - 1);
                }

                // Fetch data using the constructed query string
                fetcher.fetchPoolList(queryString.toString());
            } else {
                Messages.showErrorDialog(project, "Please provide valid query parameters.", "Invalid Parameters");
            }
        }
    }

    private static class QueryParameterDialog extends DialogWrapper {
        private JTextField pageNoField;
        private JTextField limitField;
        private JCheckBox retiredPoolsCheckbox;
        private ComboBox<String> sortByComboBox;
        private ComboBox<String> orderComboBox;

        protected QueryParameterDialog() {
            super(true); // Use current window as parent
            setTitle("Enter Query Parameters");
            init();
        }

        @Override
        protected @Nullable JComponent createCenterPanel() {
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Input fields for parameters
            pageNoField = new JTextField();
            limitField = new JTextField();
            retiredPoolsCheckbox = new JCheckBox("Include retired pools");
            sortByComboBox = new ComboBox<>(new String[]{"name", "margin", "pledge", "random"});
            orderComboBox = new ComboBox<>(new String[]{"asc", "desc"});

            // Add components to the panel
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(new JLabel("Page Number (1-10000):"), gbc);
            gbc.gridx = 1;
            panel.add(pageNoField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add(new JLabel("Limit (1-50):"), gbc);
            gbc.gridx = 1;
            panel.add(limitField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            panel.add(new JLabel("Sort By:"), gbc);
            gbc.gridx = 1;
            panel.add(sortByComboBox, gbc);

            gbc.gridx = 0;
            gbc.gridy = 3;
            panel.add(new JLabel("Order:"), gbc);
            gbc.gridx = 1;
            panel.add(orderComboBox, gbc);

            gbc.gridx = 0;
            gbc.gridy = 4;
            panel.add(retiredPoolsCheckbox, gbc);

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
                }

                // Other parameters
                params.put("retiredPools", String.valueOf(retiredPoolsCheckbox.isSelected()));
                params.put("sortBy", (String) sortByComboBox.getSelectedItem());
                params.put("order", (String) orderComboBox.getSelectedItem());

            } catch (NumberFormatException ex) {
                showError("Invalid numeric input.");
                return new HashMap<>();
            }

            return params;
        }

        // Show error messages
        private void showError(String message) {
            Messages.showErrorDialog(message, "Error");
        }
    }
}
