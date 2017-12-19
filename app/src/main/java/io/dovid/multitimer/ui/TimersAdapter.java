package io.dovid.multitimer.ui;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import io.dovid.multitimer.BuildConfig;
import io.dovid.multitimer.R;
import io.dovid.multitimer.database.DatabaseHelper;
import io.dovid.multitimer.model.TimerDAO;
import io.dovid.multitimer.model.TimerEntity;
import io.dovid.multitimer.utilities.TimerAlarmManager;
import io.dovid.multitimer.utilities.TimerRunner;


/**
 * Author: Umberto D'Ovidio
 * Date: 19/08/17
 * Email: umberto.dovidio@gmail.com
 * Website: http://dovid.io
 * Tutorial link : http://dovid.io
 */

public class TimersAdapter extends RecyclerView.Adapter<TimersAdapter.TimerViewHolder> {

    private ArrayList<TimerEntity> timers;
    private Context context;
    private DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
    private int[] colors;
    private static final String TAG = "CUSTOMADAPTER";


    public TimersAdapter(Context context) {
        this.context = context;
        timers = TimerDAO.getTimers(databaseHelper);
        TimerRunner.getInstance().run(context);
    }

    void setColors(int[] colors) {
        this.colors = colors;
        notifyDataSetChanged();
    }

    void refreshTimers() {
        timers = TimerDAO.getTimers(databaseHelper);
        notifyDataSetChanged();
    }

    void insertTimer() {
        timers = TimerDAO.getTimers(databaseHelper);
        notifyItemInserted(timers.size());
    }

    private void deleteTimer(int position) {
        TimerDAO.deleteTimer(databaseHelper, timers.get(position).getId());
        notifyItemRemoved(position);
    }

    void showUpdateTimerDialog(int position) {
        TimerUpdateDialog setupDialog = TimerUpdateDialog.getInstance(
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

        public TimerViewHolder(View itemView) {
            super(itemView);
        }

        void bind(int position) {
            if (!timers.get(position).isAnimating()) {
                if (timers.get(position).isRunning() || timers.get(position).getDefaultTime() != timers.get(position).getExpiredTime()) {
                    setupPlayView(position);
                } else {
                    setupPauseView(position);
                }
            } else {
                Log.d(TAG, "not binding");
            }
        }

        private void setupPauseView(final int position) {

            itemView.findViewById(R.id.pauseTimer).setVisibility(View.VISIBLE);
            itemView.findViewById(R.id.playTimer).setVisibility(View.GONE);

            TextView timerNameTV = itemView.findViewById(R.id.textViewTimerName);
            TextView defaultTimeTV = itemView.findViewById(R.id.textViewDefaultTime);

            final TimerEntity timer = timers.get(position);

            timerNameTV.setText(timer.getName());
            timerNameTV.setBackgroundColor(ContextCompat.getColor(context, colors[timer.getId() % colors.length]));

            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
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

            defaultTimeTV.setText(DurationFormatUtils.formatDuration(timer.getDefaultTime(), BuildConfig.ITALIANTIME));
            ImageButton playButton = itemView.findViewById(R.id.buttonPlay);


            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playAnimation(timer, true);
                    TimerDAO.updateTimerPlayTimestamp(databaseHelper, timer.getId(), System.currentTimeMillis());
                    TimerDAO.updateTimerRunning(databaseHelper, timer.getId(), true);
                    refreshTimers();
                    TimerAlarmManager.setupAlarms(context, timers);
                }
            });

            Switch switchButton = itemView.findViewById(R.id.switchNotify);
            switchButton.setChecked(timer.shouldNotify());

            switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    TimerDAO.updateTimerShouldNotify(databaseHelper, timer.getId(), b);
                }
            });

        }

        private void setupPlayView(int position) {

            final TimerEntity timer = timers.get(position);

            itemView.findViewById(R.id.pauseTimer).setVisibility(View.GONE);
            itemView.findViewById(R.id.playTimer).setVisibility(View.VISIBLE);

            final ImageButton pause = itemView.findViewById(R.id.buttonPause);
            TextView countdownRunning = itemView.findViewById(R.id.editTextCountdownRunning);

            TextView timerName = itemView.findViewById(R.id.textViewTimerNameRunning);
            timerName.setText(timer.getName());

            if (timer.getExpiredTime() >= 0) {
                countdownRunning.setText(DurationFormatUtils.formatDuration(timer.getExpiredTime(), BuildConfig.ITALIANTIME));
            } else {
                countdownRunning.setText("00:00:00");
            }

            countdownRunning.setBackgroundResource(colors[timer.getId() % colors.length]);

            Button resetButton = itemView.findViewById(R.id.buttonReset);

            resetButton.setTextColor(ContextCompat.getColor(context, colors[timer.getId() % colors.length]));

            resetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playAnimation(timer, true);
                    TimerDAO.updateTimerRunning(databaseHelper, timer.getId(), false);
                    TimerDAO.updateTimerExpiredTime(databaseHelper, timer.getId(), timer.getDefaultTime());
                    TimerDAO.putPlayTimeStampNull(databaseHelper, timer.getId());
                    refreshTimers();
                    TimerAlarmManager.setupAlarms(context, timers);
                }
            });

            pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (timer.isRunning()) {
                        pause.setImageResource(R.drawable.play_icon);
                        TimerDAO.putPlayTimeStampNull(databaseHelper, timer.getId());
                    } else {
                        pause.setImageResource(R.drawable.pause_icon);
                        TimerDAO.updateTimerPlayTimestamp(databaseHelper, timer.getId(), System.currentTimeMillis() - (timer.getDefaultTime() - timer.getExpiredTime()));
                    }
                    TimerDAO.updateTimerRunning(databaseHelper, timer.getId(), !timer.isRunning());
                    refreshTimers();
                    TimerAlarmManager.setupAlarms(context, timers);
                }
            });


            if (timer.getExpiredTime() != timer.getDefaultTime()) {
                if (!timer.isRunning()) {
                    pause.setImageResource(R.drawable.play_icon);
                }
            }
        }

        private void setupPauseColors(TimerEntity timer) {
            TextView timerNameTV = itemView.findViewById(R.id.textViewTimerName);
            timerNameTV.setText(timer.getName());
            timerNameTV.setBackgroundColor(ContextCompat.getColor(context, colors[timer.getId() % colors.length]));
        }

        private void setupPlayColors(TimerEntity timer) {
            TextView timerName = itemView.findViewById(R.id.textViewTimerNameRunning);
            timerName.setText(timer.getName());

            TextView countdownRunning = itemView.findViewById(R.id.editTextCountdownRunning);
            countdownRunning.setText(DurationFormatUtils.formatDuration(timer.getExpiredTime() - 1000, BuildConfig.ITALIANTIME));
            countdownRunning.setBackgroundResource(colors[timer.getId() % colors.length]);

            Button resetButton = itemView.findViewById(R.id.buttonReset);
            resetButton.setTextColor(ContextCompat.getColor(context, colors[timer.getId() % colors.length]));
        }

        private void playAnimation(final TimerEntity timer, boolean willPlay) {
            timer.setAnimating(true);
            TimerDAO.updateIsAnimating(databaseHelper, timer.getId(), true);
            AnimatorSet animatorSetCardOut;
            AnimatorSet animatorSetCardIn;
            if (willPlay) {
                setupPlayColors(timer);

                itemView.findViewById(R.id.playTimer).setVisibility(View.VISIBLE);
                itemView.findViewById(R.id.playTimer).setAlpha(0f);

                animatorSetCardOut = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.flip_left_out);
                animatorSetCardOut.setTarget(itemView.findViewById(R.id.pauseTimer));
                animatorSetCardIn = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.flip_left_in);
                animatorSetCardIn.setTarget(itemView.findViewById(R.id.playTimer));
            } else {
                setupPauseColors(timer);

                itemView.findViewById(R.id.pauseTimer).setVisibility(View.VISIBLE);
                itemView.findViewById(R.id.pauseTimer).setAlpha(0f);

                animatorSetCardOut = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.flip_right_out);
                animatorSetCardOut.setTarget(itemView.findViewById(R.id.playTimer));
                animatorSetCardIn = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.flip_right_in);
                animatorSetCardIn.setTarget(itemView.findViewById(R.id.pauseTimer));
            }

            AnimatorSet allAnimatorSet = new AnimatorSet();
            allAnimatorSet.playTogether(animatorSetCardOut, animatorSetCardIn);
            allAnimatorSet.setDuration(500);

            allAnimatorSet.addListener(new MyAnimationListenerAdapter(new onAnimationStopDoneListener() {
                @Override
                public void onAnimationStopDone() {
                    Log.d(TAG, "stopping animation");
                    TimerDAO.updateIsAnimating(databaseHelper, timer.getId(), false);
                    refreshTimers();
                }
            }));
        }
    }

    interface onAnimationStopDoneListener {
        void onAnimationStopDone();
    }

    private class MyAnimationListenerAdapter extends AnimatorListenerAdapter {

        onAnimationStopDoneListener delegate;

        public MyAnimationListenerAdapter(onAnimationStopDoneListener delegate) {
            this.delegate = delegate;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            delegate.onAnimationStopDone();
        }
    }


}
