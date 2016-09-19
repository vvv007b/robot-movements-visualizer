package ru.mcst.robotGroup.paths.finding;

import ru.mcst.robotGroup.paths.linking.PathsLinkingGUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Deprecated
class main_class  {

    public main_class() {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            if(args.length == 0) {
                Surface surface = new Surface();
                PathsFindingGUI pathsFindingGUI = new PathsFindingGUI(surface);
                pathsFindingGUI.setVisible(true);
                PathsLinkingGUI pathsLinkingGUI = new PathsLinkingGUI();
                pathsLinkingGUI.setVisible(true);
            }
            else{
                System.out.println("Text mode");
                Surface surface = new Surface();
                try{
                    int size = Integer.parseInt(args[0]);
                    System.out.println("Calculating passability...");
                    try {
                        BufferedImage image = ImageIO.read(new File("Maps/small.png"));
                        surface.setMapImages(image);
//                        System.out.println(surface.getRobot().getRadius());
//                        System.out.println(size);
                        surface.getRobot().setRadius(size);
                        surface.getRobot().getMap().setScale(size);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    surface.calculatePassabilitySilent(size);

                    System.out.println("Path finding...");

                    if (surface.placeRobotSilent(size / 2 + 1,size / 2 + 1) == 0) System.out.println("Incorrect start");
                    if (surface.setFinishSilent(surface.getRobot().getMap().getWidth()-100,surface.getRobot().getMap().getHeight()-100) == 0)
                        System.out.println("Incorrect finish");
//                    SearchAlgorithm searchAlgorithm = new SearchAlgorithm();
//                    searchAlgorithm.searchAStar()

                }
                catch (NumberFormatException e){
                    System.out.println("Usage: size");
                }
            }
        });
    }
}