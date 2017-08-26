package io.dovid.multitimer.model;

/**
 * Author: Umberto D'Ovidio
 * Date: 19/08/17
 * Email: umberto.dovidio@gmail.com
 * Website: http://dovid.io
 * Tutorial link : http://dovid.io
 */

public class TimerEntity {

    private int id;
    private String name;
    private long defaultTime;
    private long expiredTime;
    private boolean isRunning;
    private boolean shouldNotify;

    public TimerEntity() {
        super();
    }

    public TimerEntity(int id, String name, long countdownTime, long countdownTimeRunning, boolean currentlyRunning, boolean shouldNotify) {
        this.id = id;
        this.name = name;
        this.defaultTime = countdownTime;
        this.expiredTime = countdownTimeRunning;
        this.isRunning = currentlyRunning;
        this.shouldNotify = shouldNotify;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDefaultTime() {
        return defaultTime;
    }

    public long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(long expiredTime) {
        this.expiredTime = expiredTime;
    }

    public void setDefaultTime(long defaultTime) {
        this.defaultTime = defaultTime;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        this.isRunning = running;
    }

    public boolean shouldNotify() {
        return shouldNotify;
    }

    public void setShouldNotify(boolean shouldNotify) {
        this.shouldNotify = shouldNotify;
    }

    @Override
    public String toString() {
        return "name: " + name + " defaultTime: " + defaultTime + " expiredTime: " + expiredTime + " isRunning: " + isRunning;
     }
}
