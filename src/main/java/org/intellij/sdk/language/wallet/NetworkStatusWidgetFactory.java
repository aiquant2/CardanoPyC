
package org.intellij.sdk.language.wallet;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidgetFactory;
import org.jetbrains.annotations.NotNull;

public class NetworkStatusWidgetFactory implements StatusBarWidgetFactory {

    @Override
    public @NotNull String getId() {
        return "CardanoNetworkStatus";
    }

    @Override
    public @NotNull String getDisplayName() {
        return "Cardano Network Status";
    }

    @Override
    public boolean isAvailable(@NotNull Project project) {
        return true;
    }

    @Override
    public @NotNull StatusBarWidget createWidget(@NotNull Project project) {
        return new NetworkStatusWidget(project);
    }


    @Override
    public void disposeWidget(@NotNull StatusBarWidget widget) {}

    @Override
    public boolean canBeEnabledOn(@NotNull StatusBar statusBar) {
        return true;
    }
}
