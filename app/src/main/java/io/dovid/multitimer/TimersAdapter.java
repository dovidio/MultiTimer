package io.dovid.multitimer;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobWorkItem;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import org.apache.commons.lang.time.DurationFormatUtils;

import java.util.ArrayList;
import java.util.List;

import io.dovid.multitimer.database.DatabaseHelper;
import io.dovid.multitimer.database.TimerContract;
import io.dovid.multitimer.model.TimerDAO;
import io.dovid.multitimer.model.TimerEntity;
import io.dovid.multitimer.utilities.TimerRunner;

/**
 * Author: Umberto D'Ovidio
 * Date: 19/08/17
 * Email: umberto.dovidio@gmail.com
 * Website: http://dovid.io
 * Tutorial link : http://dovid.io
 */

class TimersAdapter extends RecyclerView.Adapter<TimersAdapter.TimerViewHolder> {

    private static final String TAG = "CUSTOMADAPTER";
    private static final int TIMER_JOB = 921;
    private ArrayList<TimerEntity> timers;
    private Context context;
    private DatabaseHelper databaseHelper;

    private int[] colors = new int[]{
            R.color.card1,
            R.color.card2,
            R.color.card3,
            R.color.card4,
            R.color.card5,
            R.color.card6,
            R.color.card7,
            R.color.card8,
            R.color.card9,
            R.color.card10
    };

    public TimersAdapter(final Context context) {
        this.context = context;
        databaseHelper = DatabaseHelper.getInstance(context);
        timers = TimerDAO.getTimers(databaseHelper);

        TimerRunner.run(context);


        JobInfo jobInfo = new JobInfo.Builder(TIMER_JOB, new ComponentName(context, TimerRunner.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                .setPeriodic(1000)
                .setPersisted(true)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .build();
    }

    public void refreshTimers() {
        timers = TimerDAO.getTimers(databaseHelper);
        notifyDataSetChanged();
    }


    private void deleteTimer(int position) {
        TimerDAO.deleteTimer(databaseHelper, timers.get(position).getId());
        refreshTimers();
    }

    private void showUpdateTimerDialog(int position) {
        DialogFragment setupDialog = TimerUpdateDialog.getInstance(
                databaseHelper,
                timers.get(position).getId());
        setupDialog.show(((Activity) context).getFragmentManager(), "create tag");
    }

    @Override
    public TimerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.timer, parent, false);
        return new TimerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TimerViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return timers.size();
    }


    public class TimerViewHolder extends RecyclerView.ViewHolder {

        public TimerViewHolder(final View itemView) {
            super(itemView);
        }

        public void bind(final int position) {
            if (timers.get(position).isRunning() || timers.get(position).getDefaultTime() != timers.get(position).getExpiredTime()) {
                setupPlayView(position);
            } else {
                setupPauseView(position);
            }
        }

        private void setupPauseView(final int position) {

            itemView.findViewById(R.id.pauseTimer).setVisibility(View.VISIBLE);
            itemView.findViewById(R.id.playTimer).setVisibility(View.GONE);

            TextView timerNameTV = itemView.findViewById(R.id.textViewTimerName);
            TextView defaultTimeTV = itemView.findViewById(R.id.textViewDefaultTime);

            timerNameTV.setText(timers.get(position).getName());
            timerNameTV.setBackgroundColor(ContextCompat.getColor(context, colors[position]));
            timerNameTV.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                    contextMenu.setHeaderTitle(R.string.what_to_do);
                    contextMenu.add(0, view.getId(), 0, R.string.delete).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            deleteTimer(position);
                            return true;
                        }
                    });

                    contextMenu.add(0, view.getId(), 0, R.string.update).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            showUpdateTimerDialog(position);
                            return true;
                        }
                    });
                }
            });

            defaultTimeTV.setText(DurationFormatUtils.formatDuration(timers.get(position).getDefaultTime(), BuildConfig.ITALIANTIME));
            ImageButton playButton = itemView.findViewById(R.id.buttonPlay);
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimerDAO.updateTimerPlayTimestamp(databaseHelper, timers.get(position).getId(), new java.util.Date().getTime());
                    TimerDAO.updateTimerRunning(databaseHelper, timers.get(position).getId(), true);
                }
            });

            Switch switchButton = itemView.findViewById(R.id.switchNotify);
            switchButton.setChecked(timers.get(position).shouldNotify());

            switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    TimerDAO.updateTimerShouldNotify(databaseHelper, timers.get(position).getId(), b);
                }
            });
        }

        private void setupPlayView(final int position) {

            itemView.findViewById(R.id.pauseTimer).setVisibility(View.GONE);
            itemView.findViewById(R.id.playTimer).setVisibility(View.VISIBLE);

            final ImageButton pause = itemView.findViewById(R.id.buttonPause);
            final TextView countdownRunning = itemView.findViewById(R.id.editTextCountdownRunning);

            countdownRunning.setText(DurationFormatUtils.formatDuration(timers.get(position).getExpiredTime(), BuildConfig.ITALIANTIME));
            countdownRunning.setBackgroundResource(colors[position]);

            final Button resetButton = itemView.findViewById(R.id.buttonReset);
            resetButton.setTextColor(ContextCompat.getColor(context, colors[position]));

            resetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemView.findViewById(R.id.playTimer).setVisibility(View.GONE);
                    itemView.findViewById(R.id.pauseTimer).setVisibility(View.VISIBLE);
                    TimerDAO.updateTimerRunning(databaseHelper, timers.get(position).getId(), false);
                    TimerDAO.updateTimerExpiredTime(databaseHelper, timers.get(position).getId(), timers.get(position).getDefaultTime());
                    TimerDAO.putPlayTimeStampNull(databaseHelper, timers.get(position).getId());
                    refreshTimers();
                }
            });

            pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (timers.get(position).isRunning()) {
                        pause.setImageResource(R.drawable.play_icon);
                        TimerDAO.putPlayTimeStampNull(databaseHelper, timers.get(position).getId());
                    } else {
                        pause.setImageResource(R.drawable.pause_icon);
                        TimerDAO.updateTimerPlayTimestamp(databaseHelper, timers.get(position).getId(), new java.util.Date().getTime());
                    }
                    TimerDAO.updateTimerRunning(databaseHelper, timers.get(position).getId(), !timers.get(position).isRunning());
                    refreshTimers();
                }
            });

            if (timers.get(position).getExpiredTime() != timers.get(position).getDefaultTime()) {
                if (!timers.get(position).isRunning()) {
                    pause.setImageResource(R.drawable.play_icon);
                }

            }
        }
    }
}
