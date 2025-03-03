package org.intellij.sdk.language.wallet;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

class BackgroundImagePanel extends JPanel {
    private Image backgroundImage;

    public BackgroundImagePanel(String imagePath) {
        try {
            backgroundImage = new ImageIcon(Objects.requireNonNull(getClass().getResource(imagePath))).getImage();
        } catch (Exception e) {
            System.err.println("Image not found: " + imagePath);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            System.err.println("Background image is null!");
        }
    }
}

