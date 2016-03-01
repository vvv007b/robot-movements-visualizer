package ru.mcst.RobotGroup.PathsFinding;

import java.awt.geom.Line2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sapachev_i on 12/9/15.
 */
public class Hypervisor {
//    private static class RobotPair {
//        private Robot bypasing;
//        private Robot ignoring;
//
//        public RobotPair(Robot bypasing, Robot ignoring) {
//            this.bypasing = bypasing;
//            this.ignoring = ignoring;
//        }
//
//        public Robot getBypasing() {
//            return bypasing;
//        }
//
//        public void setBypasing(Robot bypasing) {
//            this.bypasing = bypasing;
//        }
//
//        public Robot getIgnoring() {
//            return ignoring;
//        }
//
//        public void setIgnoring(Robot ignoring) {
//            this.ignoring = ignoring;
//        }
//        public boolean hasRobot(Robot robot) {
//            return (bypasing==robot || ignoring==robot);
//        }
//        public boolean hasPair(Robot r1, Robot r2) {
//            return (this.bypasing==r1 && this.ignoring==r2) || (this.bypasing==r2 && this.ignoring==r1);
//        }
//    }

    private static List<Robot> robots=new ArrayList<Robot>();
    //private static List<RobotPair> pairs=new ArrayList<RobotPair>();

    //private static HashMap<Robot, Robot> pairs=new HashMap<Robot,Robot>();
    private static boolean logging=false;

    public Hypervisor() {
    }
    public Hypervisor(List<Robot> robots) {
        this.robots=robots;
    }
    //public void stopHypervisor() {stop=true;}
    public static void sendMapChangedSignal(boolean value) {
        for(Robot r:robots) {
            r.setMapChangedSignal(value);
        }
    }
//    public static Robot checkRobotsOnWay(Robot robot) {
//        int robotSize = robot.getMap().getScale()/2;
//        int dx = robot.getSensorsRange(), dy = (robotSize - 2) - robotSize / 3;
//        /*
//        xy[0]				xy[1]
//        center
//        xy[2]				xy[3]
//        */
//        int x[] = { 0, dx, 0, dx};
//        int y[] = { dy, dy, -dy, -dy};
//
//        double cos = Math.cos(robot.getAzimuth()), sin = Math.sin(robot.getAzimuth());
//
//        for (int i = 0; i < 4; ++i) {
//            dx = x[i]; dy = y[i];
//            x[i] = (int)robot.getX() + (int) (cos * dx + sin * dy);
//            y[i] = (int)robot.getY() + (int) (cos * dy - sin * dx);
//        }
//
//        for(Robot r: robots) {
//            if(r!=robot) {
//                int dx2 = robotSize - 2, dy2 = dx2 - robotSize / 3;
//                /*
//                xy[1]				xy[0]
//                        center
//                xy[2]				xy[3]
//                 */
//                int x2[] = { dx2, -dx2, -dx2, dx2};
//                int y2[] = { dy2, dy2, -dy2, -dy2};
//
//                double cos2 = Math.cos(r.getAzimuth()), sin2 = Math.sin(r.getAzimuth());;
//
//                for (int i = 0; i < 4; ++i) {
//                    dx2 = x2[i]; dy2 = y2[i];
//                    x2[i] = (int)r.getX() + (int) (cos2 * dx2 + sin2 * dy2);
//                    y2[i] = (int)r.getY() + (int) (cos2 * dy2 - sin2 * dx2);
//                }
//
//                for(int i=0; i<4; i+=2) {
//                    for(int j=0; j<3; ++j) {
//                        if(Line2D.linesIntersect(x[i],y[i],x[i+1],y[i+1],x2[j],y2[j],x2[j+1],y2[j+1])) {
//                            return r;
//                        }
//                    }
//                    if(Line2D.linesIntersect(x[i],y[i],x[i+1],y[i+1],x2[3],y2[3],x2[0],y2[0])) {
//                        return r;
//                    }
//                }
//            }
//        }
//        return null;
//    }

    // may be need to synchronize
    public static Robot checkRobotsOnWay(Robot robot, Link link, double passed) {
        for (Robot r : robots) {
            if (r != robot) {

//                Robot valueByKey;
//                if((valueByKey=pairs.get(robot))==null) {
//                    for(Map.Entry<Robot, Robot> entry:pairs.entrySet()) {
//                        if(entry.getValue()==robot) {
//                            continueFlag=true;
//                            break;
//                        }
//                    }
//                }
//                if (continueFlag)
//                    continue;
                    if (Link.isSegmentsBlockedByRobot(link.getSegments(),
                            (int) r.getX(), (int) r.getY(), r.getAzimuth(), robot.getMap().getScale(), 0, passed)) {
//                    if(valueByKey!=null) {
//                        pairs.remove(robot);
//                    }
//                    pairs.put(robot, r);
                        return r;
                    }

            }
        }
        return null;
    }
    public static Robot checkRobotsOnWay(Robot robot, List<Link> links, double passed) {
        if(links.isEmpty())
            return null;
        Link currentLink=links.get(0);

        for (Robot r : robots) {
            if (r != robot) {
                for(Link link: links) {
                    if(link==currentLink) {
                        if (Link.isSegmentsBlockedByRobot(link.getSegments(),
                                (int) r.getX(), (int) r.getY(), r.getAzimuth(), robot.getMap().getScale(), 0, passed)) {
                            return r;
                        }
                    } else {
//                Robot valueByKey;
//                if((valueByKey=pairs.get(robot))==null) {
//                    for(Map.Entry<Robot, Robot> entry:pairs.entrySet()) {
//                        if(entry.getValue()==robot) {
//                            continueFlag=true;
//                            break;
//                        }
//                    }
//                }
//                if (continueFlag)
//                    continue;
                        if (Link.isSegmentsBlockedByRobot(link.getSegments(),
                                (int) r.getX(), (int) r.getY(), r.getAzimuth(), robot.getMap().getScale(), 0, 0)) {
//                    if(valueByKey!=null) {
//                        pairs.remove(robot);
//                    }
//                    pairs.put(robot, r);
                            return r;
                        }
                    }
                }
            }
        }
        return null;
    }
    public static Robot checkRobotInCoordinates(int x, int y, int robotSize, Robot robot) {
        //robotSize/=2;
        for(Robot r: robots) {
            if(r!=robot) {
                if (Math.abs(r.getX() - x) <= robotSize && Math.abs(r.getY() - y) <= robotSize) {
                    return r;
                }
            }
        }
        return null;
    }
    public synchronized static boolean isPointOccupiedAsFinish(double x, double y, Robot exeption) {
        for(Robot robot: robots) {
            if(robot!=exeption) {
                Node destination=robot.getRealDestination();
                //if (robot.getFinish().getX() == x && robot.getFinish().getY() == y) {
                if(destination!=null) {
                    if (destination.getX() == x && destination.getY() == y) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void startLog() {
        logging=true;
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                // create files
                PrintWriter writers[]=new PrintWriter[robots.size()];
                for(int i=0; i<robots.size(); ++i) {
                    try {
                        writers[i]=new PrintWriter("robot_"+i);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                for(int i=0; i<robots.size(); ++i) {
                    writers[i].println(robots.get(0).getMap().getWidth() + " " + robots.get(0).getMap().getHeight());
                }
                // while not stopped - log MOVING robots
                while (logging) {
                    for(int i=0; i<robots.size(); ++i) {
                        Robot r=robots.get(i);
                        if(r.getSpeed()>0) {
                            writers[i].println((int)r.getX() + " " + (int)r.getY());
                        }
                    }
                    try {
                        Thread.sleep(25);        //40 fps
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // close all files
                for(PrintWriter w:writers)
                    w.close();
            }
        });
        thread.start();
    }
    public static void stopLog() {
        logging=false;
    }

    public static ArrayList<double[]> getAllCoordinates() {
        ArrayList<double[]> result = new ArrayList<double[]>();
        double coordinates[]=new double[2];
        for(Robot r: robots) {
            coordinates[0]=r.getX();
            coordinates[1]=r.getY();
            result.add(coordinates);
        }
        return result;
    }

    // returns {width, height}
    public static int[] getMapSize() {
        if(robots.size()==0)
            return null;
        int result[]=new int[2];
        result[0]=robots.get(0).getMap().getWidth();
        result[1]=robots.get(0).getMap().getHeight();
        return result;
    }
}
