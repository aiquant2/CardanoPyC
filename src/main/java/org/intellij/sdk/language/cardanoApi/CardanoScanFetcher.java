package org.intellij.sdk.language.cardanoApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;


import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.intellij.openapi.updateSettings.impl.PluginDownloader.showErrorDialog;

public class CardanoScanFetcher {

    private static final String API_KEY_PROPERTY = "CardanoScanAPIKey";
    private static final Map<String, String> sessionCache = new ConcurrentHashMap<>(); // Thread-safe session storage
    private final Project project;

    public CardanoScanFetcher(Project project) {
        this.project = project;
    }

    // Trigger action methods - Ensure API key is valid before executing
    public void fetchBlockDetails() {
        String apiKey = ensureApiKey();
        if (apiKey != null) {
            fetchDataWithApiKey(apiKey, CardanoScanApiClient::getBlockDetails);
        }
    }

    public void fetchNetworkState() {
        String apiKey = ensureApiKey();
        if (apiKey != null) {
            fetchDataWithApiKey(apiKey, CardanoScanApiClient::getNetworkState);
        }
    }

    public void fetchProtocolParams() {
        String apiKey = ensureApiKey();
        if (apiKey != null) {
            fetchDataWithApiKey(apiKey, CardanoScanApiClient::getProtocolParams);
        }
    }

    public void fetchGovernanceCommittee() {
        String apiKey = ensureApiKey();
        if (apiKey != null) {
            fetchDataWithApiKey(apiKey, CardanoScanApiClient::getGovernanceCommittee);
        }
    }

    public void fetchAddressDetails(String address) {
        String apiKey = ensureApiKey();
        if (apiKey != null) {
            fetchDataWithApiKey(apiKey, client -> client.getAddressBalance(address));
        }
    }

    public void fetchPoolInfo(String poolId) {
        String apiKey = ensureApiKey();
        if (apiKey != null) {
            fetchDataWithApiKey(apiKey, client -> client.getPoolInfo(poolId));
        }
    }

    public void fetchPoolStatus(String poolId) {
        String apiKey = ensureApiKey();
        if (apiKey != null) {
            fetchDataWithApiKey(apiKey, client -> client.getPoolStatus(poolId));
        }
    }

    public void fetchTransactionInfo(String txHash) {
        String apiKey = ensureApiKey();
        if (apiKey != null) {
            fetchDataWithApiKey(apiKey, client -> client.getTransactionInfo(txHash));
        }
    }

    public void fetchRewardAccountInfo(String rewardAddress) {
        String apiKey = ensureApiKey();
        if (apiKey != null) {
            fetchDataWithApiKey(apiKey, client -> client.getRewardAccountInfo(rewardAddress));
        }
    }

    public void fetchGovernanceHotKeyInfo(String hotHex) {
        String apiKey = ensureApiKey();
        if (apiKey != null) {
            fetchDataWithApiKey(apiKey, client -> client.getGovernanceHotKeyInfo(hotHex));
        }
    }

    public void fetchGovernanceColdKeyInfo(String coldHex) {
        String apiKey = ensureApiKey();
        if (apiKey != null) {
            fetchDataWithApiKey(apiKey, client -> client.getGovernanceColdKeyInfo(coldHex));
        }
    }

    public void fetchGovernanceDRepInfo(String dRepId) {
        String apiKey = ensureApiKey();
        if (apiKey != null) {
            fetchDataWithApiKey(apiKey, client -> client.getGovernanceDRepInfo(dRepId));
        }
    }

    public void fetchGovernanceActionDetails(String actionId) {
        String apiKey = ensureApiKey();
        if (apiKey != null){
            fetchDataWithApiKey(apiKey, client -> client.getGovernanceActionDetails(actionId));
        }
    }
    public void fetchBlockDetails(String blockKey) {
        String apiKey = ensureApiKey();
        if (apiKey != null){
            fetchDataWithApiKey(apiKey, client -> client.getBlockDetails(blockKey));
        }
    }
    public void fetchPoolList(String pageNo) {
        String apiKey = ensureApiKey();
        if (apiKey != null){
            fetchDataWithApiKey(apiKey, client -> client.getPoolList(pageNo));
        }
    }
    public void fetchExpiringPoolList(String pageNo) {
        String apiKey = ensureApiKey();
        if (apiKey != null){
            fetchDataWithApiKey(apiKey, client -> client.getExpiringPoolList(pageNo));
        }
    }
    public void fetchExpiredPoolList(String pageNo) {
        String apiKey = ensureApiKey();
        if (apiKey != null){
            fetchDataWithApiKey(apiKey, client -> client.getExpiredPoolList(pageNo));
        }
    }
    public void fetchAssetDetails(String pageNo) {
        String apiKey = ensureApiKey();
        if (apiKey != null){
            fetchDataWithApiKey(apiKey, client -> client.getAssetDetails(pageNo));
        }
    }
    public void fetchPolicyDetails(String policyId) {
        String apiKey = ensureApiKey();
        if (apiKey != null){
            fetchDataWithApiKey(apiKey, client -> client.getPolicyDetails(policyId));
        }
    }
    public void fetchAssetByAddress(String address) {
        String apiKey = ensureApiKey();
        if (apiKey != null){
            fetchDataWithApiKey(apiKey, client -> client.getAssetByAddress(address));
        }
    }
    public void fetchDRepList(String pageNo) {
        String apiKey = ensureApiKey();
        if (apiKey != null){
            fetchDataWithApiKey(apiKey, client -> client.getDRepList(pageNo));
        }
    }
    public void fetchTransactionListByAddress(String address) {
        String apiKey = ensureApiKey();
        if (apiKey != null){
            fetchDataWithApiKey(apiKey, client -> client.getTransationListByAddress(address));
        }
    }
    public void fetchAddressAssociatedWithAStakeKey(String address) {
        String apiKey = ensureApiKey();
        if (apiKey != null){
            fetchDataWithApiKey(apiKey, client -> client.getAddressAssociatedWithAStakeKey(address));
        }
    }
    public void fetchMemberList(String address) {
        String apiKey = ensureApiKey();
        if (apiKey != null){
            fetchDataWithApiKey(apiKey, client -> client.getMemberList(address));
        }
    }
    // Ensure API key is valid or ask for input if not present
    public String ensureApiKey() {
        String apiKey = sessionCache.get(API_KEY_PROPERTY);
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = promptForApiKey();
        }
        return apiKey;
    }

        public String promptForApiKey() {
            final String[] apiKeyHolder = new String[1];
            ApplicationManager.getApplication().invokeAndWait(() -> {
                String apiKey = Messages.showInputDialog(
                        "Enter your API Key:",
                        "API Key Input",
                        Messages.getQuestionIcon()
                );
                if (apiKey != null && !apiKey.isEmpty()) {
                    apiKeyHolder[0] = apiKey.trim();
                    sessionCache.put(API_KEY_PROPERTY, apiKeyHolder[0]);
                }
            });
            return apiKeyHolder[0];
        }


    private void fetchDataWithApiKey(String apiKey, ApiClientFunction<CardanoScanApiClient, String> function) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                CardanoScanApiClient apiClient = new CardanoScanApiClient(apiKey);
                String rawData = function.apply(apiClient);
                String formattedJson = formatJson(rawData);
                showDataInTerminal(formattedJson);
            } catch (IOException | InterruptedException e) {
                handleException(e);
            }
        });
    }

    private String formatJson(String rawData) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(gson.fromJson(rawData, Object.class));
    }

    private void handleException(Exception e) {
        ApplicationManager.getApplication().invokeLater(() -> {
            String errorMessage = "Error fetching data from CardanoScan API: " + e.getMessage();
            JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
        });

        if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
    }


    private void showDataInTerminal(String data) {
        ApplicationManager.getApplication().invokeLater(() -> {
            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Terminal");
            if (toolWindow == null) {
                showErrorDialog("Terminal ToolWindow not found.");
                return;
            }

            Content existingContent = null;
            for (Content content : toolWindow.getContentManager().getContents()) {
                if ("Cardano Data Terminal".equals(content.getDisplayName())) {
                    existingContent = content;
                    break;
                }
            }

            if (existingContent != null) {
                JPanel existingPanel = (JPanel) existingContent.getComponent();
                ConsoleView existingConsole = (ConsoleView) existingPanel.getComponent(0);
                existingConsole.print("\n" + data, ConsoleViewContentType.NORMAL_OUTPUT);
            } else {
                ConsoleView consoleView = new ConsoleViewImpl(project, true);
                Disposer.register(project, consoleView); // Register disposable with the project

                JPanel terminalPanel = new JPanel(new BorderLayout());
                terminalPanel.add(consoleView.getComponent(), BorderLayout.CENTER);

                Content newContent = ContentFactory.getInstance().createContent(terminalPanel, "Cardano Data Terminal", false);
                toolWindow.getContentManager().addContent(newContent);

                Disposer.register(newContent, consoleView);
                toolWindow.show(() -> {
                    consoleView.clear();
                    consoleView.print(data, ConsoleViewContentType.NORMAL_OUTPUT);
                });
            }
        });
    }





    @FunctionalInterface
    private interface ApiClientFunction<T, R> {
        R apply(T t) throws IOException, InterruptedException;
    }

}
