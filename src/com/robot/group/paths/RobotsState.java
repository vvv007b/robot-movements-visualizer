package com.robot.group.paths;

import com.robot.group.paths.finding.Hypervisor;
import com.robot.group.paths.linking.Server;

import java.util.ArrayList;

/**
 * Created by bocharov_n on 19.09.16.
 */
public class RobotsState {
    public static final int NETWORK_MODE = 0;
    public static final int LOCAL_MODE = 1;         //default
    private int mode;
    private static volatile RobotsState instance;


    private RobotsState() {
        mode = RobotsState.LOCAL_MODE;
    }

    public static RobotsState getInstance() {
        RobotsState localInstance = instance;
        if (localInstance == null) {
            synchronized (RobotsState.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new RobotsState();
                }
            }
        }
        return localInstance;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public ArrayList<double[]> getAllCoordinates() {
        return this.mode == LOCAL_MODE ? Hypervisor.getAllCoordinates() : Server.getAllCoordinates();
    }

    public ArrayList<Double> getSpeeds() {
        return this.mode == LOCAL_MODE ? Hypervisor.getSpeeds() : Server.getSpeeds();
    }

    public ArrayList<Long> getUpdateTimes() {
        return this.mode == LOCAL_MODE ? Hypervisor.getUpdateTimes() : Server.getUpdateTimes();
    }

}
