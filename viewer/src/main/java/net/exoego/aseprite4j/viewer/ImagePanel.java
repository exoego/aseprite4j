package net.exoego.aseprite4j.viewer;

import net.exoego.aseprite4j.AsepriteFile;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImagePanel extends JPanel {
    private BufferedImage image;
    private String errorMessage;

    public ImagePanel() {
        setBackground(Color.DARK_GRAY);
    }

    public void loadImage(File file) {
        if (file == null || !file.exists()) {
            errorMessage = "File does not exist: " + file;
            image = null;
            repaint();
            return;
        }

        try {
            String name = file.getName().toLowerCase();
            if (name.endsWith(".aseprite") || name.endsWith(".ase")) {
                var aseFile = AsepriteFile.read(file.toPath());
                image = AsepriteRenderer.render(aseFile);
            } else {
                image = ImageIO.read(file);
            }
            errorMessage = null;
        } catch (Exception e) {
            errorMessage = "Error loading image: " + e.getMessage();
            image = null;
            e.printStackTrace();
        }
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        var g2d = (Graphics2D) g;

        if (errorMessage != null) {
            g2d.setColor(Color.RED);
            g2d.drawString(errorMessage, 20, 30);
            return;
        }

        if (image != null) {
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

            int scale = calculateScale(image.getWidth(), image.getHeight());
            int scaledWidth = image.getWidth() * scale;
            int scaledHeight = image.getHeight() * scale;

            int x = (getWidth() - scaledWidth) / 2;
            int y = (getHeight() - scaledHeight) / 2;

            drawCheckerboard(g2d, x, y, scaledWidth, scaledHeight);
            g2d.drawImage(image, x, y, scaledWidth, scaledHeight, null);
        }
    }

    private int calculateScale(int imgWidth, int imgHeight) {
        int maxScale = 16;
        int scaleX = Math.max(1, (getWidth() - 40) / imgWidth);
        int scaleY = Math.max(1, (getHeight() - 40) / imgHeight);
        return Math.min(Math.min(scaleX, scaleY), maxScale);
    }

    private void drawCheckerboard(Graphics2D g2d, int x, int y, int width, int height) {
        int cellSize = 8;
        var light = new Color(200, 200, 200);
        var dark = new Color(150, 150, 150);

        for (int row = 0; row < (height + cellSize - 1) / cellSize; row++) {
            for (int col = 0; col < (width + cellSize - 1) / cellSize; col++) {
                g2d.setColor((row + col) % 2 == 0 ? light : dark);
                int cellX = x + col * cellSize;
                int cellY = y + row * cellSize;
                int cellW = Math.min(cellSize, x + width - cellX);
                int cellH = Math.min(cellSize, y + height - cellY);
                g2d.fillRect(cellX, cellY, cellW, cellH);
            }
        }
    }
}
