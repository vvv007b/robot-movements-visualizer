package ru.mcst.robotGroup.paths.linking;

import ru.mcst.robotGroup.paths.RobotsState;
import ru.mcst.robotGroup.paths.finding.Hypervisor;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

class Tracker extends Thread {
    private final Camera camera;
    private final ArrayList<RobotTrajectory> trajectories;
    private ArrayList<Integer> visibleRobots;
    private final ArrayList<Color> colors;
    private final ArrayList<RobotTrajectory> robotsTrajectories;
    private boolean markForClear;

    public Tracker(Camera camera) {
        super();
        this.camera = camera;
        trajectories = new ArrayList<>();
        visibleRobots = new ArrayList<>();
        colors = new ArrayList<>();
        robotsTrajectories = new ArrayList<>();
        this.setName("Camera " + camera.getIndex());
        markForClear = false;
    }

    public void run() {
        System.out.println("Tracker start");
        while (camera.isExist()) {
//            long time = System.currentTimeMillis();
            if (markForClear) {
                markForClear = false;
                trajectories.clear();
                robotsTrajectories.clear();
                visibleRobots.clear();
            }
            ArrayList<double[]> allCoordinates = RobotsState.getInstance().getAllCoordinates();
            ArrayList<Double> speeds = RobotsState.getInstance().getSpeeds();
            ArrayList<Long> times = RobotsState.getInstance().getUpdateTimes();
            if (colors.size() < allCoordinates.size()) {
                for (int i = 0; i < allCoordinates.size(); i++) {
                    Random rand = new Random();
                    float r = rand.nextFloat();
                    float g = rand.nextFloat();
                    float b = rand.nextFloat();
                    colors.add(new Color(r, g, b));
                }
            }
            if (robotsTrajectories.size() < allCoordinates.size()) {
                for (int i = 0; i < allCoordinates.size(); i++) {
                    robotsTrajectories.add(new RobotTrajectory());
                }
            }
            int[] mapSize = Hypervisor.getMapSize();
            if (mapSize != null && MapUnderlay.getMapLayer() != null && MapUnderlay.getTrajectoriesLayer() != null) {
                BufferedImage trajectories = new BufferedImage(mapSize[0], mapSize[1], BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = trajectories.createGraphics();
                g2d.setComposite(AlphaComposite.Clear);
                g2d.fillRect(0, 0, trajectories.getWidth(), trajectories.getHeight());
                g2d.setComposite(AlphaComposite.Src);
                g2d.setColor(Color.RED);
                ArrayList<Integer> currentVisibleRobots = new ArrayList<>();
                for (int i = 0; i < allCoordinates.size(); i++) {
                    double[] coord = allCoordinates.get(i);
                    Point2D currentPoint = new Point2D.Double(coord[0], coord[1]);
                    if (camera.isVisible(currentPoint) && speeds.get(i) > 0) {
                        currentVisibleRobots.add(i);
                        g2d.setColor(colors.get(i));
                        g2d.fillOval((int) coord[0] - 3, (int) coord[1] - 3, 6, 6);
                        if (visibleRobots.indexOf(i) == -1) {
                            robotsTrajectories.get(i).getPoints().clear();   //Очищаем прошлую траекторию робота
                            robotsTrajectories.get(i).setDirection(2); // Выставляем флаг входа\выхода в положение "только вход"
                            System.out.println("Robot " + i + " entered scope");
                        }
                        Point2D prevPoint;
                        if (robotsTrajectories.get(i).getPoints().size() > 0)
                            prevPoint = robotsTrajectories.get(i).getPoints().get(robotsTrajectories.get(i).getPoints().size() - 1);
                        else prevPoint = new Point2D.Double(-1, -1);            //impossible point
                        if (!currentPoint.equals(prevPoint)) {
                            robotsTrajectories.get(i).getPoints().add(currentPoint);
                            robotsTrajectories.get(i).getSpeeds().add(speeds.get(i));
                            robotsTrajectories.get(i).getTimes().add(times.get(i));
                            //Проверяем одновременную видимость с нескольких камер
//                            for (Camera curCamera : TrackingSystem.getInstance().getCameraList()) {
                            for (Camera curCamera : TrackingSystem.getCameraList()) {
                                    if (curCamera.getTracker().isRobotVisibleNow(i) &&
                                        robotsTrajectories.get(i).getConnectedTrajectories().indexOf(curCamera.getTracker().getRobotsTrajectories().get(i)) == -1 &&
                                        curCamera != camera) {
                                    if (robotTrajectoryLength(i) > curCamera.getTracker().robotTrajectoryLength(i)) {
                                        robotsTrajectories.get(i).getNext().add(curCamera.getTracker().getRobotsTrajectories().get(i));
                                    } else {
                                        robotsTrajectories.get(i).getPrev().add(curCamera.getTracker().getRobotsTrajectories().get(i));
                                    }
                                    System.out.println("double vision");
                                    robotsTrajectories.get(i).getConnectedTrajectories().add(curCamera.getTracker().getRobotsTrajectories().get(i));
                                }
                            }
                        }
                    }
                }
                visibleRobots.stream().filter(i -> currentVisibleRobots.indexOf(i) == -1).forEach(i -> {
                    RobotTrajectory trajectory = robotsTrajectories.get(i);
                    System.out.println("Robot " + i + " exited scope");
                    if (robotsTrajectories.get(i).getDirection() == 2)
                        trajectory.setDirection(1);
                    if (robotsTrajectories.get(i).getDirection() == 0)
                        trajectory.setDirection(3);
                    this.trajectories.add(trajectory);
                    robotsTrajectories.set(i, new RobotTrajectory());
                    robotsTrajectories.get(i).getPoints().clear();
                    robotsTrajectories.get(i).setDirection(0);
                    Random rand = new Random();
                    float r = rand.nextFloat();
                    float g = rand.nextFloat();
                    float b = rand.nextFloat();
                    colors.set(i, new Color(r, g, b));
                });
                visibleRobots = currentVisibleRobots;
                g2d.dispose();
                MapUnderlay.changeTrajectoriesLayer(trajectories);
                PathsLinkingGUI.getMapPanel().repaint();
            }
            try {
                sleep(25);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
//            System.out.println(System.currentTimeMillis() - time);
        }
        System.out.println("Tracker end");
    }

    public void finishAllTrajectories() {
        trajectories.addAll(robotsTrajectories.stream().filter(robotTrajectory -> !robotTrajectory.getPoints().isEmpty()).map(RobotTrajectory::new).collect(Collectors.toList()));
    }

    public int getVisibleRobotsCount() {
        return visibleRobots.size();
    }

    private boolean isRobotVisibleNow(int index) {
        return visibleRobots.indexOf(index) >= 0;
    }

    private int robotTrajectoryLength(int index) {
        return isRobotVisibleNow(index) ? robotsTrajectories.get(index).getPoints().size() : -1;
    }

    private ArrayList<RobotTrajectory> getRobotsTrajectories() {
        return robotsTrajectories;
    }

    public ArrayList<RobotTrajectory> getTrajectories() {
        return trajectories;
    }

    public void setMarkForClear() {
        this.markForClear = true;
    }
}
