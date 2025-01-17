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

public class CardanoAssetByAddressAction extends AnAction {

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

        // Show query parameter dialog
        AssetQueryParameterDialog dialog = new AssetQueryParameterDialog();
        if (dialog.showAndGet()) {
            Map<String, String> queryParams = dialog.getQueryParameters();

            if (queryParams != null && !queryParams.isEmpty()) {
                StringBuilder queryString = new StringBuilder();
                queryParams.forEach((key, value) -> queryString.append(key).append("=").append(value).append("&"));

                // Remove trailing ampersand
                if (!queryString.isEmpty()) {
                    queryString.setLength(queryString.length() - 1);
                }

                fetcher.fetchAssetByAddress(queryString.toString());
            } else {
                Messages.showErrorDialog(project, "Please provide valid query parameters.", "Invalid Parameters");
            }
        }
    }

    private static class AssetQueryParameterDialog extends DialogWrapper {
        private JTextField addressField;
        private JTextField pageNoField;
        private JTextField limitField;

        protected AssetQueryParameterDialog() {
            super(true); // Use current window as parent
            setTitle("Enter Asset Query Parameters");
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

            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(new JLabel("Address (Hex, Max 200 chars):"), gbc);

            gbc.gridx = 1;
            panel.add(addressField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add(new JLabel("Page Number (1-10000):"), gbc);

            gbc.gridx = 1;
            panel.add(pageNoField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            panel.add(new JLabel("Limit (1-50, Default: 20):"), gbc);

            gbc.gridx = 1;
            panel.add(limitField, gbc);

            return panel;
        }

        public Map<String, String> getQueryParameters() {
            Map<String, String> params = new HashMap<>();

            String address = addressField.getText().trim();
            if (isValidAddress(address)) {
                params.put("address", address);
            } else {
                Messages.showErrorDialog("Address must be a valid hex string with a maximum length of 200 characters.", "Invalid Address");
                return null;
            }

            String pageNo = pageNoField.getText().trim();
            if (!pageNo.isEmpty()) {
                try {
                    int page = Integer.parseInt(pageNo);
                    if (page >= 1 && page <= 10000) {
                        params.put("pageNo", pageNo);
                    } else {
                        Messages.showErrorDialog("Page number must be between 1 and 10000.", "Invalid Page Number");
                        return null;
                    }
                } catch (NumberFormatException ex) {
                    Messages.showErrorDialog("Page number must be a valid integer.", "Invalid Input");
                    return null;
                }
            } else {
                Messages.showErrorDialog("Page number is required.", "Missing Input");
                return null;
            }

            String limit = limitField.getText().trim();
            if (!limit.isEmpty()) {
                try {
                    int limitValue = Integer.parseInt(limit);
                    if (limitValue >= 1 && limitValue <= 50) {
                        params.put("limit", limit);
                    } else {
                        Messages.showErrorDialog("Limit must be between 1 and 50.", "Invalid Limit");
                        return null;
                    }
                } catch (NumberFormatException ex) {
                    Messages.showErrorDialog("Limit must be a valid integer.", "Invalid Input");
                    return null;
                }
            } else {
                // Use default limit if not provided
                params.put("limit", "20");
            }

            return params;
        }

        private boolean isValidAddress(String address) {
            return address.length() <= 200 && address.matches("^[0-9A-Fa-f]+$");
        }
    }
}
