package io.dovid.multitimer.utilities;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

/**
 * Author: Umberto D'Ovidio
 * Date: 16/09/17
 * Email: umberto.dovidio@gmail.com
 * Website: http://dovid.io
 * Tutorial link : http://dovid.io
 */

public class RingtonePlayer {

    private static final String TAG = "RINGTONEPLAYER";

    private static Ringtone r;

    public static void playRingtone(final Context context, String ringtone) {
        stopPlaying();
        Uri uri = Uri.parse(ringtone);
        r = RingtoneManager.getRingtone(context, uri);
        r.play();
        stopAfter(20000);
    }


    public static void playDefaultAlarm(final Context context) {
        stopPlaying();
        Uri alarm = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM);
        r = RingtoneManager.getRingtone(context, alarm);
        r.play();
        stopAfter(20000);
    }

    public static void stopPlaying() {
        if (r != null) {
            Log.d(TAG, "stopPlaying");
            r.stop();
        }
    }

    private static void stopAfter(long milliseconds) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopPlaying();
            }
        }, milliseconds);
    }
}
