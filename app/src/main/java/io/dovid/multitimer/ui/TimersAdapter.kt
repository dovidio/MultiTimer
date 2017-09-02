package io.dovid.multitimer.ui

import android.app.Activity
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import io.dovid.multitimer.BuildConfig
import io.dovid.multitimer.R
import io.dovid.multitimer.database.DatabaseHelper
import io.dovid.multitimer.model.TimerDAO
import io.dovid.multitimer.model.TimerEntity
import io.dovid.multitimer.utilities.TimerAlarmManager
import io.dovid.multitimer.utilities.TimerRunner
import org.apache.commons.lang.time.DurationFormatUtils
import java.util.*

/**
 * Author: Umberto D'Ovidio
 * Date: 19/08/17
 * Email: umberto.dovidio@gmail.com
 * Website: http://dovid.io
 * Tutorial link : http://dovid.io
 */

internal class TimersAdapter(private val context: Context) : RecyclerView.Adapter<TimersAdapter.TimerViewHolder>() {
    private var timers: ArrayList<TimerEntity>? = null
    private val databaseHelper: DatabaseHelper
    private lateinit var colors: IntArray

    init {
        databaseHelper = DatabaseHelper.getInstance(context)
        timers = TimerDAO.getTimers(databaseHelper)

        TimerRunner.run(context)
    }

    fun setColors(colors: IntArray) {
        this.colors = colors
        notifyDataSetChanged()
    }

    fun refreshTimers() {
        timers = TimerDAO.getTimers(databaseHelper)
        notifyDataSetChanged()
    }

    private fun deleteTimer(position: Int) {
        TimerDAO.deleteTimer(databaseHelper, timers!![position].id)
        refreshTimers()
    }

    private fun showUpdateTimerDialog(position: Int) {
        val setupDialog = TimerUpdateDialog.getInstance(
                databaseHelper,
                timers!![position].id)
        setupDialog.show((context as Activity).fragmentManager, "create tag")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.timer, parent, false)
        return TimerViewHolder(v)
    }

    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return timers!!.size
    }


    inner class TimerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) {
            if (timers!![position].isRunning || timers!![position].defaultTime != timers!![position].expiredTime) {
                setupPlayView(position)
            } else {
                setupPauseView(position)
            }
        }

        private fun setupPauseView(position: Int) {

            itemView.findViewById<View>(R.id.pauseTimer).visibility = View.VISIBLE
            itemView.findViewById<View>(R.id.playTimer).visibility = View.GONE

            val timerNameTV = itemView.findViewById<TextView>(R.id.textViewTimerName)
            val defaultTimeTV = itemView.findViewById<TextView>(R.id.textViewDefaultTime)

            timerNameTV.text = timers!![position].name
            timerNameTV.setBackgroundColor(ContextCompat.getColor(context, colors[position % colors.size]))
            timerNameTV.setOnCreateContextMenuListener { contextMenu, view, contextMenuInfo ->
                contextMenu.setHeaderTitle(R.string.what_to_do)
                contextMenu.add(0, view.id, 0, R.string.delete).setOnMenuItemClickListener {
                    deleteTimer(position)
                    true
                }

                contextMenu.add(0, view.id, 0, R.string.update).setOnMenuItemClickListener {
                    showUpdateTimerDialog(position)
                    true
                }
            }

            defaultTimeTV.text = DurationFormatUtils.formatDuration(timers!![position].defaultTime, BuildConfig.ITALIANTIME)
            val playButton = itemView.findViewById<ImageButton>(R.id.buttonPlay)
            playButton.setOnClickListener {
                TimerDAO.updateTimerPlayTimestamp(databaseHelper, timers!![position].id, java.util.Date().time)
                TimerDAO.updateTimerRunning(databaseHelper, timers!![position].id, true)
                refreshTimers()
                TimerAlarmManager.setupAlarms(context, timers!!)
            }

            val switchButton = itemView.findViewById<Switch>(R.id.switchNotify)
            switchButton.isChecked = timers!![position].shouldNotify()

            switchButton.setOnCheckedChangeListener { compoundButton, b -> TimerDAO.updateTimerShouldNotify(databaseHelper, timers!![position].id, b) }
        }

        private fun setupPlayView(position: Int) {

            itemView.findViewById<View>(R.id.pauseTimer).visibility = View.GONE
            itemView.findViewById<View>(R.id.playTimer).visibility = View.VISIBLE

            val pause = itemView.findViewById<ImageButton>(R.id.buttonPause)
            val countdownRunning = itemView.findViewById<TextView>(R.id.editTextCountdownRunning)

            countdownRunning.text = DurationFormatUtils.formatDuration(timers!![position].expiredTime, BuildConfig.ITALIANTIME)
            countdownRunning.setBackgroundResource(colors[position % colors.size])

            val resetButton = itemView.findViewById<Button>(R.id.buttonReset)
            resetButton.setTextColor(ContextCompat.getColor(context, colors[position % colors.size]))

            resetButton.setOnClickListener {
                itemView.findViewById<View>(R.id.playTimer).visibility = View.GONE
                itemView.findViewById<View>(R.id.pauseTimer).visibility = View.VISIBLE
                TimerDAO.updateTimerRunning(databaseHelper, timers!![position].id, false)
                TimerDAO.updateTimerExpiredTime(databaseHelper, timers!![position].id, timers!![position].defaultTime)
                TimerDAO.putPlayTimeStampNull(databaseHelper, timers!![position].id)
                refreshTimers()
                TimerAlarmManager.setupAlarms(context, timers!!)
            }

            pause.setOnClickListener {
                if (timers!![position].isRunning) {
                    pause.setImageResource(R.drawable.play_icon)
                    TimerDAO.putPlayTimeStampNull(databaseHelper, timers!![position].id)
                } else {
                    pause.setImageResource(R.drawable.pause_icon)
                    TimerDAO.updateTimerPlayTimestamp(databaseHelper, timers!![position].id, java.util.Date().time)
                }
                TimerDAO.updateTimerRunning(databaseHelper, timers!![position].id, !timers!![position].isRunning)
                refreshTimers()
                TimerAlarmManager.setupAlarms(context, timers!!)
            }

            if (timers!![position].expiredTime != timers!![position].defaultTime) {
                if (!timers!![position].isRunning) {
                    pause.setImageResource(R.drawable.play_icon)
                }
            }
        }
    }

    companion object {

        private val TAG = "CUSTOMADAPTER"
    }
}
