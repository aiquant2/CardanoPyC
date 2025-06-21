
 package org.intellij.sdk.language.actions;

 import org.intellij.sdk.language.wallet.WalletApiKeyState;

 import java.io.*;
 import java.net.URL;

 public class PlutusAddressGenerator {

     public static String generateAddressFromPlutus(String scriptArgument) throws IOException, InterruptedException {
         // Load JS script from resources
         URL resource = PlutusAddressGenerator.class.getClassLoader().getResource("scripts/generateAddress.js");
         System.out.println(resource);
         if (resource == null) {
             throw new FileNotFoundException("Could not find generateAddress.js in resources/scripts/");
         } else {
             String resourcePath = resource.toString();

             // Remove "jar:file:" prefix
             String withoutJarPrefix = resourcePath.replaceFirst("^jar:file:", "");

             // Find index of /build/
             int buildIndex = withoutJarPrefix.indexOf("/build/");
             if (buildIndex == -1) {
                 throw new RuntimeException("Path does not contain /build/");
             }

             // Extract project root
             String projectRoot = withoutJarPrefix.substring(0, buildIndex);

             // Build the correct path
             String desiredPath = projectRoot + "/src/main/resources/scripts/generateAddress.js";

             File jsScriptFile = new File(desiredPath);

             if (!jsScriptFile.exists()) {
                 throw new FileNotFoundException("Script not found at: " + desiredPath);
             }

             // Run the JS script using Node.js

             String apiKey = WalletApiKeyState.getInstance().getApiKey(); // Securely retrieved
             String network = WalletApiKeyState.getInstance().getNetwork(); // e.g., Preprod
             ProcessBuilder pb = new ProcessBuilder("node", jsScriptFile.getAbsolutePath(), scriptArgument, apiKey, network);
             pb.redirectErrorStream(true);

             Process process = pb.start();

             BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
             StringBuilder output = new StringBuilder();
             String line;

             while ((line = reader.readLine()) != null) {
                 output.append(line).append("\n");
             }

             int exitCode = process.waitFor();
             String result = output.toString().trim();


             if (exitCode != 0 || result.isEmpty() || !result.startsWith("addr_")) {
                 throw new RuntimeException("Script error or invalid address:\n" + result);
             }

             return result;
         }
     }
 }