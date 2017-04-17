package com.robot.paths.finding;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Random;

/**
 * Created by bocharov_n on  07.10.16.
 */
class GlobalTime extends Thread {

//    private static double RAIN_CHANCE = 0.001;
    private static final int WEATHER_SLEEP_TIME = 1000;
    private static final int NORMAL_SLEEP_TIME = 20;


    private static boolean isTimeRuns;
    private static boolean isAlive;

    public static final int NORMAL_WEATHER = 0;
    public static final int RAIN_WEATHER = 1;
    public static final int DRY_WEATHER = 2;

    private static int weather = GlobalTime.NORMAL_WEATHER;
    private final Surface surface;

    private Random random;

    public GlobalTime(Surface surface) {
        isTimeRuns = false;
        isAlive = true;
        random = new Random();
        this.surface = surface;
    }

    @Override
    public void run() {
        while (isAlive) {
            if (isTimeRuns) {
                BufferedImage realityMap = surface.getRobot().getMap().getRealityMap();
                //some changes with reality map
//                System.out.println("check");
                switch (weather) {
                    case RAIN_WEATHER:
                        //reality update
//                        byte[][] realityArray = surface.getRobot().getMap().getRealityArray();
                        synchronized (surface.getRobot().getMap().realityMapLock) {
                            Point[] points = {new Point(-1, -1), new Point(1, -1), new Point(1, 1), new Point(-1, 1)};
                            fillCells(realityMap, points, MapColors.PUDDLE_COLOR, 0.5);
                        }
                        for (Robot robot:Hypervisor.getRobots()) {
                            robot.getMap().setRealityMap(realityMap);
                        }
                        try {
                            sleep(WEATHER_SLEEP_TIME);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        break;
                    case DRY_WEATHER:
                        //reality update
                        synchronized (surface.getRobot().getMap().realityMapLock) {
                            fillCells(realityMap, new Point[]{new Point(0,0)}, MapColors.GRAY_COLOR, 0.2);
                        }
                        for (Robot robot:Hypervisor.getRobots()) {
                            robot.getMap().setRealityMap(realityMap);
                        }
                        try {
                            sleep(WEATHER_SLEEP_TIME);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        break;
                    case NORMAL_WEATHER:
                        try {
                            sleep(NORMAL_SLEEP_TIME);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        break;
                    default:
                        try {
                            sleep(NORMAL_SLEEP_TIME);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        break;
                }
                surface.repaint();
            } else {
                try {
                    sleep(NORMAL_SLEEP_TIME);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

        }
    }

    private void fillCells(BufferedImage realityMap, Point[] points, Color color, double probability) {
        Graphics2D graphics2D = realityMap.createGraphics();
        graphics2D.setColor(color);
        for (int i = 0; i < realityMap.getWidth(); i++) {
            for (int j = 0; j < realityMap.getHeight(); j++) {
                if (Objects.equals(new Color(realityMap.getRGB(i, j)), MapColors.PUDDLE_COLOR)) {
//                    System.out.println("draw");
                    //with probability 0.5 fluid nearby point
                    if (random.nextDouble() < probability) {
                        for (Point point:points) {
                            graphics2D.fillRect(i + point.x, j + point.y, 1, 1);
                        }
                    }
                }
            }
        }
        graphics2D.dispose();
    }

    public static boolean isTimeRuns() {
        return isTimeRuns;
    }

    public static void setTimeRuns(boolean timeRuns) {
        isTimeRuns = timeRuns;
    }

//    public static void killTime() {
//        isAlive = false;
//    }

    public static void setWeather(int newWeather) {
        weather = newWeather;
    }



}
