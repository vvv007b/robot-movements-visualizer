package ru.mcst.RobotGroup.PathsLinking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by bocharov_n on 27.11.15.
 */
class TrackingSystem {
    private static List<Camera> cameraList = new ArrayList<>();

    private static ArrayList<RobotTrajectory> trajectoriesList = new ArrayList<>();
    private static ArrayList<InOutVector> inOutVectorsList = new ArrayList<>();


    public static void linkTrajectories(){
        ArrayList<InOutVector> inVectors = new ArrayList<>(),
                outVectors = new ArrayList<>();
        for(Camera camera:TrackingSystem.getCameraList()){
            camera.getTracker().finishAllTrajectories();
            camera.getTracker().getTrajectories().stream().filter(robotTrajectory -> robotTrajectory.getPoints().size() >= 2).forEach(robotTrajectory -> {
                int direction = robotTrajectory.getDirection();
                if (robotTrajectory.getPoints().size() >= 3) {
                    InOutVector inVector = new InOutVector(robotTrajectory, InOutVector.IN),
                            outVector = new InOutVector(robotTrajectory, InOutVector.OUT);
                    if (direction == 2 || direction == 1) {
                        inVectors.add(inVector);
                        inOutVectorsList.add(inVector);
                        robotTrajectory.setInVector(inVector);
                    }
                    if (direction == 3 || direction == 1) {
                        outVectors.add(outVector);
                        inOutVectorsList.add(outVector);
                        robotTrajectory.setOutVector(outVector);
                    }
                    System.out.println(robotTrajectory.getDirection());
                    trajectoriesList.add(robotTrajectory);
                }
            });
        }
        System.out.println("Detected " + inVectors.size() + " inVectors and " + outVectors.size() + " outVectors");

        for (RobotTrajectory robotTrajectory:trajectoriesList){
            for(RobotTrajectory next:robotTrajectory.getNext()){
                next.getInVector().getPrev().add(robotTrajectory.getOutVector());
            }
            for(RobotTrajectory prev:robotTrajectory.getPrev()){
                prev.getOutVector().getNext().add(robotTrajectory.getInVector());
            }
        }


        for(InOutVector outVector:outVectors){
            inVectors.stream().filter(inVector -> outVector.isPotentialFollowerTo(inVector) &
                    !inVector.getRobotTrajectory().equals(outVector.getRobotTrajectory())).forEach(inVector -> {
                if (inVector.getRobotTrajectory().getConnectedTrajectories().indexOf(outVector.getRobotTrajectory()) == -1)
                    inVector.getRobotTrajectory().getConnectedTrajectories().add(outVector.getRobotTrajectory());
                if (outVector.getRobotTrajectory().getConnectedTrajectories().indexOf(inVector.getRobotTrajectory()) == -1)
                    outVector.getRobotTrajectory().getConnectedTrajectories().add(inVector.getRobotTrajectory());
                inVector.getPrev().add(outVector);
                outVector.getNext().add(inVector);
            });
        }
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
        HashMap<RobotTrajectory,Integer> d = new HashMap<>();
//        HashMap<RobotTrajectory,RobotTrajectory> p = new HashMap<RobotTrajectory, RobotTrajectory>();
        ArrayList<RobotTrajectory> U = new ArrayList<>();
        for(RobotTrajectory rt:trajectoriesList){
            d.put(rt, Integer.MAX_VALUE);
//            p.put(rt, null);
        }
        d.put(robotTrajectory, 0);
//        p.put(robotTrajectory, robotTrajectory);
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
//                        p.put(u, v);
                    }
                }
            }
        }
        trajectoriesList.stream().filter(rt -> d.get(rt) < Integer.MAX_VALUE && robotTrajectory.getConnectedTrajectories().indexOf(rt) == -1 &&
                !rt.equals(robotTrajectory)).forEach(rt -> robotTrajectory.getConnectedTrajectories().add(rt));

    }

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
