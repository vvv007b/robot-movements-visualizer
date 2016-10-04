package com.robot.group.paths.finding;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Hypervisor {

    private static List<Robot> robots = new ArrayList<>();

    private static boolean logging = false;


    public Hypervisor(List<Robot> robots) {
        Hypervisor.robots = robots;
    }

    public static void sendMapChangedSignal(boolean value) {
        for (Robot r : robots) {
            r.setMapChangedSignal(value);
        }
    }

    // may be need to synchronize
    public static Robot checkRobotsOnWay(Robot robot, Link link, double passed) {
        for (Robot r : robots) {
            if (r != robot) {
                if (Link.isSegmentsBlockedByRobot(link.getSegments(), new Point((int) r.getX(), (int) r.getY()),
                        r.getAzimuth(), robot.getMap().getScale(), 0, passed)) {
                    return r;
                }
            }
        }
        return null;
    }

    public static Robot checkRobotsOnWay(Robot robot, List<Link> links, double passed) {
        if (links.isEmpty()) {
            return null;
        }
        Link currentLink = links.get(0);

        for (Robot r : robots) {
            if (r != robot) {
                for (Link link : links) {
                    if (link == currentLink) {
                        if (Link.isSegmentsBlockedByRobot(link.getSegments(), new Point((int) r.getX(), (int) r.getY()),
                                r.getAzimuth(), robot.getMap().getScale(), 0, passed)) {
                            return r;
                        }
                    } else {
                        if (Link.isSegmentsBlockedByRobot(link.getSegments(), new Point((int) r.getX(), (int) r.getY()),
                                r.getAzimuth(), robot.getMap().getScale(), 0, 0)) {
                            return r;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static Robot checkRobotInCoordinates(Point point, int robotSize, Robot robot) {
        for (Robot r : robots) {
            if (r != robot) {
                if (Math.abs(r.getX() - point.x) <= robotSize && Math.abs(r.getY() - point.y) <= robotSize) {
                    return r;
                }
            }
        }
        return null;
    }


    public static synchronized boolean isPointOccupiedAsFinish(Point2D point, Robot exeption) {
        for (Robot robot : robots) {
            if (robot != exeption) {
                Node destination = robot.getRealDestination();
                if (destination != null) {
                    if (destination.getX() == point.getX() && destination.getY() == point.getY()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void startLog() {
        logging = true;
        Thread thread = new Thread(() -> {
            // create files
            PrintWriter[] writers = new PrintWriter[robots.size()];
            for (int i = 0; i < robots.size(); ++i) {
                try {
                    writers[i] = new PrintWriter("robot_" + i);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < robots.size(); ++i) {
                Robot robot = robots.get(i);
                writers[i].println(robot.getMap().getWidth() + " " + robot.getMap().getHeight());
                ++i;
            }
            // while not stopped - log MOVING robots
            while (logging) {
                for (int i = 0; i < robots.size(); ++i) {
                    Robot robot = robots.get(i);
//                    i=0;
//                    for(Map.Entry<Robot, RobotParameters> entry:robots.entrySet()) {
//                        Robot robot=entry.getKey();
                    if (robot.getSpeed() > 0) {
                        writers[i].println((int) robot.getX() + " " + (int) robot.getY());
                    }
                    ++i;
                }
                try {
                    Thread.sleep(25);        //40 fps
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // close all files
            for (PrintWriter w : writers) {
                w.close();
            }
        });
        thread.start();
    }

    public static void stopLog() {
        logging = false;
    }


    // returns {width, height}
    public static int[] getMapSize() {
        if (robots.size() == 0) {
            return null;
        }
        MapInfo map = robots.get(0).getMap();
        //MapInfo map=robots.entrySet().iterator().next().getKey().getMap();
        if (map.getImage() == null) {
            return null;
        }
        int[] result = new int[2];
        result[0] = map.getWidth();
        result[1] = map.getHeight();
        return result;
    }

    public static Image getMapImage() {
        if (robots.size() == 0) {
            return null;
        }
        //return robots.entrySet().iterator().next().getKey().getMap().getImage();
        return robots.get(0).getMap().getImage();
    }

    public static ArrayList<double[]> getAllCoordinates() {
        return (ArrayList<double[]>) robots.stream().map(Robot::getCachedCoordinates).collect(Collectors.toList());
    }

    public static ArrayList<Double> getSpeeds() {
        return (ArrayList<Double>) robots.stream().map(Robot::getCachedSpeed).collect(Collectors.toList());
    }

    public static ArrayList<Long> getUpdateTimes() {
        return (ArrayList<Long>) robots.stream().map(Robot::getUpdateTime).collect(Collectors.toList());
    }
}
