
package org.intellij.sdk.language.wallet;

import com.google.gson.*;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewBalanceDialog extends DialogWrapper {
    private static final int MAX_TRANSACTIONS_TO_SHOW = 5;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
    }

    private  JLabel balanceLabel;
    private  JTextArea transactionsArea;
    private final String walletAddress;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();



    public ViewBalanceDialog() {
        super(true);
        setTitle("Wallet Balance and Transactions");

        walletAddress = promptForWalletAddress();
        if (walletAddress == null) {
            close(CANCEL_EXIT_CODE);
            return;
        }

        // Basic address validation
        if (!walletAddress.startsWith("addr") || walletAddress.length() < 10) {
            JOptionPane.showMessageDialog(
                    null,
                    "Invalid Cardano address format. Please enter a valid address.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            close(CANCEL_EXIT_CODE);
            return;
        }

        init();
    }
    private String promptForWalletAddress() {
        String address = JOptionPane.showInputDialog(
                null,
                "Enter your Cardano wallet address:",
                "Wallet Address Input",
                JOptionPane.PLAIN_MESSAGE
        );

        if (address == null || address.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    null,
                    "No address provided. Dialog will now close.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return null;
        }

        return address.trim();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(400, 450));

        // Balance display
        balanceLabel = new JLabel("<html><h2>Your Balance:</h2><br><b>Fetching balance...</b></html>", JLabel.CENTER);
        panel.add(balanceLabel, BorderLayout.NORTH);

        // Transactions display
        transactionsArea = new JTextArea(15, 50);
        transactionsArea.setEditable(false);
        transactionsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        transactionsArea.setText("Fetching transaction history...");
        panel.add(new JBScrollPane(transactionsArea), BorderLayout.CENTER);

        fetchWalletDataAsync();
        return panel;
    }

    @Override
    public void dispose() {
        executorService.shutdown();
        super.dispose();
    }

    private void fetchWalletDataAsync() {
        executorService.submit(() -> {
            try {
                String apiKey = getApiKey();
                validateApiKey(apiKey);

                String balance = fetchWalletBalance(walletAddress);
                List<String> transactions = fetchTransactionHistory(walletAddress);

                SwingUtilities.invokeLater(() -> updateUI(balance, transactions));
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> showError(e.getMessage()));
            }
        });
    }

    private void validateApiKey(String apiKey) throws Exception {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new Exception("API Key not set. Please configure your Blockfrost API key.");
        }
    }

    private void updateUI(String balance, List<String> transactions) {
        balanceLabel.setText("<html><h2>Your Balance:</h2><br><b>" + balance + "</b></html>");

        StringBuilder sb = new StringBuilder();
        if (transactions.isEmpty()) {
            sb.append("No transactions found");
        } else {
            sb.append("Last Transactions:\n").append("-".repeat(20)).append("\n");
            transactions.forEach(sb::append);
        }
        transactionsArea.setText(sb.toString());
    }

    private void showError(String message) {
        balanceLabel.setText("<html><h2>Error:</h2><br><b>" + message + "</b></html>");
        transactionsArea.setText("Failed to fetch data:\n" + message);
    }


    private String fetchWalletBalance(String address) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getBlockfrostUrl() + "/addresses/" + address))
                .header("project_id", getApiKey())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 404) {
            // Address exists but has no transactions/balance
            return "0.000000 ADA";
        }
        if (response.statusCode() == 400) {
            throw new Exception("Invalid wallet address format");
        }
        if (response.statusCode() != 200) {
            throw new Exception("API request failed with status: " + response.statusCode());
        }

        return extractBalanceFromResponse(response.body());
    }

private List<String> fetchTransactionHistory(String address) throws Exception {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(getBlockfrostUrl() + "/addresses/" + address + "/transactions"))
            .header("project_id", getApiKey())
            .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() == 404) {
        // No transactions found is a normal case
        return Collections.emptyList();
    }
    if (response.statusCode() == 400) {
        throw new Exception("Invalid wallet address format");
    }
    if (response.statusCode() != 200) {
        throw new Exception("Failed to fetch transactions: " + response.statusCode());
    }

    return extractTransactionsFromResponse(response.body());
}
    private String extractBalanceFromResponse(String response) throws JsonSyntaxException {
        try {
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            if (!json.has("amount")) {
                return "0.000000 ADA";
            }

            JsonArray amounts = json.getAsJsonArray("amount");
            if (amounts.isEmpty()) {
                return "0.000000 ADA";
            }
            for (JsonElement element : amounts) {
                JsonObject amount = element.getAsJsonObject();
                if ("lovelace".equals(amount.get("unit").getAsString())) {
                    long lovelace = amount.get("quantity").getAsLong();
                    return String.format("%.6f ADA", lovelace / 1_000_000.0);
                }
            }
            return "0.000000 ADA";
        } catch (Exception e) {
            throw new JsonSyntaxException("Failed to parse balance response", e);
        }
    }

    private List<String> extractTransactionsFromResponse(String response) {
        List<String> transactions = new ArrayList<>();
        try {
            JsonArray txArray = JsonParser.parseString(response).getAsJsonArray();
            int totalTxs = txArray.size();
            int limit = Math.min(MAX_TRANSACTIONS_TO_SHOW, totalTxs);

            for (int i = totalTxs - 1; i >= totalTxs - limit; i--) {
                JsonObject tx = txArray.get(i).getAsJsonObject();
                transactions.add(String.format(
                        "Tx Hash: %s\nTx Index: %d\nBlock Height: %d\nBlock Time: %s\n%s\n",
                        tx.get("tx_hash").getAsString(),
                        tx.get("tx_index").getAsInt(),
                        tx.get("block_height").getAsInt(),
                        formatBlockTime(tx.get("block_time").getAsLong()),
                        "-".repeat(20)
                ));
            }
        } catch (Exception e) {
            transactions.add("Error parsing transactions: " + e.getMessage());
        }
        return transactions;
    }

    private String formatBlockTime(long timestamp) {
        return DATE_FORMAT.format(new Date(timestamp * 1000));
    }

    private String getNetworkType() {
        String netType = WalletApiKeyState.getInstance().getNetwork();
        return switch (netType == null ? "" : netType) {
            case "mainnet" -> "mainnet";
            case "preprod" -> "preprod";
            default -> "preview";
        };
    }

    private String getBlockfrostUrl() {
        return "https://cardano-" + getNetworkType() + ".blockfrost.io/api/v0";
    }

    private String getApiKey() {
        return WalletApiKeyState.getInstance().getApiKey();
    }
}