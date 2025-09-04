package org.intellij.sdk.language.wallet;

import com.bloxbean.cardano.client.account.Account;
import com.bloxbean.cardano.client.api.ProtocolParamsSupplier;
import com.bloxbean.cardano.client.api.UtxoSupplier;
import com.bloxbean.cardano.client.api.model.Result;
import com.bloxbean.cardano.client.backend.api.BackendService;
import com.bloxbean.cardano.client.backend.api.DefaultProtocolParamsSupplier;
import com.bloxbean.cardano.client.backend.api.DefaultUtxoSupplier;
import com.bloxbean.cardano.client.backend.blockfrost.common.Constants;
import com.bloxbean.cardano.client.backend.blockfrost.service.BFBackendService;
import com.bloxbean.cardano.client.cip.cip20.MessageMetadata;
import com.bloxbean.cardano.client.common.model.Networks;
import com.bloxbean.cardano.client.function.Output;
import com.bloxbean.cardano.client.function.TxBuilder;
import com.bloxbean.cardano.client.function.TxBuilderContext;
import com.bloxbean.cardano.client.transaction.spec.Transaction;
import static com.bloxbean.cardano.client.common.ADAConversionUtil.adaToLovelace;
import static com.bloxbean.cardano.client.common.CardanoConstants.LOVELACE;
import static com.bloxbean.cardano.client.function.helper.AuxDataProviders.metadataProvider;
import static com.bloxbean.cardano.client.function.helper.BalanceTxBuilders.balanceTx;
import static com.bloxbean.cardano.client.function.helper.InputBuilders.createFromSender;
import static com.bloxbean.cardano.client.function.helper.SignerProviders.signerFrom;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class SendAdaDialog extends DialogWrapper {
    protected JTextField recipientField;
    protected JTextField amountField;

    public SendAdaDialog() {
        super(true);
        setTitle("Send ADA");
        init();
    }

    @Override
    protected JComponent createCenterPanel() {

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        panel.add(new JLabel("Recipient Address:"));
        recipientField = new JTextField();
        panel.add(recipientField);

        panel.add(new JLabel("Amount (ADA):"));
        amountField = new JTextField();
        panel.add(amountField);

        return panel;
    }
    @Override
    protected void doOKAction() {
        String recipient = recipientField.getText();
        String amount = amountField.getText();

        if (recipient.isEmpty() || amount.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            transfer(recipient, Double.parseDouble(amount));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Transaction failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        close(OK_EXIT_CODE);
    }
    private void transfer(String recipientAddress, double amount) throws Exception {
        String senderMnemonic =  SecureStorageUtil.retrieveCredential("wallet_mnemonic");
        System.out.println(senderMnemonic);
        String network = WalletApiKeyState.getInstance().getNetwork();
        System.out.println(network);
        Account sender;
        switch (network) {
            case "preprod" -> sender=new Account(Networks.preprod(), senderMnemonic);
            case "mainnet" ->sender=new Account(Networks.mainnet(), senderMnemonic);
            default -> sender=new Account(Networks.preview(), senderMnemonic);
        };
        Account senderAccount = sender;
        String senderAddress = senderAccount.baseAddress();
        BackendService backendService = getBackendService();

        Output output = Output.builder()
                .address(recipientAddress)
                .assetName(LOVELACE)
                .qty(adaToLovelace(amount))
                .build();
        MessageMetadata metadata = MessageMetadata.create()
                .add("Sending " + amount + " ADA");
        TxBuilder txBuilder = output.outputBuilder()
                .buildInputs(createFromSender(senderAddress, senderAddress))
                .andThen(metadataProvider(metadata))
                .andThen(balanceTx(senderAddress, 1));

        UtxoSupplier utxoSupplier = new DefaultUtxoSupplier(backendService.getUtxoService());
        ProtocolParamsSupplier protocolParamsSupplier = new DefaultProtocolParamsSupplier(backendService.getEpochService());
        Transaction signedTransaction = TxBuilderContext.init(utxoSupplier, protocolParamsSupplier)
                .buildAndSign(txBuilder, signerFrom(senderAccount));
        Result<String> result = backendService.getTransactionService().submitTransaction(signedTransaction.serialize());
       JOptionPane.showMessageDialog(null, "Transaction Hash: " + result.getValue(), "Transaction Sent", JOptionPane.INFORMATION_MESSAGE);
    }

    private static @NotNull BackendService getBackendService() {
        String bf_projectId = WalletApiKeyState.getInstance().getApiKey();
        String network = WalletApiKeyState.getInstance().getNetwork();
        System.out.println(network);
        String state = switch (network) {
            case "preview" -> Constants.BLOCKFROST_PREVIEW_URL;
            case "preprod" -> Constants.BLOCKFROST_PREPROD_URL;
            case "mainnet" -> Constants.BLOCKFROST_MAINNET_URL;
            default -> "";
        };

        return new BFBackendService(state, bf_projectId);
    }
}
