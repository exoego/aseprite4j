package net.exoego.aseprite4j.viewer;

import javax.swing.JPanel;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.net.MalformedURLException;

public class ImagePanel extends JPanel {
    private Image image;

    public void loadImage(File file) {
        if (file == null || !file.exists()) {
            System.out.println("File does not exist: " + file);
            return;
        }
        try {
            this.image = Toolkit.getDefaultToolkit().getImage(file.toURI().toURL());
        } catch (MalformedURLException e) {
            System.out.println("Error loading image: " + e.getMessage());
        }
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 20, 20, this);
            System.out.println("Image painted");
        }
    }
}
