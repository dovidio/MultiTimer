package io.dovid.multitimer.ui

import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import io.dovid.multitimer.BuildConfig
import io.dovid.multitimer.R
import io.dovid.multitimer.database.DatabaseHelper
import io.dovid.multitimer.model.TimerDAO
import io.dovid.multitimer.utilities.TimerAlarmManager


class TimesUpActivity : AppCompatActivity() {
    private var r: Ringtone? = null
    private var v: Vibrator? = null

    private val MATERIAL_THEME = "0"
    private val DARK_THEME = "1"
    private val BAKERY_THEME = "2"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // wake lock
        val win = window
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        setContentView(R.layout.activity_times_up)

        // get timer info
        val intent = intent
        val timerName = intent.extras.getString(BuildConfig.EXTRA_TIMER_NAME)
        val timerId = intent.extras.getInt(BuildConfig.EXTRA_TIMER_ID)

        // stop this timer and setup other alarms
        updateExpiredTimer(timerId)

        val timesUpTV = findViewById(R.id.textViewTimesUp) as TextView
        timesUpTV.text = timerName + " " + getString(R.string.time_up)

        val backToTimers = findViewById(R.id.times_up_back_to_timers_button) as Button

        backToTimers.setOnClickListener {
            TimerAlarmManager.setupAlarms(this)
            val i = Intent(this@TimesUpActivity, MainActivity::class.java)
            startActivity(i)
            finish()
        }

        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 500 //You can manage the blinking time with this parameter
        anim.startOffset = 20
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = Animation.INFINITE

        val timeTV = findViewById(R.id.times_up_zero_time_tv) as TextView

        timeTV.animation = anim


        playAlarmRingtone()
        setupColors()
    }

    private fun playAlarmRingtone() {
        if (BuildConfig.PAID) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(this)
            val ringtone = preferences.getString("notifications_new_message_ringtone", null)
            val vibrate = preferences.getBoolean("notifications_new_message_vibrate", false)

            if (vibrate) {
                Log.d(TAG, "playAlarmRingtone: vibrating")
                v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val timings = longArrayOf(1000, 1000)
                    val amplitudes = intArrayOf(255, 0)
                    v!!.vibrate(VibrationEffect.createWaveform(timings, amplitudes, 0))
                } else {
                    val pattern = longArrayOf(0, 1000, 1000)
                    v!!.vibrate(pattern, 0)
                }
            }

            if (ringtone != null) {
                val uri = Uri.parse(ringtone)
                r = RingtoneManager.getRingtone(applicationContext, uri)
                r!!.play()
            }
        } else {
            val alarm = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM)
            r = RingtoneManager.getRingtone(applicationContext, alarm)
            r!!.play()
        }
    }

    private fun setupColors() {
        if (BuildConfig.PAID) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            val colorTheme = sharedPreferences.getString("color_theme_list", "0")

            val rl = findViewById(R.id.times_up_rl) as RelativeLayout

            if (colorTheme == MATERIAL_THEME) {
                rl.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
            } else if (colorTheme == DARK_THEME) {
                rl.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_card))
            } else {
                rl.setBackgroundColor(ContextCompat.getColor(this, R.color.bakery_card2))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (r != null && r!!.isPlaying) {
            r!!.stop()
        }
        if (v != null) {
            v!!.cancel()
        }
    }

    override fun onStop() {
        Log.d(TAG, "onStop: called")
        super.onStop()
    }

    private fun updateExpiredTimer(timerId: Int) {
        var databaseHelper: DatabaseHelper? = null
        try {
            databaseHelper = DatabaseHelper.getInstance(this)
            val defaultTime = TimerDAO.getProperty(databaseHelper, "DEFAULT_TIME", timerId) as Long
            TimerDAO.updateTimerRunning(databaseHelper, timerId, false)
            TimerDAO.updateTimerExpiredTime(databaseHelper, timerId, defaultTime)
        } finally {
            databaseHelper?.close()
        }
    }

    companion object {

        private val TAG = "TIMESUPACTIVITY"
    }


}
