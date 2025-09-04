package org.intellij.sdk.language.wallet;

import com.intellij.diagnostic.ActivityCategory;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.extensions.ExtensionsArea;
import com.intellij.openapi.extensions.PluginDescriptor;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.util.messages.MessageBus;
import kotlin.jvm.functions.Function0;
import kotlinx.coroutines.CoroutineScope;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.SystemIndependent;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

import static org.junit.Assert.*;

public class NetworkStatusWidgetFactoryTest {

    private NetworkStatusWidgetFactory factory;
    private Project dummyProject;
    private StatusBar dummyStatusBar;

    // ✅ Minimal stub for Project
    private static class DummyProject implements Project {
        @Override public String getName() { return "DummyProject"; }

        @Override
        public VirtualFile getBaseDir() {
            return null;
        }

        @Override
        public @Nullable @NonNls @SystemIndependent String getBasePath() {
            return "";
        }

        @Override
        public @Nullable VirtualFile getProjectFile() {
            return null;
        }

        @Override
        public @Nullable @NonNls @SystemIndependent String getProjectFilePath() {
            return "";
        }

        @Override
        public @Nullable VirtualFile getWorkspaceFile() {
            return null;
        }

        @Override
        public @NotNull @NonNls String getLocationHash() {
            return "";
        }

        @Override public boolean isOpen() { return true; }
        @Override public boolean isInitialized() { return true; }

        @Override
        public <T> T getComponent(@NotNull Class<T> aClass) {
            return null;
        }

        @Override
        public boolean hasComponent(@NotNull Class<?> aClass) {
            return false;
        }

        @Override
        public boolean isInjectionForExtensionSupported() {
            return false;
        }

        @Override
        public @NotNull MessageBus getMessageBus() {
            return null;
        }

        @Override
        public boolean isDisposed() {
            return false;
        }

        @Override
        public @NotNull Condition<?> getDisposed() {
            return null;
        }

        @Override public <T> T getService(Class<T> serviceClass) { return null; }

        @Override
        public @NotNull ExtensionsArea getExtensionArea() {
            return null;
        }

        @Override
        public <T> T instantiateClass(@NotNull Class<T> aClass, @NotNull PluginId pluginId) {
            return null;
        }

        @Override
        public <T> T instantiateClassWithConstructorInjection(@NotNull Class<T> aClass, @NotNull Object o, @NotNull PluginId pluginId) {
            return null;
        }

        @Override
        public @NotNull RuntimeException createError(@NotNull Throwable throwable, @NotNull PluginId pluginId) {
            return null;
        }

        @Override
        public @NotNull RuntimeException createError(@NotNull @NonNls String s, @NotNull PluginId pluginId) {
            return null;
        }

        @Override
        public @NotNull RuntimeException createError(@NotNull @NonNls String s, @Nullable Throwable throwable, @NotNull PluginId pluginId, @Nullable Map<String, String> map) {
            return null;
        }

        @Override
        public @NotNull <T> Class<T> loadClass(@NotNull String s, @NotNull PluginDescriptor pluginDescriptor) throws ClassNotFoundException {
            return null;
        }

        @Override
        public <T> @NotNull T instantiateClass(@NotNull String s, @NotNull PluginDescriptor pluginDescriptor) {
            return null;
        }

        @Override
        public @NotNull ActivityCategory getActivityCategory(boolean b) {
            return null;
        }

        @Override public void save() {}
        @Override public boolean isDefault() { return false; }

        @Override
        public void dispose() {

        }

        @Override
        public <T> @Nullable T getUserData(@NotNull Key<T> key) {
            return null;
        }

        @Override
        public <T> void putUserData(@NotNull Key<T> key, @Nullable T t) {

        }
    }

    // ✅ Minimal stub for StatusBar
    private static class DummyStatusBar implements StatusBar {
        @Override public void updateWidget(String id) {}
        @Override public void addWidget(StatusBarWidget widget) {}
        @Override public void addWidget(StatusBarWidget widget, String anchor) {}
        @Override public void removeWidget(String id) {}
        @Override public StatusBarWidget getWidget(String id) { return null; }
        @Override public void fireNotificationPopup(javax.swing.JComponent content, java.awt.Color backgroundColor) {}
        @Override public Project getProject() { return new DummyProject(); }

        @Override
        public @Nullable JComponent getComponent() {
            return null;
        }

        @Override
        public void addWidget(@NotNull StatusBarWidget statusBarWidget, @NotNull Disposable disposable) {

        }

        @Override
        public void addWidget(@NotNull StatusBarWidget statusBarWidget, @NotNull String s, @NotNull Disposable disposable) {

        }

        @Override
        public @Nullable StatusBar createChild(@NotNull CoroutineScope coroutineScope, @NotNull IdeFrame ideFrame, @NotNull Function0<? extends FileEditor> function0) {
            return null;
        }

        @Override
        public @Nullable StatusBar findChild(@NotNull Component component) {
            return null;
        }

        @Override
        public void startRefreshIndication(@Nullable String s) {

        }

        @Override
        public void stopRefreshIndication() {

        }

        @Override
        public @NotNull Function0<FileEditor> getCurrentEditor() {
            return null;
        }

        @Override
        public void setInfo(@NlsContexts.StatusBarText @Nullable String s) {

        }

        @Override
        public void setInfo(@NlsContexts.StatusBarText @Nullable String s, @NonNls @Nullable String s1) {

        }

        @Override
        public @NlsContexts.StatusBarText String getInfo() {
            return "";
        }
    }

    @Before
    public void setUp() {
        factory = new NetworkStatusWidgetFactory();
        dummyProject = new DummyProject();
        dummyStatusBar = new DummyStatusBar();
    }

    @Test
    public void testGetId() {
        assertEquals("CardanoNetworkStatus", factory.getId());
    }

    @Test
    public void testGetDisplayName() {
        assertEquals("Cardano Network Status", factory.getDisplayName());
    }

    @Test
    public void testCreateWidgetReturnsNotNull() {
        StatusBarWidget widget = factory.createWidget(dummyProject);
        assertNotNull(widget);
        // Don’t assert instanceof NetworkStatusWidget unless you have that class available
    }

    @Test
    public void testCanBeEnabledOnAlwaysTrue() {
        assertTrue(factory.canBeEnabledOn(dummyStatusBar));
    }

    @Test
    public void testDisposeWidgetDoesNotThrow() {
        StatusBarWidget widget = factory.createWidget(dummyProject);
        factory.disposeWidget(widget); // should not throw
    }
}
