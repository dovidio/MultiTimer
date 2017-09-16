package io.dovid.multitimer.utilities;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

/**
 * Author: Umberto D'Ovidio
 * Date: 16/09/17
 * Email: umberto.dovidio@gmail.com
 * Website: http://dovid.io
 * Tutorial link : http://dovid.io
 */

public class VibrationPlayer {

    private static Vibrator v;

    public static void vibrate(final Context context) {
        stopVibrating();
        v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            long[] timings = {1000, 1000};
            int[] amplitudes = {255, 0};
            v.vibrate(VibrationEffect.createWaveform(timings, amplitudes, 0));
        } else {
            long[] pattern = {0, 1000, 1000};
            v.vibrate(pattern, 0);
        }
    }

    public static void stopVibrating() {
        if (v != null) {
            v.cancel();
        }
    }
}
