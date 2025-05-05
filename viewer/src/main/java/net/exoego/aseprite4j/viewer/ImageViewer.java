package net.exoego.aseprite4j.viewer;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

public class ImageViewer extends JFrame {
    public ImageViewer() {
        final var viewer = this;
        final var imagePanel = new ImagePanel();
        viewer.add(imagePanel);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);

        var buttonOpen = new JButton("Open...");
        buttonOpen.addActionListener((ActionEvent e) -> {
            var fileChooser = new JFileChooser();
            fileChooser.showOpenDialog(viewer);

            var selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected file: " + selectedFile);

            imagePanel.loadImage(selectedFile);
        });
        viewer.add(buttonOpen, BorderLayout.PAGE_START);
    }
}
