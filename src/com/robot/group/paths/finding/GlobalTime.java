package com.robot.group.paths.finding;

/**
 * Created by bocharov_n on 07.10.16.
 */
class GlobalTime extends Thread {

//    private static double RAIN_CHANCE = 0.001;
    private static final int WEATHER_SLEEPTIME = 1000;
    private static final int NORMAL_SLEEPTIME = 20;


    private boolean isTimeRuns;
    private boolean isAlive;

    public static final int NORMAL_WEATHER = 0;
    public static final int RAIN_WEATHER = 1;
    public static final int DRY_WEATHER = 2;

    private int weather;

    public GlobalTime() {
        this.isTimeRuns = false;
        this.isAlive = true;
    }

    @Override
    public void run() {
        while (isAlive) {
            if (isTimeRuns) {
                //some changes with reality map
                switch (this.weather){
                    case RAIN_WEATHER:
                        //reality update
                        for (Robot curRobot:Hypervisor.getRobots()) {
                            synchronized (curRobot.getMap().getRealityArray()){
                                byte[][] realityArray = curRobot.getMap().getRealityArray();
                                for (int i = 0; i < realityArray.length; i++) {
                                    for (int j = 0; j < realityArray[0].length; j++) {

                                    }
                                }
                            }
                        }
                        try {
                            sleep(WEATHER_SLEEPTIME);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        break;
                    case DRY_WEATHER:
                        //reality update
                        try {
                            sleep(WEATHER_SLEEPTIME);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        break;
                    case NORMAL_WEATHER:
                        try {
                            sleep(NORMAL_SLEEPTIME);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        break;
                    default:
                        try {
                            sleep(NORMAL_SLEEPTIME);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        break;
                }
            }

        }
    }

    public boolean isTimeRuns() {
        return isTimeRuns;
    }

    public void setTimeRuns(boolean timeRuns) {
        isTimeRuns = timeRuns;
    }

    public void killTime() {
        isAlive = false;
    }

    public void setWeather(int weather){
        this.weather = weather;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }
}
