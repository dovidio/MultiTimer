package io.dovid.multitimer.utilities;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

/**
 * Author: Umberto D'Ovidio
 * Date: 26/08/17
 * Email: umberto.dovidio@gmail.com
 * Website: http://dovid.io
 * Tutorial link : http://dovid.io
 */

public class Converter {

    public static long hmsToMilliseconds(final long hours, final long minutes, final long seconds) {
        return TimeUnit.HOURS.toMillis(hours) +
                TimeUnit.MINUTES.toMillis(minutes) +
                TimeUnit.SECONDS.toMillis(seconds);

    }
}
