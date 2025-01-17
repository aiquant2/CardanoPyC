package org.intellij.sdk.language.cardanoApi.actions;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.intellij.sdk.language.cardanoApi.CardanoScanFetcher;

public class CardanoGovernanceCommitteeAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // Get the current project and trigger the fetch
        CardanoScanFetcher fetcher = new CardanoScanFetcher(e.getProject());

        // Trigger fetching the governance committee data
        fetcher.fetchGovernanceCommittee();
    }
}
