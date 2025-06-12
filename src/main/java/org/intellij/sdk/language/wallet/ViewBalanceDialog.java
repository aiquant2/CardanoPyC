//package org.intellij.sdk.language.wallet;
//
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import com.intellij.openapi.ui.DialogWrapper;
//import com.intellij.ui.components.JBScrollPane;
//import org.jetbrains.annotations.Nullable;
//
//import javax.swing.*;
//import java.awt.*;
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.TimeZone;
//import java.util.jar.JarOutputStream;
//
//public class ViewBalanceDialog extends DialogWrapper {
//    private static final String netType=WalletApiKeyState.getInstance().getNetwork();
//    static final String state=switch (netType) {
//        case "mainnet" -> "mainnet";
//        case "preprod" -> "preprod";
//        default -> "preview";
//    };
//
//    private static final String BLOCKFROST_API_URL = "https://cardano-" + state + ".blockfrost.io/api/v0";
//
//    private static final String API_KEY = WalletApiKeyState.getInstance().getApiKey();
//
//
//    private JLabel balanceLabel;
//    private JTextArea transactionsArea;
//
//    public ViewBalanceDialog() {
//        super(true);
//        setTitle("Wallet Balance and Transactions");
//        init();
//
//    }
//
//    @Override
//    protected @Nullable JComponent createCenterPanel() {
//        JPanel panel = new JPanel(new BorderLayout());
//        panel.setPreferredSize(new Dimension(350, 400));
//
//        balanceLabel = new JLabel("<html><h2>Your Balance:</h2><br><b>Fetching balance...</b></html>", JLabel.CENTER);
//        panel.add(balanceLabel, BorderLayout.NORTH);
//
//
//        transactionsArea = new JTextArea(12, 40);
//        transactionsArea.setEditable(false);
//        transactionsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
//        transactionsArea.setText("Fetching transaction history...");
//        JBScrollPane scrollPane = new JBScrollPane(transactionsArea);
//        panel.add(scrollPane, BorderLayout.CENTER);
//
//        new Thread(() -> {
//            try {
//                String address = SecureStorageUtil.retrieveCredential("wallet_baseAddress");
//                System.out.println(address);
//                String balance = fetchWalletBalance(address);
//                System.out.println(balance);
//                List<String> transactions = fetchTransactionHistory(address);
//
//                SwingUtilities.invokeLater(() -> {
//                    balanceLabel.setText("<html><h2>Your Balance:</h2><br><b>" + balance + "</b></html>");
//
//                    StringBuilder transactionsText = new StringBuilder("Last Transactions:\n--------------------\n");
//                    for (String txDetails : transactions) {
//                        transactionsText.append(txDetails);
//                    }
//                    transactionsArea.setText(transactionsText.toString());
//                });
//            } catch (Exception e) {
//                SwingUtilities.invokeLater(() -> {
//                    balanceLabel.setText("<html><h2>Error:</h2><br><b>" + e.getMessage() + "</b></html>");
//                    transactionsArea.setText("Failed to fetch transaction history: " + e.getMessage());
//                });
//            }
//        }).start();
//
//        return panel;
//    }
//
//    @Override
//    protected void doOKAction() {
//        close(OK_EXIT_CODE);
//    }
//
//
//    private String fetchWalletBalance(String address) throws Exception {
//        System.out.println(address);
//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(BLOCKFROST_API_URL + "/addresses/" + address))
//                .header("project_id", API_KEY)
//                .build();
//
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//        System.out.println("Response from Blockfrost: " + response.body());
//
////        if (response.statusCode() != 200) {
////            throw new Exception("Failed to fetch data: " + response.body());
////        }
//
//        if (response.statusCode() == 404) {
//            throw new Exception("Address not found. Make sure the wallet is restored and synced.");
//        } else if (response.statusCode() != 200) {
//            throw new Exception("Failed to fetch data (" + response.statusCode() + "): " + response.body());
//        }
//
//
//        return extractBalanceFromResponse(response.body());
//    }
//
//
//    private List<String> fetchTransactionHistory(String address) throws Exception {
//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(BLOCKFROST_API_URL + "/addresses/" + address + "/transactions"))
//                .header("project_id", API_KEY)
//                .build();
//
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        if (response.statusCode() != 200) {
//            throw new Exception("Failed to fetch transactions: " + response.body());
//        }
//
//
//        return extractTransactionsFromResponse(response.body());
//    }
//
//
////    private String extractBalanceFromResponse(String response) {
////        JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
////        JsonArray amounts = jsonResponse.getAsJsonArray("amount");
////        long balanceInLovelace = amounts.get(0).getAsJsonObject().get("quantity").getAsLong();
////        double balanceInAda = balanceInLovelace / 1000000.0;
////        return String.format("%.6f ADA", balanceInAda);
////    }
//
//    private String extractBalanceFromResponse(String response) {
//        JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
//
//        if (!jsonResponse.has("amount")) {
//            return "0.000000 ADA";
//        }
//
//        JsonArray amounts = jsonResponse.getAsJsonArray("amount");
//
//        if (amounts.size() == 0) {
//            return "0.000000 ADA";
//        }
//
//        // Look for ADA (lovelace)
//        for (int i = 0; i < amounts.size(); i++) {
//            JsonObject amountObj = amounts.get(i).getAsJsonObject();
//            String unit = amountObj.get("unit").getAsString();
//            if ("lovelace".equals(unit)) {
//                long balanceInLovelace = amountObj.get("quantity").getAsLong();
//                double balanceInAda = balanceInLovelace / 1_000_000.0;
//                return String.format("%.6f ADA", balanceInAda);
//            }
//        }
//
//        // No lovelace found — possibly only tokens or NFTs
//        return "0.000000 ADA";
//    }
//
//
//
//    private String formatBlockTime(long timestamp) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
//        return sdf.format(new Date(timestamp * 1000));
//    }
//
//
//    private List<String> extractTransactionsFromResponse(String response) {
//        List<String> transactions = new ArrayList<>();
//        try {
//            JsonArray transactionsArray = JsonParser.parseString(response).getAsJsonArray();
//            int totalTransactions = transactionsArray.size();
//            int startIndex = Math.max(totalTransactions - 5, 0);
//
//
//            for (int i = totalTransactions - 1; i >= startIndex; i--) {
//                JsonObject transaction = transactionsArray.get(i).getAsJsonObject();
//                String txHash = transaction.get("tx_hash").getAsString();
//                int txIndex = transaction.get("tx_index").getAsInt();
//                int blockHeight = transaction.get("block_height").getAsInt();
//                long blockTime = transaction.get("block_time").getAsLong();
//
//                String formattedTransaction = String.format(
//                        "Tx Hash: %s\nTx Index: %d\nBlock Height: %d\nBlock Time: %s\n--------------------\n",
//                        txHash, txIndex, blockHeight, formatBlockTime(blockTime)
//                );
//                transactions.add(formattedTransaction);
//            }
//        } catch (Exception e) {
//            transactions.add("Error parsing transactions");
//        }
//        return transactions;
//    }
//
//}

//
//package org.intellij.sdk.language.wallet;
//
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import com.google.gson.JsonSyntaxException;
//import com.intellij.openapi.ui.DialogWrapper;
//import com.intellij.ui.components.JBScrollPane;
//import org.jetbrains.annotations.Nullable;
//
//import javax.swing.*;
//import java.awt.*;
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.TimeZone;
//
//public class ViewBalanceDialog extends DialogWrapper {
//    private static final String netType = WalletApiKeyState.getInstance().getNetwork();
//    static final String state = switch (netType) {
//        case "mainnet" -> "mainnet";
//        case "preprod" -> "preprod";
//        default -> "preview";
//    };
//
//    private static final String BLOCKFROST_API_URL = "https://cardano-" + state + ".blockfrost.io/api/v0";
//    private static final String API_KEY = WalletApiKeyState.getInstance().getApiKey();
//
//    private  JLabel balanceLabel;
//    private  JTextArea transactionsArea;
//
//    public ViewBalanceDialog() {
//        super(true);
//        setTitle("Wallet Balance and Transactions");
//        init();
//    }
//
//    @Override
//    protected @Nullable JComponent createCenterPanel() {
//        JPanel panel = new JPanel(new BorderLayout());
//        panel.setPreferredSize(new Dimension(350, 400));
//
//        balanceLabel = new JLabel("<html><h2>Your Balance:</h2><br><b>Fetching balance...</b></html>", JLabel.CENTER);
//        panel.add(balanceLabel, BorderLayout.NORTH);
//
//        transactionsArea = new JTextArea(12, 40);
//        transactionsArea.setEditable(false);
//        transactionsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
//        transactionsArea.setText("Fetching transaction history...");
//        JBScrollPane scrollPane = new JBScrollPane(transactionsArea);
//        panel.add(scrollPane, BorderLayout.CENTER);
//
//        fetchWalletDataAsync();
//        return panel;
//    }
//
//    private void fetchWalletDataAsync() {
//        new Thread(() -> {
//            try {
//                String address = SecureStorageUtil.retrieveCredential("wallet_baseAddress");
//                if (address == null || address.isEmpty()) {
//                    throw new Exception("Wallet address not found");
//                }
//
//                String balance = fetchWalletBalance(address);
//                List<String> transactions = fetchTransactionHistory(address);
//
//                SwingUtilities.invokeLater(() -> {
//                    balanceLabel.setText("<html><h2>Your Balance:</h2><br><b>" + balance + "</b></html>");
//
//                    StringBuilder transactionsText = new StringBuilder("Last Transactions:\n--------------------\n");
//                    for (String txDetails : transactions) {
//                        transactionsText.append(txDetails);
//                    }
//                    transactionsArea.setText(transactionsText.toString());
//                });
//            } catch (Exception e) {
//                SwingUtilities.invokeLater(() -> {
//                    balanceLabel.setText("<html><h2>Error:</h2><br><b>" + e.getMessage() + "</b></html>");
//                    transactionsArea.setText("Failed to fetch transaction history: " + e.getMessage());
//                });
//            }
//        }).start();
//    }
//
//    @Override
//    protected void doOKAction() {
//        close(OK_EXIT_CODE);
//    }
//
//    private String fetchWalletBalance(String address) throws Exception {
//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(BLOCKFROST_API_URL + "/addresses/" + address))
//                .header("project_id", API_KEY)
//                .build();
//
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        if (response.statusCode() == 404) {
//            throw new Exception("Address not found. Make sure the wallet is restored and synced.");
//        } else if (response.statusCode() != 200) {
//            throw new Exception("Failed to fetch data (" + response.statusCode() + "): " + response.body());
//        }
//
//        return extractBalanceFromResponse(response.body());
//    }
//
//    private List<String> fetchTransactionHistory(String address) throws Exception {
//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(BLOCKFROST_API_URL + "/addresses/" + address + "/transactions"))
//                .header("project_id", API_KEY)
//                .build();
//
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        if (response.statusCode() != 200) {
//            throw new Exception("Failed to fetch transactions: " + response.body());
//        }
//
//        return extractTransactionsFromResponse(response.body());
//    }
//
//    private String extractBalanceFromResponse(String response) throws JsonSyntaxException {
//        try {
//            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
//
//            if (!jsonResponse.has("amount")) {
//                return "0.000000 ADA";
//            }
//
//            JsonArray amounts = jsonResponse.getAsJsonArray("amount");
//            if (amounts.size() == 0) {
//                return "0.000000 ADA";
//            }
//
//            for (int i = 0; i < amounts.size(); i++) {
//                JsonObject amountObj = amounts.get(i).getAsJsonObject();
//                String unit = amountObj.get("unit").getAsString();
//                if ("lovelace".equals(unit)) {
//                    long balanceInLovelace = amountObj.get("quantity").getAsLong();
//                    double balanceInAda = balanceInLovelace / 1_000_000.0;
//                    return String.format("%.6f ADA", balanceInAda);
//                }
//            }
//            return "0.000000 ADA";
//        } catch (Exception e) {
//            throw new JsonSyntaxException("Failed to parse balance response", e);
//        }
//    }
//
//    private String formatBlockTime(long timestamp) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
//        return sdf.format(new Date(timestamp * 1000));
//    }
//
//    private List<String> extractTransactionsFromResponse(String response) {
//        List<String> transactions = new ArrayList<>();
//        try {
//            JsonArray transactionsArray = JsonParser.parseString(response).getAsJsonArray();
//            int totalTransactions = transactionsArray.size();
//            int transactionsToShow = Math.min(5, totalTransactions);
//
//            for (int i = totalTransactions - 1; i >= totalTransactions - transactionsToShow; i--) {
//                JsonObject transaction = transactionsArray.get(i).getAsJsonObject();
//                String txHash = transaction.get("tx_hash").getAsString();
//                int txIndex = transaction.get("tx_index").getAsInt();
//                int blockHeight = transaction.get("block_height").getAsInt();
//                long blockTime = transaction.get("block_time").getAsLong();
//
//                transactions.add(String.format(
//                        "Tx Hash: %s\nTx Index: %d\nBlock Height: %d\nBlock Time: %s\n--------------------\n",
//                        txHash, txIndex, blockHeight, formatBlockTime(blockTime)
//                ));
//            }
//        } catch (Exception e) {
//            transactions.add("Error parsing transactions: " + e.getMessage());
//        }
//        return transactions;
//    }
//}


// new
//
//package org.intellij.sdk.language.wallet;
//
//import com.google.gson.*;
//import com.intellij.openapi.ui.DialogWrapper;
//import com.intellij.ui.components.JBScrollPane;
//import org.jetbrains.annotations.Nullable;
//
//import javax.swing.*;
////import java.awt.*;
//import java.awt.*;
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.List;
//
//public class ViewBalanceDialog extends DialogWrapper {
//    private static final String netTypeRaw = WalletApiKeyState.getInstance().getNetwork();
//    private static final String netType = netTypeRaw == null ? "" : netTypeRaw;
//    private static final String state = switch (netType) {
//        case "mainnet" -> "mainnet";
//        case "preprod" -> "preprod";
//        default -> "preview";
//    };
//
//    private static final String BLOCKFROST_API_URL = "https://cardano-" + state + ".blockfrost.io/api/v0";
//    private static final String API_KEY = WalletApiKeyState.getInstance().getApiKey();
//
//
//    private JLabel balanceLabel;
//    private JTextArea transactionsArea;
//
//    public ViewBalanceDialog() {
//        super(true);
//        System.out.println(API_KEY);
//        setTitle("Wallet Balance and Transactions");
//        init();
//    }
//
//    @Override
//    protected @Nullable JComponent createCenterPanel() {
//        JPanel panel = new JPanel(new BorderLayout());
//        panel.setPreferredSize(new Dimension(350, 400));
//
//        balanceLabel = new JLabel("<html><h2>Your Balance:</h2><br><b>Fetching balance...</b></html>", JLabel.CENTER);
//        panel.add(balanceLabel, BorderLayout.NORTH);
//
//        transactionsArea = new JTextArea(12, 40);
//        transactionsArea.setEditable(false);
//        transactionsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
//        transactionsArea.setText("Fetching transaction history...");
//        JBScrollPane scrollPane = new JBScrollPane(transactionsArea);
//        panel.add(scrollPane, BorderLayout.CENTER);
//
//        fetchWalletDataAsync();
//        return panel;
//    }
//
//    private void fetchWalletDataAsync() {
//        new Thread(() -> {
//            try {
//                String address = SecureStorageUtil.retrieveCredential("wallet_baseAddress");
//                if (address == null || address.isEmpty()) {
//                    throw new Exception("Wallet address not found");
//                }
//
//                if (API_KEY == null || API_KEY.isEmpty()) {
//                    throw new Exception("API Key not set. Please enter a valid Blockfrost API key.");
//                }
//
//                String balance = fetchWalletBalance(address);
//                List<String> transactions = fetchTransactionHistory(address);
//
//                SwingUtilities.invokeLater(() -> {
//                    balanceLabel.setText("<html><h2>Your Balance:</h2><br><b>" + balance + "</b></html>");
//
//                    StringBuilder transactionsText = new StringBuilder("Last Transactions:\n--------------------\n");
//                    for (String txDetails : transactions) {
//                        transactionsText.append(txDetails);
//                    }
//                    transactionsArea.setText(transactionsText.toString());
//                });
//            } catch (Exception e) {
//                e.printStackTrace(); // Show full stack trace in console
//                SwingUtilities.invokeLater(() -> {
//                    balanceLabel.setText("<html><h2>Error:</h2><br><b>" + e.getMessage() + "</b></html>");
//                    transactionsArea.setText("Failed to fetch transaction history:\n" + e);
//                });
//            }
//        }).start();
//    }
//
//    private String fetchWalletBalance(String address) throws Exception {
//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(BLOCKFROST_API_URL + "/addresses/" + address))
//                .header("project_id", API_KEY)
//                .build();
//
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        if (response.statusCode() == 404) {
//            throw new Exception("Address not found. Make sure the wallet is restored and synced.");
//        } else if (response.statusCode() != 200) {
//            throw new Exception("Failed to fetch data (" + response.statusCode() + "): " + response.body());
//        }
//
//        return extractBalanceFromResponse(response.body());
//    }
//
//    private List<String> fetchTransactionHistory(String address) throws Exception {
//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(BLOCKFROST_API_URL + "/addresses/" + address + "/transactions"))
//                .header("project_id", API_KEY)
//                .build();
//
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        if (response.statusCode() != 200) {
//            throw new Exception("Failed to fetch transactions: " + response.body());
//        }
//
//        return extractTransactionsFromResponse(response.body());
//    }
//
//    private String extractBalanceFromResponse(String response) throws JsonSyntaxException {
//        try {
//            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
//
//            if (!jsonResponse.has("amount")) {
//                return "0.000000 ADA";
//            }
//
//            JsonArray amounts = jsonResponse.getAsJsonArray("amount");
//            if (amounts.size() == 0) {
//                return "0.000000 ADA";
//            }
//
//            for (JsonElement element : amounts) {
//                JsonObject amountObj = element.getAsJsonObject();
//                String unit = amountObj.get("unit").getAsString();
//                if ("lovelace".equals(unit)) {
//                    long balanceInLovelace = amountObj.get("quantity").getAsLong();
//                    double balanceInAda = balanceInLovelace / 1_000_000.0;
//                    return String.format("%.6f ADA", balanceInAda);
//                }
//            }
//            return "0.000000 ADA";
//        } catch (Exception e) {
//            throw new JsonSyntaxException("Failed to parse balance response", e);
//        }
//    }
//
//    private String formatBlockTime(long timestamp) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
//        return sdf.format(new Date(timestamp * 1000));
//    }
//
//    private List<String> extractTransactionsFromResponse(String response) {
//        List<String> transactions = new ArrayList<>();
//        try {
//            JsonElement element = JsonParser.parseString(response);
//            if (!element.isJsonArray()) {
//                throw new JsonParseException("Expected a JSON array but got: " + element);
//            }
//
//            JsonArray transactionsArray = element.getAsJsonArray();
//            int totalTransactions = transactionsArray.size();
//            int transactionsToShow = Math.min(5, totalTransactions);
//
//            for (int i = totalTransactions - 1; i >= totalTransactions - transactionsToShow; i--) {
//                JsonObject transaction = transactionsArray.get(i).getAsJsonObject();
//                String txHash = transaction.get("tx_hash").getAsString();
//                int txIndex = transaction.get("tx_index").getAsInt();
//                int blockHeight = transaction.get("block_height").getAsInt();
//                long blockTime = transaction.get("block_time").getAsLong();
//
//                transactions.add(String.format(
//                        "Tx Hash: %s\nTx Index: %d\nBlock Height: %d\nBlock Time: %s\n--------------------\n",
//                        txHash, txIndex, blockHeight, formatBlockTime(blockTime)
//                ));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            transactions.add("Error parsing transactions: " + e.getMessage());
//        }
//        return transactions;
//    }
//}




// new  new

//
//package org.intellij.sdk.language.wallet;
//
//import com.google.gson.*;
//import com.intellij.openapi.ui.DialogWrapper;
//import com.intellij.ui.components.JBScrollPane;
//import org.jetbrains.annotations.Nullable;
//
//import javax.swing.*;
//import java.awt.*;
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.List;
//
//public class ViewBalanceDialog extends DialogWrapper {
////    private static final String netTypeRaw = WalletApiKeyState.getInstance().getNetwork();
////    private static final String netType = netTypeRaw == null ? "" : netTypeRaw;
////
////    private static final String state = switch (netType) {
////        case "mainnet" -> "mainnet";
////        case "preprod" -> "preprod";
////        default -> "preview";
////    };
////
////    private static  String BLOCKFROST_API_URL = "https://cardano-" + state + ".blockfrost.io/api/v0";
////    private static  String API_KEY = WalletApiKeyState.getInstance().getApiKey();
//
//
//    private String getNetworkType() {
//        String netTypeRaw = WalletApiKeyState.getInstance().getNetwork();
//        return switch (netTypeRaw == null ? "" : netTypeRaw) {
//            case "mainnet" -> "mainnet";
//            case "preprod" -> "preprod";
//            default -> "preview";
//        };
//    }
//
//    private String getBlockfrostUrl() {
//        return "https://cardano-" + getNetworkType() + ".blockfrost.io/api/v0";
//    }
//
//    private String getApiKey() {
//        return WalletApiKeyState.getInstance().getApiKey();
//    }
//
//
//
//    private JLabel balanceLabel;
//    private JTextArea transactionsArea;
//    private String walletAddress;
//
//    public ViewBalanceDialog() {
//        super(true);
//        setTitle("Wallet Balance and Transactions");
//
//        // Ask user to input wallet address
//        walletAddress = JOptionPane.showInputDialog(
//                null,
//                "Enter your Cardano wallet address:",
//                "Wallet Address Input",
//                JOptionPane.PLAIN_MESSAGE
//        );
//
//        if (walletAddress == null || walletAddress.trim().isEmpty()) {
//            JOptionPane.showMessageDialog(null, "No address provided. Dialog will now close.", "Error", JOptionPane.ERROR_MESSAGE);
//            close(CANCEL_EXIT_CODE); // Closes the dialog gracefully
//            return;
//        }
//
//        walletAddress = walletAddress.trim();
//        init();
//    }
//
//    @Override
//    protected @Nullable JComponent createCenterPanel() {
//        JPanel panel = new JPanel(new BorderLayout());
//        panel.setPreferredSize(new Dimension(350, 400));
//
//        balanceLabel = new JLabel("<html><h2>Your Balance:</h2><br><b>Fetching balance...</b></html>", JLabel.CENTER);
//        panel.add(balanceLabel, BorderLayout.NORTH);
//
//        transactionsArea = new JTextArea(12, 40);
//        transactionsArea.setEditable(false);
//        transactionsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
//        transactionsArea.setText("Fetching transaction history...");
//        JBScrollPane scrollPane = new JBScrollPane(transactionsArea);
//        panel.add(scrollPane, BorderLayout.CENTER);
//
//        fetchWalletDataAsync();
//        return panel;
//    }
//
//    private void fetchWalletDataAsync() {
//        new Thread(() -> {
//            try {
//
//                if (getApiKey() == null || getApiKey().isEmpty()) {
//                    throw new Exception("API Key not set. Please enter a valid Blockfrost API key.");
//                }
//
//                String balance = fetchWalletBalance(walletAddress);
//                List<String> transactions = fetchTransactionHistory(walletAddress);
//
//                SwingUtilities.invokeLater(() -> {
//                    balanceLabel.setText("<html><h2>Your Balance:</h2><br><b>" + balance + "</b></html>");
//
//                    StringBuilder transactionsText = new StringBuilder("Last Transactions:\n--------------------\n");
//                    for (String txDetails : transactions) {
//                        transactionsText.append(txDetails);
//                    }
//                    transactionsArea.setText(transactionsText.toString());
//                });
//            } catch (Exception e) {
//                e.printStackTrace(); // Debug info in console
//                SwingUtilities.invokeLater(() -> {
//                    balanceLabel.setText("<html><h2>Error:</h2><br><b>" + e.getMessage() + "</b></html>");
//                    transactionsArea.setText("Failed to fetch transaction history:\n" + e.getMessage());
//                });
//            }
//        }).start();
//    }
//
//    private String fetchWalletBalance(String address) throws Exception {
//
//        HttpClient client = HttpClient.newHttpClient();
////        HttpRequest request = HttpRequest.newBuilder()
////                .uri(URI.create(BLOCKFROST_API_URL + "/addresses/" + address))
////                .header("project_id", API_KEY)
////                .build();
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(getBlockfrostUrl() + "/addresses/" + address))
//                .header("project_id", getApiKey())
//                .build();
//
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        if (response.statusCode() == 404) {
//            throw new Exception("Address not found. Make sure the wallet is restored and synced.");
//        } else if (response.statusCode() != 200) {
//            throw new Exception("Failed to fetch data (" + response.statusCode() + "): " + response.body());
//        }
//
//        return extractBalanceFromResponse(response.body());
//    }
//
//    private List<String> fetchTransactionHistory(String address) throws Exception {
//        HttpClient client = HttpClient.newHttpClient();
////        HttpRequest request = HttpRequest.newBuilder()
////                .uri(URI.create(BLOCKFROST_API_URL + "/addresses/" + address + "/transactions"))
////                .header("project_id", API_KEY)
////                .build();
//
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(getBlockfrostUrl() + "/addresses/" + address))
//                .header("project_id", getApiKey())
//                .build();
//
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        if (response.statusCode() != 200) {
//            throw new Exception("Failed to fetch transactions: " + response.body());
//        }
//
//        return extractTransactionsFromResponse(response.body());
//    }
//
//    private String extractBalanceFromResponse(String response) throws JsonSyntaxException {
//        try {
//            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
//
//            if (!jsonResponse.has("amount")) {
//                return "0.000000 ADA";
//            }
//
//            JsonArray amounts = jsonResponse.getAsJsonArray("amount");
//            if (amounts.size() == 0) {
//                return "0.000000 ADA";
//            }
//
//            for (JsonElement element : amounts) {
//                JsonObject amountObj = element.getAsJsonObject();
//                String unit = amountObj.get("unit").getAsString();
//                if ("lovelace".equals(unit)) {
//                    long balanceInLovelace = amountObj.get("quantity").getAsLong();
//                    double balanceInAda = balanceInLovelace / 1_000_000.0;
//                    return String.format("%.6f ADA", balanceInAda);
//                }
//            }
//            return "0.000000 ADA";
//        } catch (Exception e) {
//            throw new JsonSyntaxException("Failed to parse balance response", e);
//        }
//    }
//
//    private List<String> extractTransactionsFromResponse(String response) {
//        List<String> transactions = new ArrayList<>();
//        try {
//            JsonElement element = JsonParser.parseString(response);
//            if (!element.isJsonArray()) {
//                throw new JsonParseException("Expected a JSON array but got: " + element);
//            }
//
//            JsonArray transactionsArray = element.getAsJsonArray();
//            int totalTransactions = transactionsArray.size();
//            int transactionsToShow = Math.min(5, totalTransactions);
//
//            for (int i = totalTransactions - 1; i >= totalTransactions - transactionsToShow; i--) {
//                JsonObject transaction = transactionsArray.get(i).getAsJsonObject();
//                String txHash = transaction.get("tx_hash").getAsString();
//                int txIndex = transaction.get("tx_index").getAsInt();
//                int blockHeight = transaction.get("block_height").getAsInt();
//                long blockTime = transaction.get("block_time").getAsLong();
//
//                transactions.add(String.format(
//                        "Tx Hash: %s\nTx Index: %d\nBlock Height: %d\nBlock Time: %s\n--------------------\n",
//                        txHash, txIndex, blockHeight, formatBlockTime(blockTime)
//                ));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            transactions.add("Error parsing transactions: " + e.getMessage());
//        }
//        return transactions;
//    }
//
//    private String formatBlockTime(long timestamp) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
//        return sdf.format(new Date(timestamp * 1000));
//    }
//}


///
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

public class ViewBalanceDialog extends DialogWrapper {

    private JLabel balanceLabel;
    private JTextArea transactionsArea;
    private String walletAddress;

    public ViewBalanceDialog() {
        super(true);
        setTitle("Wallet Balance and Transactions");

        // Ask user to input wallet address
        walletAddress = JOptionPane.showInputDialog(
                null,
                "Enter your Cardano wallet address:",
                "Wallet Address Input",
                JOptionPane.PLAIN_MESSAGE
        );

        if (walletAddress == null || walletAddress.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No address provided. Dialog will now close.", "Error", JOptionPane.ERROR_MESSAGE);
            close(CANCEL_EXIT_CODE); // Closes the dialog gracefully
            return;
        }

        walletAddress = walletAddress.trim();
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
        new Thread(() -> {
            try {
                String apiKey = getApiKey();
                if (apiKey == null || apiKey.isEmpty()) {
                    throw new Exception("API Key not set. Please enter a valid Blockfrost API key.");
                }

                String balance = fetchWalletBalance(walletAddress);
                List<String> transactions = fetchTransactionHistory(walletAddress);

                SwingUtilities.invokeLater(() -> {
                    balanceLabel.setText("<html><h2>Your Balance:</h2><br><b>" + balance + "</b></html>");
                    StringBuilder transactionsText = new StringBuilder("Last Transactions:\n--------------------\n");
                    for (String txDetails : transactions) {
                        transactionsText.append(txDetails);
                    }
                    transactionsArea.setText(transactionsText.toString());
                });
            } catch (Exception e) {
                e.printStackTrace(); // Debug info in console
                SwingUtilities.invokeLater(() -> {
                    balanceLabel.setText("<html><h2>Error:</h2><br><b>" + e.getMessage() + "</b></html>");
                    transactionsArea.setText("Failed to fetch transaction history:\n" + e.getMessage());
                });
            }
        }).start();
    }

    private String fetchWalletBalance(String address) throws Exception {
        System.out.println(getBlockfrostUrl());
        System.out.println(getApiKey());
        System.out.println(getNetworkType());

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getBlockfrostUrl() + "/addresses/" + address))
                .header("project_id", getApiKey())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 404) {
            throw new Exception("Address not found. Make sure the wallet is restored and synced.");
        } else if (response.statusCode() != 200) {
            throw new Exception("Failed to fetch data (" + response.statusCode() + "): " + response.body());
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

        if (response.statusCode() != 200) {
            throw new Exception("Failed to fetch transactions: " + response.body());
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
            if (amounts.size() == 0) {
                return "0.000000 ADA";
            }

            for (JsonElement element : amounts) {
                JsonObject amountObj = element.getAsJsonObject();
                String unit = amountObj.get("unit").getAsString();
                if ("lovelace".equals(unit)) {
                    long balanceInLovelace = amountObj.get("quantity").getAsLong();
                    double balanceInAda = balanceInLovelace / 1_000_000.0;
                    return String.format("%.6f ADA", balanceInAda);
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
            JsonElement element = JsonParser.parseString(response);
            if (!element.isJsonArray()) {
                throw new JsonParseException("Expected a JSON array but got: " + element);
            }

            JsonArray transactionsArray = element.getAsJsonArray();
            int totalTransactions = transactionsArray.size();
            int transactionsToShow = Math.min(5, totalTransactions);

            for (int i = totalTransactions - 1; i >= totalTransactions - transactionsToShow; i--) {
                JsonObject transaction = transactionsArray.get(i).getAsJsonObject();
                String txHash = transaction.get("tx_hash").getAsString();
                int txIndex = transaction.get("tx_index").getAsInt();
                int blockHeight = transaction.get("block_height").getAsInt();
                long blockTime = transaction.get("block_time").getAsLong();

                transactions.add(String.format(
                        "Tx Hash: %s\nTx Index: %d\nBlock Height: %d\nBlock Time: %s\n--------------------\n",
                        txHash, txIndex, blockHeight, formatBlockTime(blockTime)
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            transactions.add("Error parsing transactions: " + e.getMessage());
        }
        return transactions;
    }

    private String formatBlockTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        return sdf.format(new Date(timestamp * 1000));
    }

    // 🧠 Dynamic methods for latest state
    private String getNetworkType() {
        String netTypeRaw = WalletApiKeyState.getInstance().getNetwork();
        return switch (netTypeRaw == null ? "" : netTypeRaw) {
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
