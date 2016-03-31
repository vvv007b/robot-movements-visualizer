package ru.mcst.RobotGroup.PathsLinking;

import ru.mcst.RobotGroup.PathsFinding.Hypervisor;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by bocharov_n on 02.03.16.
 */
class Tracker extends Thread{
    private Camera camera;
    private ArrayList<RobotTrajectory> trajectories;
    private ArrayList<Integer> visibleRobots;
    private ArrayList<Color> colors;
    private ArrayList<RobotTrajectory> robotsTrajectories;
    //private ArrayList<Integer> robotsTrajectoriesDirections;

    public Tracker(Camera camera){
        super();
        this.camera = camera;
        trajectories = new ArrayList<RobotTrajectory>();
        visibleRobots = new ArrayList<Integer>();
        colors = new ArrayList<Color>();
        robotsTrajectories = new ArrayList<RobotTrajectory>();
        //robotsTrajectoriesDirections = new ArrayList<Integer>();        //0 - nothing; 1 - in && out; 2 - in; 3 - out
    }

    public void run(){
        while (camera.isExist()){
            ArrayList<double[]> allCoordinates = Hypervisor.getAllCoordinates();


            // Here will be check if robots start moving, that change arrays size and create empty fields everywhere;
            // now there a stupid crutch
            if(colors.size() < allCoordinates.size()){
                for(int i = 0; i < allCoordinates.size(); i++){
                    Random rand = new Random();
                    float r = rand.nextFloat();
                    float g = rand.nextFloat();
                    float b = rand.nextFloat();
                    colors.add(new Color(r, g, b));
                }
            }
            if(robotsTrajectories.size() < allCoordinates.size()){
                for(int i = 0; i < allCoordinates.size(); i++){
                    robotsTrajectories.add(new RobotTrajectory());
                    //robotsTrajectoriesDirections.add(0);
                }
            }

            int[] mapSize = Hypervisor.getMapSize();
            if(mapSize != null && MapUnderlay.getMapLayer() != null && MapUnderlay.getTrajectoriesLayer() != null) {
                BufferedImage trajectories = new BufferedImage(mapSize[0],mapSize[1], BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = trajectories.createGraphics();
                g2d.setComposite(AlphaComposite.Clear);
                g2d.fillRect(0, 0, trajectories.getWidth(), trajectories.getHeight());
                g2d.setComposite(AlphaComposite.Src);
                g2d.setColor(Color.RED);

                ArrayList<Integer> currentVisibleRobots = new ArrayList<Integer>();

                for (int i = 0; i < allCoordinates.size(); i++){
                    double[] coord = allCoordinates.get(i);
                    if (camera.isVisible(new Point2D.Double(coord[0], coord[1])) && Hypervisor.getSpeeds().get(i) > 0) {
                        currentVisibleRobots.add(i);
                        g2d.setColor(colors.get(i));
                        g2d.fillOval((int) coord[0] - 3, (int) coord[1] - 3, 6, 6);
                        if(visibleRobots.indexOf(i) == -1){        //Вообще, наверное, эту проверку вместе с очисткой можно убрать к хуям. или нет.
                            robotsTrajectories.get(i).getPoints().clear();   //Очищаем прошлую траекторию робота
                            robotsTrajectories.get(i).setDirection(2); // Выставляем флаг входа\выхода в положение "только вход"
                            System.out.println("Robot " + i + " entered scope");
                        }
                        robotsTrajectories.get(i).getPoints().add(new Point2D.Double(coord[0], coord[1]));  //Добавляем его координату
                    }
                }                                                   //TODO: подумать, не может ли быть пропуск итерации при перекрытии областей видимости камер.
                for(int i:visibleRobots){
                    if (currentVisibleRobots.indexOf(i) == -1){
                        RobotTrajectory trajectory = new RobotTrajectory();
                        for(Point2D point2D:robotsTrajectories.get(i).getPoints()){
                            trajectory.getPoints().add(point2D);
                        }
                        System.out.println("Robot " + i + " exited scope");
                        if(robotsTrajectories.get(i).getDirection() == 2)
                            trajectory.setDirection(1);
                        if(robotsTrajectories.get(i).getDirection() == 0)
                            trajectory.setDirection(3);
                        this.trajectories.add(trajectory);
                        robotsTrajectories.get(i).getPoints().clear();
                        robotsTrajectories.get(i).setDirection(0);
                        Random rand = new Random();
                        float r = rand.nextFloat();
                        float g = rand.nextFloat();
                        float b = rand.nextFloat();
                        colors.set(i, new Color(r, g, b));
                    }
                }
                visibleRobots = currentVisibleRobots;
                g2d.dispose();
                MapUnderlay.changeTrajectoriesLayer(trajectories);
                GUI.getMapPanel().repaint();
            }
            try{
                sleep(25);
            }
            catch(InterruptedException ex){
                ex.printStackTrace();
            }

        }
    }

    public void finishAllTrajectories(){
        for(RobotTrajectory robotTrajectory:robotsTrajectories){
            if(!robotTrajectory.getPoints().isEmpty()){
                trajectories.add(new RobotTrajectory(robotTrajectory));
            }
        }
    }

    public void clear(){
        trajectories.clear();
        robotsTrajectories.clear();
        visibleRobots.clear();
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public ArrayList<RobotTrajectory> getTrajectories() {
        return trajectories;
    }
}
