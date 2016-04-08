package ru.mcst.RobotGroup.PathsLinking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by bocharov_n on 27.11.15.
 */
class TrackingSystem {
    private static List<Camera> cameraList = new ArrayList<Camera>();
//    private static Map<DoubleKey, List<VisitedPoint>> accumulator = new HashMap<DoubleKey, List<VisitedPoint>>();
//    private static List<VisitedPoint> loadedPoints = new ArrayList<VisitedPoint>();


    private static ArrayList<RobotTrajectory> trajectoriesList = new ArrayList<RobotTrajectory>();
    private static ArrayList<InOutVector> inOutVectorsList = new ArrayList<InOutVector>();

    private static double angleSampleRate = 1;
    private static double radiusSampleRate = 1;
    private static double accuracy = radiusSampleRate / 4;

    private static final int coreSize = 5;    // WARNING! MUST BE ODD
    private static final int centerSuperiority = 200; // measured in percent.
    private static final int minimalPointsCount = 10;

    public static void createTrajectoriesList(){
        for(Camera curCamera:cameraList){
            for(RobotTrajectory rt:curCamera.getTracker().getTrajectories()){
                trajectoriesList.add(rt);
            }
        }
    }

    public static void linkTrajectories(){
        ArrayList<InOutVector> inVectors = new ArrayList<InOutVector>(),
                outVectors = new ArrayList<InOutVector>();
        for(Camera camera:TrackingSystem.getCameraList()){
            camera.getTracker().finishAllTrajectories();
            for(RobotTrajectory robotTrajectory:camera.getTracker().getTrajectories()){
                if(robotTrajectory.getPoints().size() >= 2) {
                    int direction = robotTrajectory.getDirection();
                    InOutVector inVector = new InOutVector(robotTrajectory, InOutVector.IN),
                                outVector = new InOutVector(robotTrajectory, InOutVector.OUT);
                    if (direction == 2 || direction == 1) {
                        inVectors.add(inVector);
                        robotTrajectory.setInVector(inVector);
                    }
                    if (direction == 3 || direction == 1) {
                        outVectors.add(outVector);
                        robotTrajectory.setOutVector(outVector);
                    }
                    System.out.println(robotTrajectory.getDirection());
                }
            }
        }
//        double azimuthAccuracy = 5,        //degrees
//                normalAccuracy = 50;        //pixels
        System.out.println("Detected " + inVectors.size() + " inVectors and " + outVectors.size() + " outVectors");
        //Generating list for GUI
        for(InOutVector inVector:inVectors){
            inOutVectorsList.add(inVector);
        }
        for(InOutVector outVector:outVectors){
            inOutVectorsList.add(outVector);
        }


        for(InOutVector outVector:outVectors){
            for(InOutVector inVector:inVectors){
//                System.out.println(outVector.isPotentialFollowerTo(inVector));
                if(outVector.isPotentialFollowerTo(inVector)){
                    if (inVector.getRobotTrajectory().getConnectedTrajectories().indexOf(outVector.getRobotTrajectory()) == - 1 &&
                            !inVector.getRobotTrajectory().equals(outVector.getRobotTrajectory()))
                        inVector.getRobotTrajectory().getConnectedTrajectories().add(outVector.getRobotTrajectory());
                    if (outVector.getRobotTrajectory().getConnectedTrajectories().indexOf(inVector.getRobotTrajectory()) == - 1 &&
                            !inVector.getRobotTrajectory().equals(outVector.getRobotTrajectory()))
                        outVector.getRobotTrajectory().getConnectedTrajectories().add(inVector.getRobotTrajectory());
                }
            }
        }
        createTrajectoriesList();
        int i = 0;
        System.out.println(getTrajectoriesList().size() + " trajectories founded");
        for(RobotTrajectory rt:getTrajectoriesList()){
            System.out.println("trajectory " + i++ + ":");
            if (rt.getInVector() != null)
                System.out.println("inVector azimuth = " + rt.getInVector().getAzimuth() + " normal = " + rt.getInVector().getNormal());
            if (rt.getOutVector() != null)
                System.out.println("outVector azimuth = " + rt.getOutVector().getAzimuth() + " normal = " + rt.getOutVector().getNormal());

            findConnections(rt);
            System.out.println(rt.getConnectedTrajectories().size() + " connected trajectories");
        }
    }

    public static void findConnections(RobotTrajectory robotTrajectory){
        HashMap<RobotTrajectory,Integer> d = new HashMap<RobotTrajectory, Integer>();
        HashMap<RobotTrajectory,RobotTrajectory> p = new HashMap<RobotTrajectory, RobotTrajectory>();
        ArrayList<RobotTrajectory> U = new ArrayList<RobotTrajectory>();
        for(RobotTrajectory rt:trajectoriesList){
            d.put(rt, Integer.MAX_VALUE);
            p.put(rt, null);
        }
        d.put(robotTrajectory, 0);
        p.put(robotTrajectory, robotTrajectory);
        while(U.size() != trajectoriesList.size()){
            int min = Integer.MAX_VALUE;
            RobotTrajectory v = new RobotTrajectory();
            for(RobotTrajectory rt:trajectoriesList){
                if (U.indexOf(rt) == -1 && d.get(rt) < min){
                    min = d.get(rt);
                    v = rt;
                }
            }
            U.add(v);
            for(RobotTrajectory u:v.getConnectedTrajectories()){
                if(U.indexOf(u) == - 1){
                    if (d.get(u) > d.get(v) + 1){
                        d.put(u, d.get(v) + 1);
                        p.put(u, v);
                    }
                }
            }
        }
        for(RobotTrajectory rt: trajectoriesList){
            if (d.get(rt) < Integer.MAX_VALUE && robotTrajectory.getConnectedTrajectories().indexOf(rt) == -1 &&
                    !rt.equals(robotTrajectory))
                robotTrajectory.getConnectedTrajectories().add(rt);
        }

    }

//
//    public static Map<DoubleKey, List<VisitedPoint>> calculateLines(HashSet<VisitedPoint> pointsList, double startR, double endR){
//        Map<DoubleKey, List<VisitedPoint>> acc = new HashMap<DoubleKey, List<VisitedPoint>>();
//        for(double i = 0; i < 180; i += angleSampleRate){
//            for(double j = startR; j < endR; j += radiusSampleRate){
//                DoubleKey dkey = new DoubleKey(i, j);
//                acc.put(dkey, null);
//                List<VisitedPoint> points = new ArrayList<VisitedPoint>();
//                StraightLine line = new StraightLine(i, j, StraightLine.POLAR);
//                for(VisitedPoint point: pointsList){
//                    if (line.isContains(point.getX(), point.getY(), accuracy))
//                        points.add(new VisitedPoint(point));
//                }
//                acc.put(dkey, points);
//            }
//        }
//        return acc;
//    }
//
//
//    public static List<StraightLine> findLocalMaximums(Map<DoubleKey, List<VisitedPoint>> acc, double startR, double endR){
//        List<StraightLine> lines = new ArrayList<StraightLine>();
//
//       for (double i = 0; i < 180 - coreSize * angleSampleRate; i += angleSampleRate) {
//            for (double j = startR; j < endR - coreSize * radiusSampleRate; j += radiusSampleRate) {
//                double mean = 0;
//                try {
//                    for(double p = 0; p < coreSize; p++){
//                        for (double q = 0; q < coreSize; q++){
//                            mean += acc.get(new DoubleKey(i + p * angleSampleRate, j + q * radiusSampleRate)).size();
//                        }
//                    }
//                    mean /= 9;
//                    int center = coreSize / 2;
//                    if (mean * (1 + (double)centerSuperiority / 100) < acc.get(
//                            new DoubleKey(i + center * angleSampleRate, j + center * radiusSampleRate)).size() &&
//                            acc.get(new DoubleKey(i + center * angleSampleRate,
//                                    j + center * radiusSampleRate)).size() > minimalPointsCount){
//                        lines.add(new StraightLine(i + center * angleSampleRate, j + center * radiusSampleRate, StraightLine.POLAR));
//                        log("Line at " + i + " " + j + " with mean " + mean + " center " + acc.get(
//                                new DoubleKey(i + center * angleSampleRate, j + center * radiusSampleRate)).size());
//                    }
//                }
//                catch(NullPointerException ex){
//                    log("error at " + i + " "  + j);
//                }
//            }
//        }
//
//        return lines;
//    }


    public static ArrayList<RobotTrajectory> getTrajectoriesList() {
        return trajectoriesList;
    }

    public static List<Camera> getCameraList() {
        return cameraList;
    }

    public static ArrayList<InOutVector> getInOutVectorsList() {
        return inOutVectorsList;
    }

    public static void addCamera(Camera camera){
        cameraList.add(camera);
    }

    public static void removeCamera(Camera camera){
        cameraList.remove(camera);
    }
}
