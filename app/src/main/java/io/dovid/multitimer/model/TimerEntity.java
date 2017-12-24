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
    private boolean isAnimating;
    private boolean shouldNotify;


    public TimerEntity() {
        this.name = "";
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

    public void setDefaultTime(long defaultTime) {
        this.defaultTime = defaultTime;
    }

    public long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(long expiredTime) {
        this.expiredTime = expiredTime;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public boolean isAnimating() {
        return isAnimating;
    }

    public void setAnimating(boolean animating) {
        isAnimating = animating;
    }

    public boolean shouldNotify() {
        return shouldNotify;
    }

    public void setShouldNotify(boolean shouldNotify) {
        this.shouldNotify = shouldNotify;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof TimerEntity)) {
            return false;
        }
        TimerEntity other = (TimerEntity) obj;

        return this.id == other.id &&
                this.name.equals(other.name) &&
                this.defaultTime == other.defaultTime &&
                this.expiredTime == other.expiredTime &&
                this.isRunning == other.isRunning &&
                this.isAnimating == other.isAnimating &&
                this.shouldNotify == other.shouldNotify;
    }

    public String toString() {
        return "name: " + name + " defaultTime: " + defaultTime + " expiredTime: " + expiredTime + " isRunning: " + isRunning;
    }
}
