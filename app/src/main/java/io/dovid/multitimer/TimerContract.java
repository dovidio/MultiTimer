package io.dovid.multitimer;

import android.provider.BaseColumns;

/**
 * Author: Umberto D'Ovidio
 * Date: 25/08/17
 * Email: umberto.dovidio@gmail.com
 * Website: http://dovid.io
 * Tutorial link : http://dovid.io
 */

public final class TimerContract {

    private TimerContract() {}

    public static class Timer implements BaseColumns {
        public static final String ID = "ID";
        public static final String NAME = "NAME";
        public static final String DEFAULT_TIME = "DEFAULT_TIME";
        public static final String EXPIRED_TIME = "EXPIRED_TIME";
        public static final String IS_RUNNING = "IS_RUNNING";
    }

    public static class TimerCollection implements BaseColumns {
        public static final String ID = "ID";
        public static final String NAME = "NAME";
    }

    public static class TimerTimerCollection implements BaseColumns {
        public static final String ID = "ID";
        public static final String TIMER_ID = "TIMER_ID";
        public static final String TIMER_COLLECTION_ID = "TIMER_COLLECTION_ID";
    }
}
