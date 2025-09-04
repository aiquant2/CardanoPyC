package org.intellij.sdk.language.deployment;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.testFramework.TestActionEvent;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class CardanoCliActionTest extends BasePlatformTestCase {   // FIXED NAME

    public void testUpdateSetsEnabledWhenProjectExists() {
        CardanoCliAction action = new CardanoCliAction();
        AnActionEvent event = createTestEvent(getProject());

        action.update(event);

        assertTrue("Action should be enabled when project exists",
                event.getPresentation().isEnabled());
        assertTrue("Action should be visible when project exists",
                event.getPresentation().isVisible());
    }

    public void testGetNetworkFlagPreview() {
        CardanoCliAction action = new CardanoCliAction();
        String flag = invokePrivateGetNetworkFlag(action, "preview");
        assertEquals("--testnet-magic 2", flag);
    }

    public void testGetNetworkFlagPreprod() {
        CardanoCliAction action = new CardanoCliAction();
        String flag = invokePrivateGetNetworkFlag(action, "preprod");
        assertEquals("--testnet-magic 1", flag);
    }

    public void testGetNetworkFlagMainnet() {
        CardanoCliAction action = new CardanoCliAction();
        String flag = invokePrivateGetNetworkFlag(action, "mainnet");
        assertEquals("--mainnet", flag);
    }

    public void testGetNetworkFlagInvalid() {
        CardanoCliAction action = new CardanoCliAction();
        String flag = invokePrivateGetNetworkFlag(action, "invalid");
        assertNull(flag);
    }

    // --- Helpers ---

    private String invokePrivateGetNetworkFlag(CardanoCliAction action, String type) {
        try {
            var method = CardanoCliAction.class.getDeclaredMethod("getNetworkFlag", String.class);
            method.setAccessible(true);
            return (String) method.invoke(action, type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private AnActionEvent createTestEvent(Project project) {
        var dataContext = SimpleDataContext.builder()
                .add(PlatformDataKeys.PROJECT, project)
                .build();

        return TestActionEvent.createTestEvent(dataContext);
    }
}


