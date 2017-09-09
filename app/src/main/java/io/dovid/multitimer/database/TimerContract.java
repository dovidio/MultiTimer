package io.dovid.multitimer.database;

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
        public static final String TABLE_NAME = "TIMER";
        public static final String NAME = "NAME";
        public static final String DEFAULT_TIME = "DEFAULT_TIME";
        public static final String PlAY_STARTED_AT = "PlAY_STARTED_AT";
        public static final String EXPIRED_TIME = "EXPIRED_TIME";
        public static final String IS_RUNNING = "IS_RUNNING";
        public static final String SHOULD_NOTIFY = "SHOULD_NOTIFY";
        public static final String IS_ANIMATING = "IS_ANIMATING";
    }

    public static class TimerCollection implements BaseColumns {
        public static final String TABLE_NAME = "TIMER_COLLECTION";
        public static final String NAME = "NAME";
        public static final String QUANTITY = "QUANTITY";
    }

    public static class TimerTimerCollection implements BaseColumns {
        public static final String TABLE_NAME = "TIMER_TIMER_COLLECTION";
        public static final String TIMER_ID = "TIMER_ID";
        public static final String TIMER_COLLECTION_ID = "TIMER_COLLECTION_ID";
    }
}
