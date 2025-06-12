
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewSpecificBalanceDialog extends DialogWrapper {
    private static final String NET_TYPE = WalletApiKeyState.getInstance().getNetwork();
    private static final String STATE = switch (NET_TYPE) {
        case "mainnet" -> "mainnet";
        case "preprod" -> "preprod";
        default -> "preview";
    };
    private static final String BLOCKFROST_API_URL = "https://cardano-" + STATE + ".blockfrost.io/api/v0";
    private static final String API_KEY = WalletApiKeyState.getInstance().getApiKey();
    private  final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
    }

    private  JLabel balanceLabel;
    private  JTextArea transactionsArea;

    public ViewSpecificBalanceDialog() {
        super(true);
        setTitle("Wallet Balance and Transactions");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(350, 400));

        balanceLabel = new JLabel("<html><h2>Your Balance:</h2><br><b>Fetching balance...</b></html>", JLabel.CENTER);
        panel.add(balanceLabel, BorderLayout.NORTH);

        transactionsArea = new JTextArea(12, 40);
        transactionsArea.setEditable(false);
        transactionsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        transactionsArea.setText("Fetching transaction history...");

        JBScrollPane scrollPane = new JBScrollPane(transactionsArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        fetchWalletDataAsync();

        return panel;
    }

    private void fetchWalletDataAsync() {
        executorService.submit(() -> {
            try {
                String address = SecureStorageUtil.retrieveCredential("wallet_baseAddress");
                if (address == null || address.isEmpty()) {
                    throw new Exception("Wallet address not found");
                }

                String balance = fetchWalletBalance(address);
                List<String> transactions = fetchTransactionHistory(address);

                SwingUtilities.invokeLater(() -> updateUI(balance, transactions));
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> showError(e.getMessage()));
            }
        });
    }

    private void updateUI(String balance, List<String> transactions) {
        balanceLabel.setText("<html><h2>Your Balance:</h2><br><b>" + balance + "</b></html>");

        StringBuilder transactionsText = new StringBuilder();
        if (!transactions.isEmpty()) {
            transactionsText.append("Last Transactions:\n--------------------\n");
            transactions.forEach(transactionsText::append);
        } else {
            transactionsText.append("No transactions found");
        }
        transactionsArea.setText(transactionsText.toString());
    }

    private void showError(String message) {
        balanceLabel.setText("<html><h2>Error:</h2><br><b>" + message + "</b></html>");
        transactionsArea.setText("Failed to fetch data: " + message);
    }

    @Override
    protected void doOKAction() {
        close(OK_EXIT_CODE);
    }

    @Override
    public void dispose() {
        executorService.shutdown();
        super.dispose();
    }

    private String fetchWalletBalance(String address) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BLOCKFROST_API_URL + "/addresses/" + address))
                .header("project_id", API_KEY)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 404) {
            return "0.000000 ADA";
        }
        if (response.statusCode() != 200) {
            throw new Exception("Failed to fetch balance: " + response.statusCode());
        }

        return extractBalanceFromResponse(response.body());
    }

    private List<String> fetchTransactionHistory(String address) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BLOCKFROST_API_URL + "/addresses/" + address + "/transactions"))
                .header("project_id", API_KEY)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 404) {
            return new ArrayList<>();
        }
        if (response.statusCode() != 200) {
            throw new Exception("Failed to fetch transactions: " + response.statusCode());
        }

        return extractTransactionsFromResponse(response.body());
    }

    private String extractBalanceFromResponse(String response) throws JsonSyntaxException {
        try {
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();

            if (!jsonResponse.has("amount")) {
                return "0.000000 ADA";
            }

            JsonArray amounts = jsonResponse.getAsJsonArray("amount");
            if (amounts.isEmpty()) {
                return "0.000000 ADA";
            }

            for (JsonElement element : amounts) {
                JsonObject amountObj = element.getAsJsonObject();
                if ("lovelace".equals(amountObj.get("unit").getAsString())) {
                    long lovelace = amountObj.get("quantity").getAsLong();
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
            JsonArray transactionsArray = JsonParser.parseString(response).getAsJsonArray();
            if (transactionsArray.isEmpty()) {
                return transactions;
            }

            int totalTransactions = transactionsArray.size();
            int startIndex = Math.max(totalTransactions - 5, 0);

            for (int i = totalTransactions - 1; i >= startIndex; i--) {
                JsonObject tx = transactionsArray.get(i).getAsJsonObject();
                String formattedTx = String.format(
                        "Tx Hash: %s\nTx Index: %d\nBlock Height: %d\nBlock Time: %s\n--------------------\n",
                        tx.get("tx_hash").getAsString(),
                        tx.get("tx_index").getAsInt(),
                        tx.get("block_height").getAsInt(),
                        formatBlockTime(tx.get("block_time").getAsLong())
                );
                transactions.add(formattedTx);
            }
        } catch (Exception e) {
            transactions.add("Error parsing transactions: " + e.getMessage());
        }
        return transactions;
    }

    private String formatBlockTime(long timestamp) {
        return DATE_FORMAT.format(new Date(timestamp * 1000));
    }
}