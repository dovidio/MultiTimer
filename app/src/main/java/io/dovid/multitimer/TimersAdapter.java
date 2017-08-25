package io.dovid.multitimer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.apache.commons.lang.time.DurationFormatUtils;

import java.util.ArrayList;

import io.dovid.multitimer.model.CustomTimer;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Author: Umberto D'Ovidio
 * Date: 19/08/17
 * Email: umberto.dovidio@gmail.com
 * Website: http://dovid.io
 * Tutorial link : http://dovid.io
 */

class TimersAdapter extends RecyclerView.Adapter<TimersAdapter.CustomViewHolder> {

    private static final String TAG = "CUSTOMADAPTER";
    public static final String ITALIANTIME = "HH:mm:ss";
    private static final byte MAX_NUMBER_TIMERS = 10;

    private ArrayList<CustomTimer> timers;
    private Context mContext;


    private int[] colors = new int[] {
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
        mContext = context;
        timers = new ArrayList<>();
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.timer, parent, false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return timers.size();
    }

    public void addTimer() {
        if (timers.size() >= MAX_NUMBER_TIMERS) {
            new AlertDialog.Builder(mContext)
                    .setTitle(R.string.maximum_number_timers_title)
                    .setMessage(R.string.should_delete_timer_first)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
        } else {
            CustomTimer e = new CustomTimer(mContext.getString(R.string.pasta), 0, 0, false);
            timers.add(e);
            notifyDataSetChanged();
            Log.d(TAG, "List size: " + timers.size());
        }
    }

    public void saveTimer(int position, long time) {
        timers.get(position).setCountdownTime(time);
    }

    public void removeTimer() {
        timers.remove(timers.size() - 1);
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView countdownTV;
        private CountDownTimer ct;

        public CustomViewHolder(final View itemView) {
            super(itemView);
        }

        private int[] parseCountdown(String s) {
            int hours = Integer.parseInt(s.substring(0, 2));
            int minutes = Integer.parseInt(s.substring(3, 5));
            int seconds = Integer.parseInt(s.substring(6));

            return new int[]{
                    hours, minutes, seconds
            };
        }

        private long countdownToMilliseconds(String s) {
            int[] v = parseCountdown(s);
            return ((v[0] * 60 * 60) + (v[1] * 60) + v[2]) * 1000;
        }

        private void startCountdown(final String time, final TextView countdownRunning, boolean shouldChangeRunningCounter, final int position) {

            long countdown = countdownToMilliseconds(time);

            Log.d(TAG, "passed time: " + time);
            Log.d(TAG, "computer time: " + countdown);

            itemView.findViewById(R.id.timer_setup).setVisibility(View.GONE);
            itemView.findViewById(R.id.timer_running).setVisibility(View.VISIBLE);

            timers.get(position).setCountdownTime(countdown);

            if (shouldChangeRunningCounter) {
                countdownRunning.setText(DurationFormatUtils.formatDuration(countdown, ITALIANTIME));
            }

            ct = new CountDownTimer(countdown, 1000) {

                @Override
                public void onTick(long l) {
                    countdownRunning.setText(DurationFormatUtils.formatDuration(l, ITALIANTIME));
                    timers.get(position).setCountdownTimeRunning(l);
                }

                @Override
                public void onFinish() {
                    boolean wantsToBeNotified = itemView.findViewById(R.id.switchNotify).isActivated();

                    if (wantsToBeNotified) {
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(itemView.getContext())
                                        .setSmallIcon(R.mipmap.ic_launcher_round)
                                        .setContentTitle("Countdown finished")
                                        .setContentText("finished countdown")
                                        .setSound(notification);

                        NotificationManager mNotifyMgr =
                                (NotificationManager) itemView.getContext().getSystemService(NOTIFICATION_SERVICE);

                        mNotifyMgr.notify(001, mBuilder.build());
                    }

                    itemView.findViewById(R.id.timer_running).setVisibility(View.GONE);
                    itemView.findViewById(R.id.timer_setup).setVisibility(View.VISIBLE);
                }
            }.start();
            timers.get(position).setCurrentlyRunning(true);
        }

        public void bind(final int position) {

            countdownTV = itemView.findViewById(R.id.textViewCountdown);
            final ImageButton playButton = itemView.findViewById(R.id.buttonPlay);
            final TextView countdownRunning = itemView.findViewById(R.id.editTextCountdownRunning);

            itemView.findViewById(R.id.editTextTimer).setBackgroundResource(colors[position]);
            countdownRunning.setBackgroundResource(colors[position]);

            // was running in the past, so make it run again with the last countdownvalue registered
            if (timers.get(position).isCurrentlyRunning()) {
                startCountdown(DurationFormatUtils.formatDuration(timers.get(position).getCountdownTimeRunning(), ITALIANTIME),
                            countdownRunning, true, position);
            } else {
                Log.d(TAG, "we are setting time: " + DurationFormatUtils.formatDuration(timers.get(position).getCountdownTime(), ITALIANTIME));
                countdownTV.setText(DurationFormatUtils.formatDuration(timers.get(position).getCountdownTime(), ITALIANTIME));
            }

            countdownTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int[] countdownValues = parseCountdown(countdownTV.getText().toString());
                    DialogFragment setupDialog = TimerSetupDialog.getInstance(
                            mContext.getString(R.string.Setup_your_timer),
                            countdownValues,
                            countdownTV, position);
                    setupDialog.show(((Activity) mContext).getFragmentManager(), "create tag");
                }
            });


            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startCountdown(countdownTV.getText().toString(), countdownRunning, true, position);
                }
            });

            final ImageButton pause = itemView.findViewById(R.id.buttonPause);
            pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (timers.get(position).isCurrentlyRunning()) {
                        if (ct != null) {
                            ct.cancel();
                        }
                        pause.setImageResource(R.drawable.play_icon);
                        timers.get(position).setCurrentlyRunning(false);
                    } else {
                        startCountdown(countdownRunning.getText().toString(), countdownRunning, false, position);
                        pause.setImageResource(R.drawable.pause_icon);
                    }
                }
            });

            final Button resetButton = itemView.findViewById(R.id.buttonReset);
            resetButton.setTextColor(ContextCompat.getColor(mContext, colors[position]));
            resetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemView.findViewById(R.id.timer_running).setVisibility(View.GONE);
                    itemView.findViewById(R.id.timer_setup).setVisibility(View.VISIBLE);
                    countdownTV.setText(DurationFormatUtils.formatDuration(timers.get(position).getCountdownTime(), ITALIANTIME));
                    if (ct != null) {
                        ct.cancel();
                    }
                    timers.get(position).setCurrentlyRunning(false);
                }
            });
        }
    }
}
