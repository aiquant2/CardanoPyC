// //package org.intellij.sdk.language.actions;
// //
// //import com.intellij.openapi.actionSystem.AnAction;
// //import com.intellij.openapi.actionSystem.AnActionEvent;
// //import com.intellij.openapi.ui.Messages;
// //import org.jetbrains.annotations.NotNull;
// //
// //public class GenerateAddressAction extends AnAction {
// //
// //    @Override
// //    public void actionPerformed(@NotNull AnActionEvent e) {
// //        try {
// //            // ✅ Run the JavaScript file and get the address
// //            String address = PlutusAddressGenerator.generateAddressFromPlutus();
// //
// //            // ✅ Show the result in a popup dialog
// //            Messages.showInfoMessage(address, "Generated Plutus Address");
// //
// //        } catch (Exception ex) {
// //            // ✅ Show error if the script fails
// //            Messages.showErrorDialog("Failed to generate address:\n" + ex.getMessage(), "Error");
// //        }
// //    }
// //}
//
// package org.intellij.sdk.language.actions;
//
// import com.intellij.openapi.actionSystem.AnAction;
// import com.intellij.openapi.actionSystem.AnActionEvent;
// import com.intellij.openapi.fileChooser.FileChooser;
// import com.intellij.openapi.fileChooser.FileChooserDescriptor;
// import com.intellij.openapi.project.Project;
// import com.intellij.openapi.ui.Messages;
// import com.intellij.openapi.vfs.VirtualFile;
// import org.jetbrains.annotations.NotNull;
//
// public class GenerateAddressAction extends AnAction {
//
//     @Override
//     public void actionPerformed(@NotNull AnActionEvent e) {
//         // File chooser for .plutus files
//         FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, false);
//         descriptor.setTitle("Select Plutus Script File");
//         descriptor.withFileFilter(file -> file.getName().endsWith(".plutus"));
//
//         VirtualFile file = FileChooser.chooseFile(descriptor, e.getProject(), null);
//         if (file == null) {
//             Messages.showWarningDialog("No file selected.", "Warning");
//             return;
//         }
//         Project project = e.getProject();
//         System.out.println(project);
//         try {
//             // Generate address using selected file
//             String address = PlutusAddressGenerator.generateAddressFromPlutus(file.getPath());
//             Messages.showInfoMessage(address, "Generated Plutus Address");
//         } catch (Exception ex) {
//             Messages.showErrorDialog("Failed to generate address:\n" + ex.getMessage(), "Error");
//         }
//     }
// }
package org.intellij.sdk.language.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class GenerateAddressAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            JOptionPane.showMessageDialog(null, "No project found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // File chooser for .plutus files
        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, false)
                .withTitle("Select Plutus Script File")
                .withFileFilter(file -> file.getExtension() != null && file.getExtension().equalsIgnoreCase("plutus"));

        VirtualFile file = FileChooser.chooseFile(descriptor, project, null);
        if (file == null) {
            JOptionPane.showMessageDialog(null, "No file selected.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Generate address using selected file
            String address = PlutusAddressGenerator.generateAddressFromPlutus(file.getPath());

            // Create a panel with the address and a copy button
            JPanel panel = new JPanel(new BorderLayout(10, 0));
            JTextField textField = new JTextField(address);
            textField.setEditable(false);
            textField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            panel.add(textField, BorderLayout.CENTER);

            // Add auto-select on focus
            textField.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                    textField.selectAll();
                }
            });

            JButton copyButton = new JButton("Copy");
            copyButton.addActionListener(event -> {
                StringSelection selection = new StringSelection(address);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
                copyButton.setText("Copied!");
            });
            panel.add(copyButton, BorderLayout.EAST);

            // Show the panel in a dialog
            JOptionPane.showMessageDialog(
                    null,
                    panel,
                    "Generated Plutus Address",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Failed to generate address:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
