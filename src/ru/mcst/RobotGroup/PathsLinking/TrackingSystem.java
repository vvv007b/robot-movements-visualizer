package ru.mcst.RobotGroup.PathsLinking;

import com.sun.javafx.geom.Vec2d;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.Map;

/**
 * Created by bocharov_n on 27.11.15.
 */
public class TrackingSystem {
    private static List<Camera> cameraList = new ArrayList<Camera>();
    private static List<Trajectory> trajectoryList = new ArrayList<Trajectory>();
//    private static Map<Camera,Point[]> visible = new HashMap<>();
    private static HashSet<VisitedPoint> visiblePoints = new HashSet<VisitedPoint>();
//    private static int[][] accumulator = new int[1][1];
//    private static Map<DoubleKey, Integer> accumulator = new HashMap<>();
    private static Map<DoubleKey, List<VisitedPoint>> accumulator = new HashMap<DoubleKey, List<VisitedPoint>>();
    private static List<VisitedPoint> loadedPoints = new ArrayList<VisitedPoint>();

    private static List<InOutVector> inVectors = new ArrayList<InOutVector>(),
                                    outVectors = new ArrayList<InOutVector>();

    private static int width = 640;
    private static int height = 480;

    private static double angleSampleRate = 1;
    private static double radiusSampleRate = 1;
    private static double accuracy = radiusSampleRate / 4;

    private static final int coreSize = 5;    // WARNING! MUST BE ODD
    private static final int centerSuperiority = 200; // measured in percent.
    private static final int minimalPointsCount = 10;

    public static void findMatches(){
        for(Camera curCamera:cameraList){
            calculateVisibleForCamera(curCamera);
            HashSet<VisitedPoint> points = new HashSet<VisitedPoint>(curCamera.getVisiblePoints());
            Map<DoubleKey, List<VisitedPoint>> acc = new HashMap<DoubleKey, List<VisitedPoint>>();
            double x = curCamera.getx(),
                    y = curCamera.gety(),
                    r = curCamera.getR();
            double startR = Math.pow(Math.pow(x - r, 2) + Math.pow(y - r, 2), 0.5),
                    endR = Math.pow(Math.pow(x + r, 2) + Math.pow(y + r, 2), 0.5);
            acc = calculateLines(points, startR, endR);
            List<StraightLine> lines = findLocalMaximums(acc, startR, endR);
            
        }
    }

    public static void mergeVisible(){
        visiblePoints.clear();
        for(Camera camera: cameraList){
            for(VisitedPoint point:camera.getVisiblePoints())
                visiblePoints.add(point);
        }
    }

    public static Map<DoubleKey, List<VisitedPoint>> calculateLines(HashSet<VisitedPoint> pointsList, double startR, double endR){
        Map<DoubleKey, List<VisitedPoint>> acc = new HashMap<DoubleKey, List<VisitedPoint>>();
        for(double i = 0; i < 180; i += angleSampleRate){
            for(double j = startR; j < endR; j += radiusSampleRate){
                DoubleKey dkey = new DoubleKey(i, j);
                acc.put(dkey, null);
                List<VisitedPoint> points = new ArrayList<VisitedPoint>();
                StraightLine line = new StraightLine(i, j, StraightLine.POLAR);
                for(VisitedPoint point: pointsList){
                    if (line.isContains(point.getX(), point.getY(), accuracy))
                        points.add(new VisitedPoint(point));
//                        acc.put(dkey, acc.get(dkey) + 1);
                }
                acc.put(dkey, points);
            }
        }
        return acc;
    }

    public static void printLines(){
        PrintStream bw = null;
        try {
            bw = new PrintStream(new File("123.txt"));
            for(double i = 0; i < 180; i += angleSampleRate){
                for(double j = 0; j < Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)); j += radiusSampleRate){
//                bw.print(accumulator.get(new DoubleKey(i, j)) + " ");
                }
                bw.println();
            }

        }
        catch(IOException ex){
            ex.printStackTrace();
        }

    }

    public static List<StraightLine> findLocalMaximums(Map<DoubleKey, List<VisitedPoint>> acc, double startR, double endR){
        List<StraightLine> lines = new ArrayList<StraightLine>();
        
       for (double i = 0; i < 180 - coreSize * angleSampleRate; i += angleSampleRate) {
            for (double j = startR; j < endR - coreSize * radiusSampleRate; j += radiusSampleRate) {
                double mean = 0;
                try {
                    for(double p = 0; p < coreSize; p++){
                        for (double q = 0; q < coreSize; q++){
                            mean += acc.get(new DoubleKey(i + p * angleSampleRate, j + q * radiusSampleRate)).size();
                        }
                    }
                    mean /= 9;
                    int center = coreSize / 2;
                    if (mean * (1 + (double)centerSuperiority / 100) < acc.get(
                            new DoubleKey(i + center * angleSampleRate, j + center * radiusSampleRate)).size() &&
                            acc.get(new DoubleKey(i + center * angleSampleRate,
                                    j + center * radiusSampleRate)).size() > minimalPointsCount){
                        lines.add(new StraightLine(i + center * angleSampleRate, j + center * radiusSampleRate, StraightLine.POLAR));
                        log("Line at " + i + " " + j + " with mean " + mean + " center " + acc.get(
                                new DoubleKey(i + center * angleSampleRate, j + center * radiusSampleRate)).size());
                    }
                }
                catch(NullPointerException ex){
                    log("error at " + i + " "  + j);
                }
            }
        }

        return lines;
    }

    public static void findInOutVectors(){
        inVectors.clear();
        outVectors.clear();
        List<VisitedPoint> cornerPoints = new ArrayList<VisitedPoint>();
        for(Camera curCamera:getCameraList()){
            System.out.println("Camera:" + curCamera.getx() + " " + curCamera.gety() + "have " +
                    curCamera.getVisiblePoints().size() + " visible points");
            for(VisitedPoint point: curCamera.getVisiblePoints()){
                if (curCamera.isOnCorner(point)) {
                    cornerPoints.add(point);
                    System.out.println("corner: " + point.x +" "+ point.y);
                }
            }
            for(VisitedPoint cornerPoint:cornerPoints){                  //Looking for nearest in time
                double mindT = Double.MAX_VALUE;
                VisitedPoint nearestPoint = null;
                for(VisitedPoint visitedPoint: curCamera.getVisiblePoints()) {
                    double dt = Math.abs(visitedPoint.getT() - cornerPoint.getT());
                    if ( dt != 0 && dt < mindT ) {
                        mindT = dt;
                        nearestPoint = visitedPoint;
                    }
                }
                if ( nearestPoint != null && nearestPoint.getT() - cornerPoint.getT() < 0 ){
                    //out
                    outVectors.add(new InOutVector(nearestPoint, cornerPoint));
                    System.out.println("nearest: " + nearestPoint.x+ " " + nearestPoint.y);
                }
                else{
                    inVectors.add(new InOutVector(cornerPoint, nearestPoint));
                    System.out.println("nearest: " + nearestPoint.x+ " " + nearestPoint.y);
                }
            }
        }
        System.out.println(outVectors.size()+" "+ inVectors.size());
    }

    public static StraightLine findMax(){
        int max = 0;
        StraightLine dk = new StraightLine(0, 0, StraightLine.POLAR);
        for(double i = 0; i < 180; i += angleSampleRate){
            for(double j = 0; j < Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)); j += radiusSampleRate){
                if (accumulator.get(new DoubleKey(i, j)).size() > max) {
                    max = accumulator.get(new DoubleKey(i, j)).size();
                    dk = new StraightLine(i, j, StraightLine.POLAR);
                }
            }
        }
        return dk;
    }

    public static void calculateVisibleForCamera(Camera camera){
        camera.getVisiblePoints().clear();
        camera.clearVisibleImage();
        for(Trajectory tr:trajectoryList){
            tr.calculatePointList();
            for(VisitedPoint point:tr.getPointList()){
                if (isVisible(point, camera)){
                    camera.addVisiblePoint(point);
                }
            }
        }
        camera.redrawVisibleImage();
    }

    public static void calculateVisible(){

        for(Camera curCamera:cameraList){
            curCamera.getVisiblePoints().clear();
            curCamera.clearVisibleImage();
        }
        for(Trajectory tr:trajectoryList){
            tr.calculatePointList();
            for(VisitedPoint point: tr.getPointList()){
                for(Camera curCamera:cameraList){
                    if(isVisible(point, curCamera)){
                        curCamera.addVisiblePoint(point);
//                        System.out.println("wow");
                    }
                }
            }
        }
        for(VisitedPoint point: loadedPoints){
            for(Camera curCamera:cameraList){
                if(isVisible(point, curCamera)){
                    curCamera.addVisiblePoint(point);
                }
            }
        }
        for(Camera curCamera:cameraList){
            curCamera.redrawVisibleImage();
        }
    }

    public static void calculateVisibleAfterLoading(){
        for(Camera curCamera:cameraList){
            curCamera.getVisiblePoints().clear();
            curCamera.clearVisibleImage();
        }
        for(VisitedPoint point: loadedPoints){
            for(Camera curCamera:cameraList){
                if(isVisible(point, curCamera)){
                    curCamera.addVisiblePoint(point);
                }
            }
        }
    }

    private static boolean isVisible(Point2D point, Camera camera){
        double x = point.getX(), y = point.getY();
        boolean isInCircle = Math.pow(camera.getx() - x, 2) + Math.pow(camera.gety() - y, 2) <= Math.pow(camera.getR(), 2);
//        if(Math.pow(camera.getx() - x, 2) + Math.pow(camera.gety() - y, 2) <= Math.pow(camera.getR(), 2)) isInCircle = true;
        double startx = camera.getx() - camera.getR() + camera.getArc().getStartPoint().getX(),
                starty = camera.gety() - camera.getR() + camera.getArc().getStartPoint().getY(),
                endx = camera.getx() - camera.getR() + camera.getArc().getEndPoint().getX(),
                endy = camera.gety() - camera.getR() + camera.getArc().getEndPoint().getY();
        Point2D startPoint = new Point2D.Double(startx, starty),
                endPoint = new Point2D.Double(endx, endy),
                centerPoint = new Point2D.Float(camera.getx(), camera.gety());

        Vec2d sectorStart = new Vec2d(startPoint.getX() - centerPoint.getX(), startPoint.getY() - centerPoint.getY()),
                sectorEnd = new Vec2d(endPoint.getX() - centerPoint.getX(), endPoint.getY() - centerPoint.getY()),
                relPoint = new Vec2d(x - centerPoint.getX(), y - centerPoint.getY());
        return isInCircle && !areClockwise(sectorStart, relPoint) && areClockwise(sectorEnd, relPoint);
    }

    public static boolean isVisible(Point2D p){
        for(Camera curCamera:cameraList){
            if (isVisible(p, curCamera)) return true;
        }
        return false;
    }

    private static boolean areClockwise(Vec2d v1, Vec2d v2){
        return -v1.x * v2.y + v1.y * v2.x <= 0;
    }

    public static List<Camera> getCameraList() {
        return cameraList;
    }

    public static void setCameraList(List<Camera> cameraList) {
        TrackingSystem.cameraList = cameraList;
    }

    public static void addCamera(Camera camera){
        cameraList.add(camera);
    }

    public static void removeCamera(Camera camera){
        cameraList.remove(camera);
    }

    public static void removeCamera(int index){
        cameraList.remove(index);
    }

    public static List<Trajectory> getTrajectoryList() {
        return trajectoryList;
    }

    public static void setTrajectoryList(List<Trajectory> trajectoryList) { TrackingSystem.trajectoryList = trajectoryList;    }

    public static void addTrajectory(Trajectory trajectory){
        trajectoryList.add(trajectory);
    }

    public static void removeTrajectory(Trajectory trajectory){
        trajectoryList.remove(trajectory);
    }

    public static void removeTrajectory(int index){
        trajectoryList.remove(index);
    }

    public static int getWidth() {
        return width;
    }

    public static void setWidth(int width) {
        TrackingSystem.width = width;
    }

    public static int getHeight() {
        return height;
    }

    public static void setHeight(int height) {
        TrackingSystem.height = height;
    }

    public static List<VisitedPoint> getloadedPoints() { return loadedPoints; }

    public static void setloadedPoints(List<VisitedPoint> loadedPoints) { TrackingSystem.loadedPoints = loadedPoints; }

    public static HashSet<VisitedPoint> getVisiblePoints() {
        return visiblePoints;
    }

    public static void setVisiblePoints(HashSet<VisitedPoint> visiblePoints) {
        TrackingSystem.visiblePoints = visiblePoints;
    }

//    public static Map<DoubleKey, Integer> getAccumulator() {
//        return accumulator;
//    }
//
//    public static void setAccumulator(Map<DoubleKey, Integer> accumulator) {
//        TrackingSystem.accumulator = accumulator;
//    }


    public static Map<DoubleKey, List<VisitedPoint>> getAccumulator() {
        return accumulator;
    }

    public static void setAccumulator(Map<DoubleKey, List<VisitedPoint>> accumulator) {
        TrackingSystem.accumulator = accumulator;
    }

    public static void addPoint(VisitedPoint p){loadedPoints.add(p);}

    private static void log(String s){ System.out.println(s); }

    private static void log(Double s){ System.out.println(s); }

    private static void log(boolean s){ System.out.println(s); }

    private static void log(int s){ System.out.println(s); }

    private static void log(Point2D p){ System.out.println(p.getX() + " " + p.getY()); }

    private static void log(Vec2d v) { System.out.println(v.x + " " + v.y);}
}
