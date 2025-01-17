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

public class CardanoBlockDetailsAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            Messages.showErrorDialog("Project is not available.", "Error");
            return;
        }

        CardanoScanFetcher fetcher = new CardanoScanFetcher(project);

        // Check if API key exists
        String apiKey = fetcher.ensureApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            Messages.showErrorDialog(project, "API key is missing. Please enter a valid API key.", "API Key Missing");
            return;
        }

        // Show the query parameter dialog
        QueryParameterDialog dialog = new QueryParameterDialog();
        if (dialog.showAndGet()) {
            Map<String, String> queryParams = dialog.getQueryParameters();

            if (queryParams != null && !queryParams.isEmpty()) {
                StringBuilder queryString = new StringBuilder();
                queryParams.forEach((key, value) -> queryString.append(key).append("=").append(value).append("&"));

                // Remove the trailing ampersand
                if (!queryString.isEmpty()) {
                    queryString.setLength(queryString.length() - 1);
                }

                fetcher.fetchBlockDetails(queryString.toString());
            } else {
                Messages.showErrorDialog(project, "Please provide valid query parameters.", "Invalid Parameters");
            }
        }
    }

    private static class QueryParameterDialog extends DialogWrapper {
        private ComboBox<String> parameterComboBox;
        private JTextField inputField;

        protected QueryParameterDialog() {
            super(true); // Use current window as parent
            setTitle("Enter Query Parameter");
            init();
        }

        @Override
        protected @Nullable JComponent createCenterPanel() {
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Dropdown for parameters
            parameterComboBox = new ComboBox<>(new String[]{"Block Hash", "Block Height", "Absolute Slot"});
            inputField = new JTextField();

            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(new JLabel("Select Parameter:"), gbc);

            gbc.gridx = 1;
            panel.add(parameterComboBox, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add(new JLabel("Enter Value:"), gbc);

            gbc.gridx = 1;
            panel.add(inputField, gbc);

            return panel;
        }

        public Map<String, String> getQueryParameters() {
            String selectedParameter = (String) parameterComboBox.getSelectedItem();
            String inputValue = inputField.getText().trim();

            if (selectedParameter != null && isValidInput(selectedParameter, inputValue)) {
                Map<String, String> params = new HashMap<>();
                params.put(mapParameterKey(selectedParameter), inputValue);
                return params;
            } else {
                Messages.showErrorDialog("Invalid value for " + selectedParameter + ".", "Invalid Input");
            }
            return null;
        }

        private boolean isValidInput(String parameter, String value) {
            if ("Block Hash".equals(parameter)) {
                // Validate Block Hash as a hexadecimal string
                return value.length() == 64 && value.matches("[a-fA-F0-9]+");
            } else {
                try {
                    double doubleValue = Double.parseDouble(value);
                    return doubleValue >= 0 && doubleValue == Math.floor(doubleValue);
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }

        private String mapParameterKey(String parameter) {
            switch (parameter) {
                case "Block Hash":
                    return "blockHash";
                case "Block Height":
                    return "blockHeight";
                case "Absolute Slot":
                    return "absoluteSlot";
                default:
                    return "";
            }
        }
    }
}
