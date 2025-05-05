package net.exoego.aseprite4j.viewer;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;


public class ImageViewer extends JFrame {

    final ImagePanel imagePanel;

    public ImageViewer() {
        final var viewer = this;
        imagePanel = new ImagePanel();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);


        var buttonOpen = new JButton("Open...");
        buttonOpen.addActionListener((ActionEvent e) -> {
            var fileChooser = new JFileChooser();
            fileChooser.showOpenDialog(viewer);

            var selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected file: " + selectedFile);
            if (selectedFile != null) {
                imagePanel.loadImage(selectedFile);
                imagePanel.paintComponent(viewer.getGraphics());
            }
        });

        getContentPane().add(buttonOpen, BorderLayout.PAGE_START);
    }
}
