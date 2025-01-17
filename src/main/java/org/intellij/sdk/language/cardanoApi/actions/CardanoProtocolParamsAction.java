package org.intellij.sdk.language.cardanoApi.actions;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.intellij.sdk.language.cardanoApi.CardanoScanFetcher;

public class CardanoProtocolParamsAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        CardanoScanFetcher fetcher = new CardanoScanFetcher(e.getProject());

        fetcher.fetchProtocolParams();
    }
}

