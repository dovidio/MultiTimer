package io.dovid.multitimer.model;

/**
 * Author: Umberto D'Ovidio
 * Date: 19/08/17
 * Email: umberto.dovidio@gmail.com
 * Website: http://dovid.io
 * Tutorial link : http://dovid.io
 */

public class CustomTimer {
    private String name;
    private long countdownTime;
    private long countdownTimeRunning;
    private boolean currentlyRunning;


    public CustomTimer(String name, long countdownTime, long countdownTimeRunning, boolean currentlyRunning) {
        this.name = name;
        this.countdownTime = countdownTime;
        this.countdownTimeRunning = countdownTimeRunning;
        this.currentlyRunning = currentlyRunning;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCountdownTime() {
        return countdownTime;
    }

    public long getCountdownTimeRunning() {
        return countdownTimeRunning;
    }

    public void setCountdownTimeRunning(long countdownTimeRunning) {
        this.countdownTimeRunning = countdownTimeRunning;
    }

    public void setCountdownTime(long countdownTime) {
        this.countdownTime = countdownTime;
    }

    public boolean isCurrentlyRunning() {
        return currentlyRunning;
    }

    public void setCurrentlyRunning(boolean currentlyRunning) {
        this.currentlyRunning = currentlyRunning;
    }


    @Override
    public String toString() {
        return "name: " + name + " countdownTime: " + countdownTime + " countDownTimeRunning: " + countdownTimeRunning + " currentlyRunning: " + currentlyRunning;
     }
}
