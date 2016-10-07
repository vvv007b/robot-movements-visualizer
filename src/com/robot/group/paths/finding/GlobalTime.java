package com.robot.group.paths.finding;

/**
 * Created by bocharov_n on 07.10.16.
 */
class GlobalTime extends Thread {

//    private static double RAIN_CHANCE = 0.001;

    private boolean isTimeRuns;
    private boolean isAlive;

    private boolean isRain;
    private boolean isDry;

    private Hypervisor hypervisor;

    public GlobalTime(Hypervisor hypervisor) {
        this.isTimeRuns = false;
        this.isAlive = true;
        this.hypervisor = hypervisor;
    }

    @Override
    public void run() {
        while (isAlive) {
            if (isTimeRuns) {
                //some changes with reality map

            }
            try {
                sleep(20);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
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

    public void setRain(boolean rain) {
        isRain = rain;
    }

    public void setDry(boolean dry) {
        isDry = dry;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }
}
