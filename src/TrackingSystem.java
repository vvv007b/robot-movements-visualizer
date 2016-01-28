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
    private static List<Camera> cameraList = new ArrayList<>();
    private static List<Trajectory> trajectoryList = new ArrayList<>();
//    private static Map<Camera,Point[]> visible = new HashMap<>();
    private static HashSet<Point2D> visiblePoints = new HashSet<>();
//    private static int[][] accumulator = new int[1][1];
    private static Map<DoubleKey, Integer> accumulator = new HashMap<>();

    private static int width = 640;
    private static int height = 480;

    private static double angleSampleRate = 1;
    private static double radiusSampleRate = 1;

    private static final int coreSize = 3;    // WARNING! MUST BE ODD
    private static final int centerSuperiority = 45; // measured in percent.
    private static final int minimalPointsCount = 25;

    public static void mergeVisible(){
        visiblePoints.clear();
        for(Camera camera: cameraList){
            for(Point2D point:camera.getVisiblePoints())
                visiblePoints.add(point);
        }
    }

    public static void calculateLines(){
//        accumulator = new int[x][y];
//        for()
        for(double i = 0; i < 180; i += angleSampleRate){
            for(double j = 0; j < Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)); j += radiusSampleRate){
                DoubleKey dkey = new DoubleKey(i, j);
                accumulator.put(dkey, 0);
                StraightLine line = new StraightLine(i, j, StraightLine.POLAR);
                for(Point2D point: visiblePoints){
                    double accuracy = radiusSampleRate / 2;
                    if (line.isContains(point.getX(), point.getY(), accuracy)) accumulator.put(dkey, accumulator.get(dkey) + 1);
                }
            }
        }
    }

    public static void printLines(){
        PrintStream bw = null;
        try {
            bw = new PrintStream(new File("123.txt"));
            for(double i = 0; i < 180; i += angleSampleRate){
                for(double j = 0; j < Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)); j += radiusSampleRate){
//                System.out.print(accumulator.get(new DoubleKey(i, j)) + " ");
                    bw.print(accumulator.get(new DoubleKey(i, j)) + " ");
                }
//                System.out.println();
                bw.println();
            }

        }
        catch(IOException ex){
            ex.printStackTrace();
        }

    }

//    public static List<StraightLine> findLocalMaximums(){
//        List<StraightLine> lines = new ArrayList<>();
//        log(width);
//        log(height);
//        log(accumulator.size());
//        double iMax = 180 - coreSize + 1,
//                jMax = Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)) - coreSize + 1;
//        for (int i = 0; i < iMax; i+=angleSampleRate){
//            for (int j = 0; j < jMax; j+=radiusSampleRate){
//                double mean = 0;
//                log(i+ " "+ j + " ");
//                log(accumulator.get(new DoubleKey(0,0)));
////                for (int p = 0; p < (int)Math.pow(coreSize, 2); p++){
////                    log(i + p / coreSize);
////                    log(j + p % coreSize);
////                    mean += accumulator.get(new DoubleKey(i + p / coreSize, j + p % coreSize));
////                }
//                for(int p = 0; p < coreSize; p++){
//                    for(int q = 0; q < coreSize; q++){
//                        mean+=accumulator.get(new DoubleKey(i + p * angleSampleRate, j + q * radiusSampleRate));
//                    }
//                }
//                mean /= 9;
//                if ( accumulator.get(new DoubleKey(i + (coreSize / 2) * angleSampleRate,
//                        j + (coreSize / 2) * radiusSampleRate)) >
//                        mean * (1 + centerSuperiority / 100)) {
//                    lines.add(new StraightLine(i + coreSize / 2, j + coreSize / 2, StraightLine.POLAR));
//                }
//            }
//        }
//        return lines;//
//    }

    public static List<StraightLine> findLocalMaximums(){
        List<StraightLine> lines = new ArrayList<>();


            for (double i = 0; i < 180 - coreSize * angleSampleRate; i += angleSampleRate) {
                for (double j = 0; j < Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)) - coreSize * radiusSampleRate; j += radiusSampleRate) {
                    double mean = 0;
                    try {
                        for(double p = 0; p < coreSize; p++){
                            for (double q = 0; q < coreSize; q++){
                                mean += accumulator.get(new DoubleKey(i + p * angleSampleRate, j + q * radiusSampleRate));
                            }
                        }
                        mean /= 9;
                        int center = coreSize / 2;
                        if (mean * (1 + (double)centerSuperiority / 100) < accumulator.get(
                                new DoubleKey(i + center * angleSampleRate, j + center * radiusSampleRate)) &&
                                accumulator.get(new DoubleKey(i + center * angleSampleRate,
                                        j + center * radiusSampleRate)) > minimalPointsCount){
                            lines.add(new StraightLine(i + center * angleSampleRate, j + center * radiusSampleRate, StraightLine.POLAR));
                            log("Line at " + i + " " + j + " with mean " + mean + " center " + accumulator.get(
                                    new DoubleKey(i + center * angleSampleRate, j + center * radiusSampleRate)));
//                            try {
//                                TimeUnit.MILLISECONDS.sleep(200);
//                            }
//                            catch(InterruptedException e){
//                                e.printStackTrace();
//                            }
                        }
                    }
                    catch(NullPointerException ex){
//                        ex.printStackTrace();
                        log("error at " + i + " "  + j);
                    }
                }
            }

        return lines;
    }

//    public static DoubleKey findMax(){
//        int max = 0;
//        DoubleKey dk = new DoubleKey(0, 0);
//        for(double i = 0; i < 180; i += angleSampleRate){
//            for(double j = 0; j < Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)); j += radiusSampleRate){
//                    if (accumulator.get(new DoubleKey(i, j)) > max) {
//                        max = accumulator.get(new DoubleKey(i, j));
//                        dk = new DoubleKey(i, j);
//                    }
//            }
//        }
//        return dk;
//    }

    public static StraightLine findMax(){
        int max = 0;
        StraightLine dk = new StraightLine(0, 0, StraightLine.POLAR);
        for(double i = 0; i < 180; i += angleSampleRate){
            for(double j = 0; j < Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)); j += radiusSampleRate){
                if (accumulator.get(new DoubleKey(i, j)) > max) {
                    max = accumulator.get(new DoubleKey(i, j));
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
            for(Point2D point:tr.getPointList()){
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
            for(Point2D point: tr.getPointList()){
                for(Camera curCamera:cameraList){
                    if(isVisible(point, curCamera)){
                        curCamera.addVisiblePoint(point);
//                        System.out.println("wow");
                    }
                }
            }
        }
        for(Camera curCamera:cameraList){
            curCamera.redrawVisibleImage();
        }
//        for(Camera curCamera:cameraList){
//            curCamera.getVisiblePoints().clear();
//            for(Trajectory tr:trajectoryList){
//                for(Point2D point: tr.getPointList()){
//
//                }
//
//            }
//        }
    }

    private static boolean isVisible(Point2D point, Camera camera){
        boolean isInCircle = false;
        double x = point.getX(), y = point.getY();
        if(Math.pow(camera.getx() - x, 2) + Math.pow(camera.gety() - y, 2) <= Math.pow(camera.getR(), 2)) isInCircle = true;
//        double  startAngle = (Math.toRadians(camera.getArc().getAngleStart()) + 4 * Math.PI) % (4 * Math.PI),
//                endAngle = (Math.toRadians((camera.getArc().getAngleExtent() + camera.getArc().getAngleStart())) + 4 * Math.PI) % (4 * Math.PI),
//        double  startAngle = Math.toRadians(camera.getArc().getAngleStart()) + 2 * Math.PI,
//                endAngle = Math.toRadians((camera.getArc().getAngleExtent() + camera.getArc().getAngleStart())),
//                angle = Math.acos((x - camera.getx()) / Math.pow(Math.pow(x - camera.getx(), 2) + Math.pow(y - camera.gety(), 2), 0.5)) +
//                        Math.PI * (y -  camera.gety() > 0 ? 1 : 0);
//        log(startAngle);
//        log(endAngle);
//        log(angle);
        double startx = camera.getx() - camera.getR() + camera.getArc().getStartPoint().getX(),
                starty = camera.gety() - camera.getR() + camera.getArc().getStartPoint().getY(),
                endx = camera.getx() - camera.getR() + camera.getArc().getEndPoint().getX(),
                endy = camera.gety() - camera.getR() + camera.getArc().getEndPoint().getY();
        Point2D startPoint = new Point2D.Double(startx, starty),
                endPoint = new Point2D.Double(endx, endy),
                centerPoint = new Point2D.Float(camera.getx(), camera.gety());

//        log(startPoint);
//        log(endPoint);
//        log(centerPoint);

        Vec2d sectorStart = new Vec2d(startPoint.getX() - centerPoint.getX(), startPoint.getY() - centerPoint.getY()),
                sectorEnd = new Vec2d(endPoint.getX() - centerPoint.getX(), endPoint.getY() - centerPoint.getY()),
                relPoint = new Vec2d(x - centerPoint.getX(), y - centerPoint.getY());

//        log(sectorStart);
//        log(sectorEnd);
//        log(relPoint);
        return isInCircle && !areClockwise(sectorStart, relPoint) && areClockwise(sectorEnd, relPoint);

//        return (( angle <= endAngle && angle >= startAngle ) ||
//                ( angle <= endAngle + 2 * Math.PI && angle >= startAngle + 2 * Math.PI ) ||
//                ( angle <= endAngle - 2 * Math.PI && angle >= startAngle - 2 * Math.PI ))
//                        && isInCircle;
//        return camera.getArc().containsAngle(angle) && isInCircle;
    }

    private static boolean areClockwise(Vec2d v1, Vec2d v2){
        return -v1.x * v2.y + v1.y * v2.x < 0;
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

    public static void setTrajectoryList(List<Trajectory> trajectoryList) {
        TrackingSystem.trajectoryList = trajectoryList;
    }

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

    private static void log(String s){ System.out.println(s); }

    private static void log(Double s){ System.out.println(s); }

    private static void log(boolean s){ System.out.println(s); }

    private static void log(int s){ System.out.println(s); }

    private static void log(Point2D p){ System.out.println(p.getX() + " " + p.getY()); }

    private static void log(Vec2d v) { System.out.println(v.x + " " + v.y);}
}
