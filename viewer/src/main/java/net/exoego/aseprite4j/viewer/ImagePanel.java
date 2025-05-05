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
        try {
            this.image = Toolkit.getDefaultToolkit()
                    .getImage(file.toURI().toURL());
        } catch (MalformedURLException e) {
            System.out.println("Error loading image: " + e.getMessage());
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, this);

        }
    }
}
