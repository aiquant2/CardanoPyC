

package org.intellij.sdk.language.wallet;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.StatusBarWidget.Multiframe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import com.intellij.util.Consumer;


public class NetworkStatusWidget implements StatusBarWidget, Multiframe {

    private final Project project;

    public NetworkStatusWidget(Project project) {
        this.project = project;
    }

    @NotNull
    @Override
    public String ID() {
        return "CardanoNetworkStatus";
    }

    @Nullable
    @Override
    public TextPresentation getPresentation() {
        return new NetworkStatusPresentation(project);
    }

    @Override
    public void install(@NotNull StatusBar statusBar) {
        // Optional: Any logic to run when the widget is added
    }

    @Override
    public void dispose() {
        // Optional: Clean up resources
    }

    @Override
    public @NotNull StatusBarWidget copy() {
        return new NetworkStatusWidget(project);
    }

    private static class NetworkStatusPresentation implements StatusBarWidget.TextPresentation {
        private final Project project;

        public NetworkStatusPresentation(Project project) {
            this.project = project;
        }

        @NotNull
        @Override
        public String getText() {
            String network = WalletApiKeyState.getInstance().getNetwork();
            return "🌐 Network: " + (network == null || network.isEmpty() ? "Not Set" : network);
        }

        @Nullable
        @Override
        public String getTooltipText() {
            return "Click to switch Cardano network";
        }

        @Nullable
        @Override
        public Consumer<MouseEvent> getClickConsumer() {
            return event -> {
                JPopupMenu menu = new JPopupMenu();
                String[] networks = {"mainnet", "preprod", "preview" };

                for (String net : networks) {
                    JMenuItem item = new JMenuItem(net);
                    item.addActionListener(e -> {
                        String input = JOptionPane.showInputDialog(
                                null,
                                "Enter Blockfrost API key for " + net + ":",
                                "API Key Input",
                                JOptionPane.PLAIN_MESSAGE
                        );

                        if (input != null && !input.trim().isEmpty()) {
                            // Validate API Key
                            boolean isValid = BlockfrostApiValidator.validate(input, net);
                            if (isValid) {
                                WalletApiKeyState.getInstance().setNetwork(net);
                                WalletApiKeyState.getInstance().setApiKey(input);

                                StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
                                if (statusBar != null) {
                                    statusBar.updateWidget("CardanoNetworkStatus");
                                }

                                JOptionPane.showMessageDialog(
                                        null,
                                        "✅ Successfully connected to " + net,
                                        "Network Connected",
                                        JOptionPane.INFORMATION_MESSAGE
                                );
                            } else {
                                JOptionPane.showMessageDialog(
                                        null,
                                        "❌ Invalid API key for " + net,
                                        "Connection Failed",
                                        JOptionPane.ERROR_MESSAGE
                                );
                            }
                        }
                    });
                    menu.add(item);
                }

                Component component = event.getComponent();
                if (component != null) {
                    menu.show(component, event.getX(), event.getY());
                }
            };
        }

        @Override
        public float getAlignment() {
            return Component.LEFT_ALIGNMENT;
        }
    }
}