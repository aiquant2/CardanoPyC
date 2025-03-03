package org.intellij.sdk.language.wallet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

public class ViewBalanceDialog extends DialogWrapper {
    private static final String netType=WalletApiKeyState.getInstance().getNetwork();
    static final String state=switch (netType) {
        case "mainnet" -> "mainnet";
        case "preprod" -> "preprod";
        default -> "preview";
    };
    private static final String BLOCKFROST_API_URL = "https://cardano-"+state+".blockfrost.io/api";
    private static final String API_KEY = WalletApiKeyState.getInstance().getApiKey();


    private JLabel balanceLabel;
    private JTextArea transactionsArea;

    public ViewBalanceDialog() {
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

        new Thread(() -> {
            try {
                String address = SecureStorageUtil.retrieveCredential("wallet_baseAddress");
                String balance = fetchWalletBalance(address);
                List<String> transactions = fetchTransactionHistory(address);

                SwingUtilities.invokeLater(() -> {
                    balanceLabel.setText("<html><h2>Your Balance:</h2><br><b>" + balance + "</b></html>");

                    StringBuilder transactionsText = new StringBuilder("Last Transactions:\n--------------------\n");
                    for (String txDetails : transactions) {
                        transactionsText.append(txDetails);
                    }
                    transactionsArea.setText(transactionsText.toString());
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    balanceLabel.setText("<html><h2>Error:</h2><br><b>" + e.getMessage() + "</b></html>");
                    transactionsArea.setText("Failed to fetch transaction history: " + e.getMessage());
                });
            }
        }).start();

        return panel;
    }

    @Override
    protected void doOKAction() {
        close(OK_EXIT_CODE);
    }


    private String fetchWalletBalance(String address) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BLOCKFROST_API_URL + "/addresses/" + address))
                .header("project_id", API_KEY)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Failed to fetch data: " + response.body());
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

        if (response.statusCode() != 200) {
            throw new Exception("Failed to fetch transactions: " + response.body());
        }


        return extractTransactionsFromResponse(response.body());
    }


    private String extractBalanceFromResponse(String response) {
        JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
        JsonArray amounts = jsonResponse.getAsJsonArray("amount");
        long balanceInLovelace = amounts.get(0).getAsJsonObject().get("quantity").getAsLong();
        double balanceInAda = balanceInLovelace / 1000000.0;
        return String.format("%.6f ADA", balanceInAda);
    }


    private String formatBlockTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        return sdf.format(new Date(timestamp * 1000));
    }


    private List<String> extractTransactionsFromResponse(String response) {
        List<String> transactions = new ArrayList<>();
        try {
            JsonArray transactionsArray = JsonParser.parseString(response).getAsJsonArray();
            int totalTransactions = transactionsArray.size();
            int startIndex = Math.max(totalTransactions - 5, 0);


            for (int i = totalTransactions - 1; i >= startIndex; i--) {
                JsonObject transaction = transactionsArray.get(i).getAsJsonObject();
                String txHash = transaction.get("tx_hash").getAsString();
                int txIndex = transaction.get("tx_index").getAsInt();
                int blockHeight = transaction.get("block_height").getAsInt();
                long blockTime = transaction.get("block_time").getAsLong();

                String formattedTransaction = String.format(
                        "Tx Hash: %s\nTx Index: %d\nBlock Height: %d\nBlock Time: %s\n--------------------\n",
                        txHash, txIndex, blockHeight, formatBlockTime(blockTime)
                );
                transactions.add(formattedTransaction);
            }
        } catch (Exception e) {
            transactions.add("Error parsing transactions");
        }
        return transactions;
    }

}
