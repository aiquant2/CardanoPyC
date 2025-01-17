package org.intellij.sdk.language.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import com.intellij.openapi.application.ApplicationManager;
import org.intellij.sdk.language.HaskellFileType;

public class CreateHaskellFileAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        // Check for an active editor
        if (FileEditorManager.getInstance(project).getSelectedEditors().length == 0) {
            Messages.showErrorDialog(project, "No editor is currently open!", "Error");
            return;
        }

        // Get the current file and its parent directory
        VirtualFile currentFile = FileEditorManager.getInstance(project).getSelectedEditor().getFile();
        if (currentFile == null) {
            Messages.showErrorDialog(project, "No file selected!", "Error");
            return;
        }

        VirtualFile currentDirectory = currentFile.isDirectory() ? currentFile : currentFile.getParent();
        if (currentDirectory == null) {
            Messages.showErrorDialog(project, "Could not find the parent directory!", "Error");
            return;
        }

        // Ask for the file name
        String fileName = Messages.showInputDialog(project, "Enter Haskell File Name:", "Create New Haskell File", Messages.getQuestionIcon());
        if (fileName == null || fileName.trim().isEmpty()) {
            return;
        }

        // Validate the file name (basic check)
        if (!fileName.matches("[a-zA-Z0-9_]+")) { // Adjust regex as needed
            Messages.showErrorDialog(project, "Invalid file name!", "Error");
            return;
        }

        // Create the new file inside a write action
        try {
            ApplicationManager.getApplication().runWriteAction(() -> {
                PsiDirectory psiDirectory = PsiManager.getInstance(project).findDirectory(currentDirectory);
                if (psiDirectory == null) {
                    Messages.showErrorDialog(project, "Could not find directory!", "Error");
                    return;
                }

                // Use PsiFileFactory to create an empty file
                PsiFileFactory fileFactory = PsiFileFactory.getInstance(project);
                String fileContent = ""; // Empty content for the new file

                // Create the file and add it to the directory
                PsiFile newFile = fileFactory.createFileFromText(fileName + ".hs", HaskellFileType.INSTANCE, fileContent);

                // Add the new file to the PsiDirectory
                PsiElement addedElement = psiDirectory.add(newFile);
                if (addedElement instanceof PsiFile) {
                    PsiFile addedFile = (PsiFile) addedElement;
                    VirtualFile virtualFile = addedFile.getVirtualFile();
                    if (virtualFile != null) {
                        // Open the file in the editor
                        FileEditorManager.getInstance(project).openFile(virtualFile, true);
                    } else {
                        Messages.showErrorDialog(project, "Failed to get virtual file for the newly created Haskell file.", "Error");
                    }
                } else {
                    Messages.showErrorDialog(project, "Failed to add the file to the directory.", "Error");
                }
            });

            // Optional success message
            // Messages.showMessageDialog(project, "Haskell file created successfully!", "Success", Messages.getInformationIcon());
        } catch (IncorrectOperationException ex) {
            Messages.showErrorDialog(project, "Failed to create Haskell file: " + ex.getMessage(), "Error");
        } catch (Exception ex) {
            Messages.showErrorDialog(project, "An unexpected error occurred: " + ex.getMessage(), "Error");
        }
    }
}