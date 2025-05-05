package net.exoego.aseprite4j.viewer;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, Aseprite Viewer!");

        var viewer = new ImageViewer();
        viewer.setVisible(true);
        if (args.length > 0) {
            viewer.imagePanel.loadImage(new File(args[0]));
        }
    }
}
