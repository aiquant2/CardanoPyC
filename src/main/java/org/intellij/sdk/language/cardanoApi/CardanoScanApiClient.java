package org.intellij.sdk.language.cardanoApi;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CardanoScanApiClient {

    private final HttpClient client;
    private final String apiKey;

    public CardanoScanApiClient(String apiKey) {
        this.apiKey = apiKey;
        this.client = HttpClient.newHttpClient();
    }

    public String getBlockDetails() throws IOException, InterruptedException {
        String url = "https://api.cardanoscan.io/api/v1/block/latest";

        // Construct the HTTP request with the API key
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apiKey", apiKey)
                .GET()
                .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new IOException("Unexpected response: " + response.statusCode() + " - " + response.body());
        }
    }
    public String getNetworkState() throws IOException, InterruptedException {
        String url = "https://api.cardanoscan.io/api/v1/network/state";

        // Construct the HTTP request with the API key
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apiKey", apiKey)
                .GET()
                .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new IOException("Unexpected response: " + response.statusCode() + " - " + response.body());
        }
    }
    public String getProtocolParams() throws IOException, InterruptedException {
        String url = "https://api.cardanoscan.io/api/v1/network/protocolParams";

        // Construct the HTTP request with the API key
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apiKey", apiKey)
                .GET()
                .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new IOException("Unexpected response: " + response.statusCode() + " - " + response.body());
        }
    }

    public String getGovernanceCommittee() throws IOException, InterruptedException {
        String url = "https://api.cardanoscan.io/api/v1/governance/committee";

        // Construct the HTTP request with the API key
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apiKey", apiKey)
                .GET()
                .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new IOException("Unexpected response: " + response.statusCode() + " - " + response.body());
        }
    }
    public String getAddressBalance(String address) throws IOException, InterruptedException {
        String url = "https://api.cardanoscan.io/api/v1/address/balance?address=" + address;

        // Construct the HTTP request with the API key
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apiKey", apiKey)
                .GET()
                .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new IOException("Unexpected response: " + response.statusCode() + " - " + response.body());
        }
    }
    public String getPoolInfo(String poolId) throws IOException, InterruptedException {
        // Construct the URL for the CardanoScan API request
        String url = "https://api.cardanoscan.io/api/v1/pool?poolId=" + poolId;

        // Construct the HTTP request with the API key
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apiKey", apiKey) // Pass API key in the header
                .GET()
                .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check for a successful response
        if (response.statusCode() == 200) {
            return response.body(); // Return the raw JSON response body
        } else {
            // Throw an exception for an unexpected response
            throw new IOException("Unexpected response: " + response.statusCode() + " - " + response.body());
        }
    }
    public String getPoolStatus(String poolId) throws IOException, InterruptedException {
        // Construct the URL for the CardanoScan API request
        String url = "https://api.cardanoscan.io/api/v1/pool/stats?poolId=" + poolId;

        // Construct the HTTP request with the API key
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apiKey", apiKey) // Pass API key in the header
                .GET()
                .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check for a successful response
        if (response.statusCode() == 200) {
            return response.body(); // Return the raw JSON response body
        } else {
            // Throw an exception for an unexpected response
            throw new IOException("Unexpected response: " + response.statusCode() + " - " + response.body());
        }
    }
    public String getTransactionInfo(String txHash) throws IOException, InterruptedException {
        // Construct the URL for the CardanoScan API request
        String url = "https://api.cardanoscan.io/api/v1/transaction?hash=" + txHash;

        // Construct the HTTP request with the API key
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apiKey", apiKey) // Pass API key in the header
                .GET()
                .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check for a successful response
        if (response.statusCode() == 200) {
            return response.body(); // Return the raw JSON response body
        } else {
            // Throw an exception for an unexpected response
            throw new IOException("Unexpected response: " + response.statusCode() + " - " + response.body());
        }
    }
    public String getRewardAccountInfo(String rewardAddress) throws IOException, InterruptedException {
        // Construct the URL for the CardanoScan API request
        String url = "https://api.cardanoscan.io/api/v1/rewardAccount?rewardAddress=" + rewardAddress;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apiKey", apiKey) // Pass API key in the header
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body(); // Return the raw JSON response body
        } else {
            throw new IOException("Unexpected response: " + response.statusCode() + " - " + response.body());
        }
    }
    public String getGovernanceHotKeyInfo(String hotHex) throws IOException, InterruptedException {
        String url = "https://api.cardanoscan.io/api/v1/governance/ccHot?hotHex=" + hotHex;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apiKey", apiKey) // Pass API key in the header
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body(); // Return the raw JSON response body
        } else {
            throw new IOException("Unexpected response: " + response.statusCode() + " - " + response.body());
        }
    }
    public String getGovernanceColdKeyInfo(String coldHex) throws IOException, InterruptedException {
        String url = "https://api.cardanoscan.io/api/v1/governance/ccMember?coldHex=" + coldHex;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apiKey", apiKey) // Pass API key in the header
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return response.body(); // Return the raw JSON response body
        } else {
            throw new IOException("Unexpected response: " + response.statusCode() + " - " + response.body());
        }
    }
    public String getGovernanceDRepInfo(String dRepId) throws IOException, InterruptedException {
        String url = "https://api.cardanoscan.io/api/v1/governance/dRep?dRepId=" + dRepId;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apiKey", apiKey) // Add the API key to the request header
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new IOException("Unexpected response: " + response.statusCode() + " - " + response.body());
        }
    }
    public String getGovernanceActionDetails(String actionId) throws IOException, InterruptedException {
        String url = "https://api.cardanoscan.io/api/v1/governance/action?actionId=" + actionId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apiKey", apiKey) // Add the API key to the request header
                .GET()
                .build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new IOException("Unexpected response: " + response.statusCode() + " - " + response.body());
        }
    }
    public String getBlockDetails(String blockKey) throws IOException, InterruptedException {
        String url = "https://api.cardanoscan.io/api/v1/block?" + blockKey;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apiKey", apiKey) // Add the API key to the request header
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body(); // Return the raw JSON response body
        } else {
            throw new IOException("Unexpected response: " + response.statusCode() + " - " + response.body());
        }
    }
    public String getPoolList(String pageNo) throws IOException, InterruptedException {
        String url = "https://api.cardanoscan.io/api/v1/pool/list?" + pageNo;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apiKey", apiKey) // Add the API key to the request header
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body(); // Return the raw JSON response body
        } else {
            throw new IOException("Unexpected response: " + response.statusCode() + " - " + response.body());
        }
    }
    public String getExpiringPoolList(String pageNo) throws IOException, InterruptedException {
        String url = "https://api.cardanoscan.io/api/v1/pool/list/expiring?" + pageNo;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apiKey", apiKey) // Add the API key to the request header
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body(); // Return the raw JSON response body
        } else {
            throw new IOException("Unexpected response: " + response.statusCode() + " - " + response.body());
        }
    }
    public String getExpiredPoolList(String pageNo) throws IOException, InterruptedException {
        String url = "https://api.cardanoscan.io/api/v1/pool/list/expired?" + pageNo;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apiKey", apiKey) // Add the API key to the request header
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body(); // Return the raw JSON response body
        } else {
            throw new IOException("Unexpected response: " + response.statusCode() + " - " + response.body());
        }
    }
    public String getAssetDetails(String assetId) throws IOException, InterruptedException {
        String url = "https://api.cardanoscan.io/api/v1/asset?" + assetId;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apiKey", apiKey) // Add the API key to the request header
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body(); // Return the raw JSON response body
        } else {
            throw new IOException("Unexpected response: " + response.statusCode() + " - " + response.body());
        }
    }
    public String getPolicyDetails(String assetId) throws IOException, InterruptedException {
        String url = "https://api.cardanoscan.io/api/v1/asset/list/byPolicyId?" + assetId;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apiKey", apiKey) // Add the API key to the request header
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body(); // Return the raw JSON response body
        } else {
            throw new IOException("Unexpected response: " + response.statusCode() + " - " + response.body());
        }
    }
    public String getAssetByAddress(String address) throws IOException, InterruptedException {
        String url = "https://api.cardanoscan.io/api/v1/asset/list/byAddress?" + address;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apiKey", apiKey) // Add the API key to the request header
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body(); // Return the raw JSON response body
        } else {
            throw new IOException("Unexpected response: " + response.statusCode() + " - " + response.body());
        }
    }
    public String getDRepList(String pageNo) throws IOException, InterruptedException {
        String url = "https://api.cardanoscan.io/api/v1/governance/dRep/list?" + pageNo;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apiKey", apiKey) // Add the API key to the request header
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new IOException("Unexpected response: " + response.statusCode() + " - " + response.body());
        }
    }
    public String getTransationListByAddress(String address) throws IOException, InterruptedException {
        String url = "https://api.cardanoscan.io/api/v1/transaction/list?" + address;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apiKey", apiKey) // Add the API key to the request header
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new IOException("Unexpected response: " + response.statusCode() + " - " + response.body());
        }
    }
    public String getAddressAssociatedWithAStakeKey(String rewardAddress) throws IOException, InterruptedException {
        String url = "https://api.cardanoscan.io/api/v1/rewardAccount/addresses?" + rewardAddress;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apiKey", apiKey) // Add the API key to the request header
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new IOException("Unexpected response: " + response.statusCode() + " - " + response.body());
        }
    }
    public String getMemberList(String pageNo) throws IOException, InterruptedException {
        String url = "https://api.cardanoscan.io/api/v1/governance/committee/members?" + pageNo;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apiKey", apiKey) // Add the API key to the request header
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new IOException("Unexpected response: " + response.statusCode() + " - " + response.body());
        }
    }
}


