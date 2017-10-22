package io.dovid.multitimer.ui

import android.app.AlertDialog
import android.app.DialogFragment
import android.content.*
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import io.dovid.multitimer.BuildConfig
import io.dovid.multitimer.R
import io.dovid.multitimer.database.DatabaseHelper
import io.dovid.multitimer.model.TimerDAO
import io.dovid.multitimer.ui.preferences.PreferenceActivity
import io.dovid.multitimer.utilities.AppRater
import io.dovid.multitimer.utilities.RingtonePlayer
import io.dovid.multitimer.utilities.VibrationPlayer
import tourguide.tourguide.Overlay
import tourguide.tourguide.Pointer
import tourguide.tourguide.ToolTip
import tourguide.tourguide.TourGuide

class MainActivity : AppCompatActivity(), CreateTimerDialog.TimerCreateDialogListener, TimerUpdateDialog.TimerUpdateDialogListener {

    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: TimersAdapter? = null
    private var fab: FloatingActionButton? = null
    private var mTourGuideHandler: TourGuide? = null
    lateinit var colors: IntArray
    private lateinit var sharedPreferences: SharedPreferences

    private var toolbar: Toolbar? = null

    private var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mAdapter?.refreshTimers()
            Log.d(TAG, "refreshing timers")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // setup recycler view holding timers
        mRecyclerView = findViewById(R.id.recyclerView) as RecyclerView
        val manager = LinearLayoutManager(this)
        mRecyclerView?.layoutManager = manager
        mAdapter = TimersAdapter(this)
        mRecyclerView?.adapter = mAdapter

        setupColors()
        AppRater.app_launched(this)

        if (!sharedPreferences.contains(TutorialStep.POINT_TO_FAB.toString())) {
            loadTutorial(step = TutorialStep.POINT_TO_FAB, target = fab)
        }

        fab = findViewById(R.id.fab) as FloatingActionButton

        fab?.setOnClickListener {
            mTourGuideHandler?.cleanUp()
            sharedPreferences.edit().putBoolean("tutorial1", true).apply()

            if (mAdapter?.itemCount ?: 0 < BuildConfig.MAX_TIMERS) {
                val createDialog = CreateTimerDialog.getInstance()
                createDialog.show(fragmentManager, "createTimer")
            } else {
                val builder = AlertDialog.Builder(this@MainActivity).setTitle(getString(R.string.main_max_number_of_timers))

                builder.setMessage(getString(R.string.main_delete_before_adding_timer))

                builder.setPositiveButton(getString(android.R.string.ok), DialogInterface.OnClickListener { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }).create().show()
            }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_paid, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {

            R.id.action_settings -> {
                val i = Intent(this, PreferenceActivity::class.java)
                startActivity(i)
            }

            R.id.action_about -> {
                AboutDialog.getInstance().show(fragmentManager, "AboutDialog")
            }

            R.id.action_play_tutorial -> {
                sharedPreferences.edit()
                        .remove(TutorialStep.POINT_TO_FAB.toString())
                        .remove(TutorialStep.POINT_TO_TIMER.toString())
                        .commit()
                loadTutorial(step = TutorialStep.POINT_TO_FAB, target = fab)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun setupColors() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val colorTheme = preferences.getString(getString(R.string.preference_color_scheme), "0")

        if (colorTheme == MATERIAL_THEME) {
            fab?.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
            colors = intArrayOf(R.color.material_card1,
                    R.color.material_card2,
                    R.color.material_card3,
                    R.color.material_card4,
                    R.color.material_card5,
                    R.color.material_card6,
                    R.color.material_card7,
                    R.color.material_card8,
                    R.color.material_card9,
                    R.color.material_card10)
        } else if (colorTheme == DARK_THEME) {
            fab?.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.primary_dark_material_dark))
            colors = intArrayOf(R.color.dark_card)
        } else {
            fab?.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.bakery_card2))
            colors = intArrayOf(
                    R.color.bakery_card1,
                    R.color.bakery_card2,
                    R.color.bakery_card3,
                    R.color.bakery_card4,
                    R.color.bakery_card5)
        }
    }

    private fun loadTutorial(step: TutorialStep, target: View?) {
        if (target != null) {
            if (step.equals(TutorialStep.POINT_TO_FAB)) {
                mTourGuideHandler = TourGuide.init(this).with(TourGuide.Technique.Click)
                        .setPointer(Pointer().setGravity(Gravity.TOP))
                        .setToolTip(ToolTip().setTitle(getString(R.string.welcome)).
                                setDescription(getString(R.string.click_on_this_button)).setGravity(Gravity.TOP))
                        .setOverlay(Overlay().setOnClickListener {
                            mTourGuideHandler?.cleanUp()
                            sharedPreferences.edit()
                                    .putBoolean(TutorialStep.POINT_TO_FAB.toString(), true)
                                    .apply()
                        })
                        .playOn(target)
            } else if (step.equals(TutorialStep.POINT_TO_TIMER)) {
                mTourGuideHandler = TourGuide.init(this).with(TourGuide.Technique.VerticalDownward)
                        .setToolTip(ToolTip()
                                .setDescription(getString(R.string.long_press_timer)).setGravity(Gravity.CENTER).setOnClickListener {
                            mTourGuideHandler?.cleanUp()
                            sharedPreferences.edit()
                                    .putBoolean(TutorialStep.POINT_TO_TIMER.toString(), true)
                                    .apply()
                        })
                        .setOverlay(Overlay().setOnClickListener {
                            mTourGuideHandler?.cleanUp()
                        }.setStyle(Overlay.Style.Rectangle))
                        .playOn(target)
            } else {
                throw RuntimeException("Do not have a tutorial step number equals to " + step)
            }
        }
    }

    // TimerCreateDialogListener implementation
    override fun onCreateTimer(name: String, time: Long, dialog: DialogFragment) {

        TimerDAO.create(DatabaseHelper.getInstance(this), name, time, false, true)

        mAdapter?.insertTimer()
        dialog.dismiss()

        if (!sharedPreferences.contains(TutorialStep.POINT_TO_TIMER.toString())) {
            Handler().postDelayed({
                loadTutorial(step = TutorialStep.POINT_TO_TIMER, target = mRecyclerView?.getChildAt(0))
            }, 1000)
        }

        // in case user does not find easy to clear tutorial
        Handler().postDelayed({
            mTourGuideHandler?.cleanUp()
        }, 6000)

    }

    // TimerUpdateDialogListener implementation
    override fun onUpdate(name: String, defaultTime: Long, timerId: Int, dialog: DialogFragment) {
        dialog.dismiss()
        TimerDAO.updateTimer(DatabaseHelper.getInstance(this), timerId, name, defaultTime, defaultTime)
        mAdapter?.refreshTimers()
    }

    override fun onResume() {
        super.onResume()
        setupColors()
        mAdapter?.setColors(colors)
        registerReceiver(receiver, IntentFilter(BuildConfig.UPDATE_TIMERS))
        RingtonePlayer.stopPlaying()
        VibrationPlayer.stopVibrating()
    }

    override fun onStop() {
        super.onStop()
        try {
            unregisterReceiver(receiver)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "MainActivity IllegalArgumentException", e);
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    companion object {
        private val TAG = "MAINACTIVITY"
        private val MATERIAL_THEME = "0"
        private val DARK_THEME = "1"
    }

    private enum class TutorialStep {
        POINT_TO_FAB, POINT_TO_TIMER
    }
}
