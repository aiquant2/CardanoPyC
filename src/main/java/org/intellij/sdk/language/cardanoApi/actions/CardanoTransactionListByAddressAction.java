package org.intellij.sdk.language.cardanoApi.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.ComboBox;
import org.intellij.sdk.language.cardanoApi.CardanoScanFetcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CardanoTransactionListByAddressAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            Messages.showErrorDialog("No active project found.", "Error");
            return;
        }

        CardanoScanFetcher fetcher = new CardanoScanFetcher(project);

        String apiKey = fetcher.ensureApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            Messages.showErrorDialog("API key is missing. Please enter a valid API key.", "API Key Missing");
            return;
        }

        QueryParameterDialog dialog = new QueryParameterDialog();
        if (dialog.showAndGet()) {
            Map<String, String> queryParams = dialog.getQueryParameters();
            if (!queryParams.isEmpty()) {
                StringBuilder queryString = new StringBuilder();
                queryParams.forEach((key, value) -> queryString.append(key).append("=").append(value).append("&"));

                if (!queryString.isEmpty()) {
                    queryString.setLength(queryString.length() - 1);
                }

                new Task.Backgroundable(project, "Fetching transaction list by address") {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        indicator.setText("Fetching transaction list...");
                        try {
                            fetcher.fetchTransactionListByAddress(queryString.toString());
                        } catch (Exception ex) {
                            throw new RuntimeException("Error fetching transaction list: " + ex.getMessage(), ex);
                        }
                    }

                    @Override
                    public void onSuccess() {
                        Messages.showInfoMessage("Transaction list fetched successfully.", "Success");
                    }

                    @Override
                    public void onThrowable(@NotNull Throwable error) {
                        Messages.showErrorDialog("Error fetching transaction list: " + error.getMessage(), "Error");
                    }
                }.queue();
            } else {
                Messages.showErrorDialog("Please provide valid query parameters.", "Invalid Parameters");
            }
        }
    }

    private static class QueryParameterDialog extends DialogWrapper {
        private JTextField addressField;
        private JTextField pageNoField;
        private JTextField limitField;
        private ComboBox<String> orderComboBox;

        protected QueryParameterDialog() {
            super(true);
            setTitle("Enter Query Parameters");
            init();
        }

        @Override
        protected @Nullable JComponent createCenterPanel() {
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            addressField = new JTextField();
            pageNoField = new JTextField();
            limitField = new JTextField();
            orderComboBox = new ComboBox<>(new String[]{"asc", "desc"});

            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(new JLabel("Address (Hex, Max 200 chars):"), gbc);
            gbc.gridx = 1;
            panel.add(addressField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add(new JLabel("Page Number (>= 1):"), gbc);
            gbc.gridx = 1;
            panel.add(pageNoField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            panel.add(new JLabel("Limit (1-50, Default: 20):"), gbc);
            gbc.gridx = 1;
            panel.add(limitField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 3;
            panel.add(new JLabel("Order (asc/desc, Default: asc):"), gbc);
            gbc.gridx = 1;
            panel.add(orderComboBox, gbc);

            return panel;
        }

        public Map<String, String> getQueryParameters() {
            Map<String, String> params = new HashMap<>();
            try {
                String address = addressField.getText().trim();
                if (address.length() <= 200) {
                    params.put("address", address);
                } else {
                    showError("Address must be a valid hex string with a maximum length of 200 characters.");
                    return new HashMap<>();
                }

                String pageNo = pageNoField.getText().trim();
                if (!pageNo.isEmpty()) {
                    int pageNumber = Integer.parseInt(pageNo);
                    if (pageNumber >= 1) {
                        params.put("pageNo", pageNo);
                    } else {
                        showError("Page number must be greater than or equal to 1.");
                        return new HashMap<>();
                    }
                } else {
                    showError("Page number is required.");
                    return new HashMap<>();
                }

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
                    params.put("limit", "20");
                }

                String order = (String) orderComboBox.getSelectedItem();
                if (order != null && (order.equals("asc") || order.equals("desc"))) {
                    params.put("order", order);
                } else {
                    params.put("order", "asc");
                }
            } catch (NumberFormatException ex) {
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
